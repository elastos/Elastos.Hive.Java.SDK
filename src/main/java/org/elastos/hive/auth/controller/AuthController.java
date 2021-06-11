package org.elastos.hive.auth.controller;

import com.fasterxml.jackson.databind.ObjectMapper;

import org.elastos.did.DIDDocument;
import org.elastos.did.jwt.Claims;
import org.elastos.did.jwt.JwtParserBuilder;
import org.elastos.hive.ServiceEndpoint;
import org.elastos.hive.exception.HiveException;

import java.io.IOException;
import java.util.HashMap;

public class AuthController {
	private AuthAPI authAPI;
	private String appInstanceDid;

	public AuthController(ServiceEndpoint serviceEndpoint, DIDDocument appInstanceDidDocument ) {
		this.authAPI = serviceEndpoint.getConnectionManager().createService(AuthAPI.class, false);
		this.appInstanceDid = appInstanceDidDocument.getSubject().toString();
	}

	public String signIn(DIDDocument appInstanceDidDocument) throws HiveException {
		try {
			Object document = new ObjectMapper()
						.readValue(appInstanceDidDocument.toString(), HashMap.class);
			ChallengeRequest challenge = authAPI.signIn(new SignInRequest(document)).execute().body();
			if (!checkMatch(challenge.getChallenge(), appInstanceDid)) {
				// TODO: logger
				throw new HiveException("Unknown sign-in failure, probably being hacked.");
			}

			return challenge.getChallenge();
		} catch (IOException e) {
			// TODO:
			e.printStackTrace();
		}
		return null;
	}

	public String auth(String challengeResponse) throws HiveException {
		try {
			AccessToken token = authAPI.auth(new ChallengeResponse(challengeResponse)).execute().body();
			if (!checkMatch(token.getAccessToken(), appInstanceDid)) {
				// TODO: logger
				throw new HiveException("Unknown auth failure, probably being hacked.");
			}

			return token.getAccessToken();
		} catch (IOException e) {
			// TODO:
			e.printStackTrace();
		}
		return null;
	}

	private boolean checkMatch(String jwtCode, String expected) {
		Claims claims;

		try {
			claims = new JwtParserBuilder().build().parseClaimsJws(jwtCode).getBody();
		} catch (Exception e) {
			// TOOD: console log;
			return false;
		}

		return claims.getExpiration().getTime() > System.currentTimeMillis()
				&& claims.getAudience().equals(expected);

	}

}
