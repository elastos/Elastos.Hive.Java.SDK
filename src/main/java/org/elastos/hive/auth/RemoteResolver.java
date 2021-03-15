package org.elastos.hive.auth;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.ImmutableMap;
import org.elastos.did.jwt.Claims;
import org.elastos.hive.AppContext;
import org.elastos.hive.AppContextProvider;
import org.elastos.hive.connection.ConnectionManager;
import org.elastos.hive.connection.model.AuthToken;
import org.elastos.hive.exception.HiveException;
import org.elastos.hive.network.response.AuthAuthResponse;
import org.elastos.hive.network.response.AuthSignInResponse;
import org.elastos.hive.network.response.ResponseBase;
import org.elastos.hive.utils.JwtUtil;
import org.jetbrains.annotations.NotNull;
import retrofit2.Response;

import java.util.Collections;
import java.util.HashMap;

public class RemoteResolver implements TokenResolver {
	private AppContextProvider contextProvider;
	private ConnectionManager connectionManager;

	public RemoteResolver(AppContext context, ConnectionManager connectionManager) {
		this.contextProvider = context.getAppContextProvider();
		this.connectionManager = connectionManager;
	}

	@NotNull
	@Override
	public AuthToken getToken() throws HiveException {
		return auth(signIn());
	}

	private String signIn() throws HiveException {
		try {
			Response<AuthSignInResponse> response = connectionManager.getAuthApi()
					.signIn(ImmutableMap.of("document", new ObjectMapper().readValue(contextProvider.getAppInstanceDocument().toString(), HashMap.class)))
					.execute();
			AuthSignInResponse sp = ResponseBase.validateBody(response);
			sp.checkValid(contextProvider.getAppInstanceDocument().getSubject().toString());
			return contextProvider.getAuthorization(sp.getChallenge()).get();
		} catch (Exception e) {
			throw new HiveException(e.getMessage());
		}
	}

	private AuthToken auth(String token) throws HiveException {
		try {
			Response<AuthAuthResponse> response = connectionManager.getAuthApi()
					.auth(Collections.singletonMap("jwt", token))
					.execute();
			AuthAuthResponse body = ResponseBase.validateBody(response);
			long exp = JwtUtil.getBody(body.getToken()).getExpiration().getTime();
			long expiresTime = System.currentTimeMillis() / 1000 + exp / 1000;
			return new AuthToken(body.getToken(), expiresTime, "token");
		} catch (Exception e) {
			throw new HiveException(e.getMessage());
		}
	}

	@Override
	public void saveToken() {
		// Do nothing.
	}

	@Override
	public void setNextResolver(TokenResolver resolver) {
		// Do nothing;
	}
}
