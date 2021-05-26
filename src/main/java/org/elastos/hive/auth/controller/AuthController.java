package org.elastos.hive.auth.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.elastos.hive.ServiceEndpoint;
import org.elastos.hive.exception.HiveException;

import java.io.IOException;
import java.util.HashMap;

public class AuthController {
	private AuthAPI authAPI;

	public AuthController(ServiceEndpoint serviceEndpoint) {
		this.authAPI = serviceEndpoint.getConnectionManager().createService(AuthAPI.class, false);
	}

	public String signIn(String appInstanceDidDocument) throws HiveException {
		try {
			Object document = new ObjectMapper().readValue(appInstanceDidDocument, HashMap.class);
			return authAPI.signIn(new SigninRequest(document)).execute().body().getChallenge();
		} catch (IOException e) {
			// TODO:
			e.printStackTrace();
		}
		return null;
	}

	public String auth(String token) throws HiveException {
		try {
			return authAPI.auth(new ChallengeResponse(token)).execute().body().getToken();
		} catch (IOException e) {
			// TODO:
			e.printStackTrace();
		}
		return null;
	}
}
