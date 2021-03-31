package org.elastos.hive.auth;

import org.elastos.hive.AppContext;
import org.elastos.hive.AppContextProvider;
import org.elastos.hive.connection.ConnectionManager;
import org.elastos.hive.exception.HttpFailedException;
import org.elastos.hive.vault.AuthenticationServiceRender;

public class RemoteResolver implements TokenResolver {
	private AppContextProvider contextProvider;
	private AuthenticationServiceRender authenticationService;

	public RemoteResolver(AppContext context, ConnectionManager connectionManager) {
		this.contextProvider = context.getAppContextProvider();
		this.authenticationService = new AuthenticationServiceRender(context, contextProvider, connectionManager);
	}

	@Override
	public AuthToken getToken() {
		try {
			return authenticationService.auth(authenticationService.signIn4Token());
		} catch (Exception e) {
			throw new HttpFailedException(401, "Failed to get token by auth requests.");
		}
	}

	@Override
	public void invalidateToken() {
		throw new UnsupportedOperationException();
	}

	@Override
	public void setNextResolver(TokenResolver resolver) {
		throw new UnsupportedOperationException();
	}
}
