package org.elastos.hive.auth;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.elastos.hive.AppContext;
import org.elastos.hive.AppContextProvider;
import org.elastos.hive.connection.ConnectionManager;
import org.elastos.hive.connection.model.AuthToken;
import org.elastos.hive.exception.HiveException;
import org.elastos.hive.network.request.AuthRequestBody;
import org.elastos.hive.network.request.SignInRequestBody;
import org.elastos.hive.network.response.AuthResponseBody;
import org.elastos.hive.network.response.SignInResponseBody;
import org.elastos.hive.network.response.ResponseBodyBase;
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
			SignInRequestBody reqBody = new SignInRequestBody();
			reqBody.setDocument(new ObjectMapper().readValue(contextProvider.getAppInstanceDocument().toString(), HashMap.class));

			SignInResponseBody rspBody = connectionManager.getAuthApi()
					.signIn(reqBody)
					.execute()
					.body();
			rspBody.checkValid(contextProvider.getAppInstanceDocument().getSubject().toString());
			return contextProvider.getAuthorization(rspBody.getChallenge()).get();
		} catch (Exception e) {
			throw new HiveException(e.getMessage());
		}
	}

	private AuthToken auth(String token) throws HiveException {
		try {
			AuthRequestBody reqBody = new AuthRequestBody();
			reqBody.setJwt(token);

			AuthResponseBody rspBody = connectionManager.getAuthApi()
					.auth(reqBody)
					.execute()
					.body();

			long exp = JwtUtil.getBody(rspBody.getToken()).getExpiration().getTime();
			long expiresTime = System.currentTimeMillis() / 1000 + exp / 1000;
			return new AuthToken(rspBody.getToken(), expiresTime, "token");
		} catch (Exception e) {
			throw new HiveException(e.getMessage());
		}
	}

	@Override
	public void saveToken() throws HiveException {}

	@Override
	public void setNextResolver(TokenResolver resolver) {}
}
