package org.elastos.hive.connection.auth;

import com.fasterxml.jackson.databind.ObjectMapper;

import org.elastos.did.DIDDocument;
import org.elastos.did.jwt.Claims;
import org.elastos.did.jwt.JwtParserBuilder;
import org.elastos.hive.connection.NodeRPCConnection;
import org.elastos.hive.connection.NodeRPCException;
import org.elastos.hive.exception.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.security.InvalidParameterException;
import java.util.HashMap;

/**
 * The authorization controller is the wrapper class for accessing hive node auth module.
 */
public class AuthController {
	private static final Logger log = LoggerFactory.getLogger(AuthController.class);
	private AuthAPI authAPI;
	private String expectationAudience;

	/**
	 * Create the auth controller with the node rpc connection and application instance did document.
	 *
	 * @param connection The node rpc connection
	 * @param appInstanceDidDoc The application instance did document.
	 */
	public AuthController(NodeRPCConnection connection, DIDDocument appInstanceDidDoc ) {
		this.authAPI = connection.createService(AuthAPI.class, false);
		this.expectationAudience = appInstanceDidDoc.getSubject().toString();
	}

	/**
	 * Sign in to hive node
	 *
	 * @param appInstanceDidDoc The application
	 * @return The challenge.
	 * @throws HiveException The exception shows the error result of the request.
	 */
	public String signIn(DIDDocument appInstanceDidDoc) throws HiveException {
		try {
			Object document = new ObjectMapper()
							.readValue(appInstanceDidDoc.toString(), HashMap.class);

			ChallengeRequest challenge;
			challenge = authAPI.signIn(new SignInRequest(document))
							.execute()
							.body();

			if (!checkValid(challenge.getChallenge(), expectationAudience)) {
				log.error("Failed to check the valid of challenge code when sign in.");
				throw new ServerUnknownException("Invalid challenge code, possibly being hacked.");
			}
			return challenge.getChallenge();
		} catch (NodeRPCException e) {
			switch (e.getCode()) {
				case NodeRPCException.BAD_REQUEST:
					throw new InvalidParameterException(e.getMessage());
				default:
					throw new ServerUnknownException(e);
			}
		} catch (IOException e) {
			throw new NetworkException(e);
		}
	}

	/**
	 * The auth operation is for getting the access token for node APIs.
	 *
	 * @param challengeResponse The challenge returned by sign in.
	 * @return The access token.
	 * @throws HiveException The exception shows the error result of the request.
	 */
	public String auth(String challengeResponse) throws HiveException {
		try {
			AccessCode token = authAPI.auth(new ChallengeResponse(challengeResponse))
							.execute()
							.body();

			if (!checkValid(token.getToken(), expectationAudience)) {
				log.error("Failed to check the valid of access token when auth.");
				throw new ServerUnknownException("Invalid challenge code, possibly being hacked.");
			}
			return token.getToken();

		} catch (NodeRPCException e) {
			switch (e.getCode()) {
				case NodeRPCException.BAD_REQUEST:
					throw new InvalidParameterException(e.getMessage());
				default:
					throw new ServerUnknownException(e);
			}
		} catch (IOException e) {
			throw new NetworkException(e);
		}
	}

	private boolean checkValid(String jwtCode, String expectationDid) {
		try {
			Claims claims = new JwtParserBuilder()
					.build()
					.parseClaimsJws(jwtCode)
					.getBody();
			return claims.getExpiration().getTime() > System.currentTimeMillis() &&
					claims.getAudience().equals(expectationDid);
		} catch (Exception e) {
			return false;
		}
	}
}
