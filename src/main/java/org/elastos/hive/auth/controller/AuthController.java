package org.elastos.hive.auth.controller;

import com.fasterxml.jackson.databind.ObjectMapper;

import org.elastos.did.DIDDocument;
import org.elastos.did.jwt.Claims;
import org.elastos.did.jwt.JwtParserBuilder;
import org.elastos.hive.ServiceEndpoint;
import org.elastos.hive.connection.NodeRPCException;
import org.elastos.hive.exception.HiveException;
import org.elastos.hive.exception.NetworkException;
import org.elastos.hive.exception.ServerUnkownException;

import java.io.IOException;
import java.util.HashMap;

public class AuthController {
	private AuthAPI authAPI;
	private String expectationAudience;

	public AuthController(ServiceEndpoint endpoint, DIDDocument appInstanceDidDoc ) {
		this.authAPI = endpoint.createService(AuthAPI.class, false);
		this.expectationAudience = appInstanceDidDoc.getSubject().toString();
	}

	public String signIn(DIDDocument appInstanceDidDoc) throws HiveException {
		try {
			Object document = new ObjectMapper()
							.readValue(appInstanceDidDoc.toString(), HashMap.class);

			ChallengeRequest challenge;
			challenge = authAPI.signIn(new SignInRequest(document))
							.execute()
							.body();

			if (!checkValid(challenge.getChallenge(), expectationAudience)) {
				// TODO: log here.
				throw new ServerUnkownException("Invalid challenge code, possibly being hacked.");
			}
			return challenge.getChallenge();

		} catch (NodeRPCException e) {
			// TODO: Handle http error code here;
			throw new ServerUnkownException();

		} catch (IOException e) {
			throw new NetworkException(e);
		}
	}

	public String auth(String challengeResponse) throws HiveException {
		try {
			AccessToken token = authAPI.auth(new ChallengeResponse(challengeResponse))
							.execute()
							.body();

			if (!checkValid(token.getAccessToken(), expectationAudience)) {
				// TODO: log here.
				throw new ServerUnkownException("Invalid challenge code, possibly being hacked.");
			}
			return token.getAccessToken();

		} catch (NodeRPCException e) {
			// TODO: Handle http error code here;
			throw new ServerUnkownException();

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
			// TOOD: log here.
			return false;
		}
	}
}
