package org.elastos.hive.auth;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.elastos.hive.AppContext;
import org.elastos.hive.AppContextProvider;
import org.elastos.hive.connection.ConnectionManager;
import org.elastos.hive.exception.HiveException;
import org.elastos.hive.network.request.AuthRequestBody;
import org.elastos.hive.network.request.SignInRequestBody;
import org.elastos.hive.network.response.AuthResponseBody;
import org.elastos.hive.network.response.HiveResponseBody;
import org.elastos.hive.network.response.SignInResponseBody;
import org.elastos.hive.utils.JwtUtil;

import java.util.HashMap;

public class RemoteResolver implements TokenResolver {
	private AppContextProvider contextProvider;
	private ConnectionManager connectionManager;

	public RemoteResolver(AppContext context, ConnectionManager connectionManager) {
		this.contextProvider = context.getAppContextProvider();
		this.connectionManager = connectionManager;
	}

	@Override
	public AuthToken getToken() throws HiveException {
		return auth(signIn());
	}

	@Override
	public void invalidateToken() {
		throw new UnsupportedOperationException();
	}

	@Override
	public void setNextResolver(TokenResolver resolver) {
		throw new UnsupportedOperationException();
	}

	private String signIn() throws HiveException {
		try {
			SignInResponseBody rspBody = HiveResponseBody.validateBody(
					connectionManager.getAuthApi()
							.signIn(new SignInRequestBody(new ObjectMapper()
									.readValue(
											contextProvider.getAppInstanceDocument().toString(),
											HashMap.class)))
							.execute()
							.body());
			rspBody.checkValid(contextProvider.getAppInstanceDocument().getSubject().toString());
			return contextProvider.getAuthorization(rspBody.getChallenge()).get();
		} catch (Exception e) {
			throw new HiveException(e.getMessage());
		}
	}

	private AuthToken auth(String token) throws HiveException {
		try {
			AuthResponseBody rspBody = HiveResponseBody.validateBody(
					connectionManager.getAuthApi()
					.auth(new AuthRequestBody(token))
					.execute()
					.body());
			long exp = JwtUtil.getBody(rspBody.getToken()).getExpiration().getTime();
			long expiresTime = System.currentTimeMillis() / 1000 + exp / 1000;
			return new AuthToken(rspBody.getToken(), expiresTime, AuthToken.TYPE_TOKEN);
		} catch (Exception e) {
			throw new HiveException(e.getMessage());
		}
	}
}
