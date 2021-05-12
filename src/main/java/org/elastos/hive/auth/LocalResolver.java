package org.elastos.hive.auth;

import com.google.gson.Gson;
import org.elastos.hive.ServiceEndpoint;
import org.elastos.hive.exception.HttpFailedException;
import org.elastos.hive.storage.DataStorage;

public class LocalResolver implements TokenResolver {
	private TokenResolver nextResolver;

	protected DataStorage dataStorage;
	protected ServiceEndpoint serviceEndpoint;
	protected AuthToken token;

	public LocalResolver(ServiceEndpoint serviceEndpoint) {
		this.dataStorage = serviceEndpoint.getAppContext().getDataStorage();
		this.serviceEndpoint = serviceEndpoint;
	}

	@Override
	public AuthToken getToken() throws HttpFailedException {
		if (token == null)
			token = restoreToken();

		if (token == null || token.isExpired()) {
			token = nextResolver.getToken();
			saveToken(token);
		}

		return token;
	}

	@Override
	public void invalidateToken() {
		if (token != null) {
			token = null;
			clearToken();
		}
	}

	@Override
	public void setNextResolver(TokenResolver resolver) {
		this.nextResolver = resolver;
	}

	protected AuthToken restoreToken() {
		String tokenStr = null;
		if (serviceEndpoint.getServiceDid() != null) {
			tokenStr = dataStorage.loadAccessToken(serviceEndpoint.getServiceDid());
			if (tokenStr == null)
				tokenStr = dataStorage.loadAccessTokenByAddress(serviceEndpoint.getProviderAddress());
		}

		if (tokenStr == null)
			return null;

		return new Gson().fromJson(tokenStr, AuthTokenToVault.class);
	}

	protected void saveToken(AuthToken token) {
		String tokenStr = new Gson().toJson(token);
		if (serviceEndpoint.getServiceDid() != null)
			dataStorage.storeAccessToken(serviceEndpoint.getServiceDid(), tokenStr);
		dataStorage.storeAccessTokenByAddress(serviceEndpoint.getProviderAddress(), tokenStr);
	}

	protected void clearToken() {
		if (serviceEndpoint.getServiceDid() != null)
			dataStorage.clearAccessToken(serviceEndpoint.getServiceDid());
		dataStorage.clearAccessTokenByAddress(serviceEndpoint.getProviderAddress());
	}
}
