package org.elastos.hive.auth;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.elastos.did.jwt.Claims;
import org.elastos.hive.AppContext;
import org.elastos.hive.AppContextProvider;
import org.elastos.hive.connection.ConnectionManager;
import org.elastos.hive.connection.model.AuthToken;
import org.elastos.hive.exception.HiveException;
import org.elastos.hive.network.request.AuthAuthRequestBody;
import org.elastos.hive.network.request.AuthSignInRequestBody;
import org.elastos.hive.network.response.AuthAuthResponseBody;
import org.elastos.hive.network.response.AuthSignInResponseBody;
import org.elastos.hive.utils.JwtUtil;
import org.jetbrains.annotations.NotNull;
import retrofit2.Response;

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
			AuthSignInRequestBody reqBody = new AuthSignInRequestBody();
			reqBody.setDocument(new ObjectMapper().readValue(contextProvider.getAppInstanceDocument().toString(), HashMap.class));
			Response<AuthSignInResponseBody> response = connectionManager.getAuthApi()
					.signIn(reqBody)
					.execute();
			AuthSignInResponseBody sp = AuthSignInResponseBody.validateBody(response);
			sp.checkValid(contextProvider.getAppInstanceDocument().getSubject().toString());
			return contextProvider.getAuthorization(sp.getChallenge()).get();
		} catch (Exception e) {
			throw new HiveException(e.getMessage());
		}
	}

	private void verifyToken(String jwtToken) throws HiveException {
		if (jwtToken == null || jwtToken.isEmpty()) throw new HiveException("Failed to get challenge from sign-in.");

		Claims claims = JwtUtil.getBody(jwtToken);
		long exp = claims.getExpiration().getTime();
		String aud = claims.getAudience();

		String did = contextProvider.getAppInstanceDocument().getSubject().toString();
		if (null == did || !did.equals(aud)) throw new HiveException("Failed to get valid challenge from sign-in.");

		if (exp <= System.currentTimeMillis()) throw new HiveException("Failed to get period challenge from sign-in");
	}

	private AuthToken auth(String token) throws HiveException {
		try {
			AuthAuthRequestBody reqBody = new AuthAuthRequestBody();
			reqBody.setJwt(token);
			Response<AuthAuthResponseBody> response = connectionManager.getAuthApi()
					.auth(reqBody)
					.execute();
			AuthAuthResponseBody body = AuthAuthResponseBody.validateBody(response);
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
