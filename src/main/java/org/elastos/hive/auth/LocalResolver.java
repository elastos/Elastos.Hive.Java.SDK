package org.elastos.hive.auth;

import org.elastos.hive.ServiceEndpoint;
import org.elastos.hive.exception.HttpFailedException;
import org.elastos.hive.storage.DataStorage;

public class LocalResolver implements TokenResolver {
	private TokenResolver nextResolver;

	protected DataStorage dataStorage;
	protected ServiceEndpoint serviceEndpoint;

	public LocalResolver(ServiceEndpoint serviceEndpoint) {
		this.dataStorage = serviceEndpoint.getAppContext().getDataStorage();
		this.serviceEndpoint = serviceEndpoint;
	}

	@Override
	public String getToken() throws HttpFailedException {
		String token = restoreToken();
		if (token == null) {
			token = nextResolver.getToken();
			saveToken(token);
		}

		return token;
	}

	@Override
	public void invalidateToken() {
		clearToken();
	}

	@Override
	public void setNextResolver(TokenResolver resolver) {
		this.nextResolver = resolver;
	}

	protected String restoreToken() {
		String tokenStr = null;
		if (serviceEndpoint.getServiceInstanceDid() != null) {
			tokenStr = dataStorage.loadAccessToken(serviceEndpoint.getServiceInstanceDid());
			if (tokenStr == null)
				tokenStr = dataStorage.loadAccessTokenByAddress(serviceEndpoint.getProviderAddress());
		}

		return tokenStr;
	}

	protected void saveToken(String tokenStr) {
		if (serviceEndpoint.getServiceInstanceDid() != null)
			dataStorage.storeAccessToken(serviceEndpoint.getServiceInstanceDid(), tokenStr);
		dataStorage.storeAccessTokenByAddress(serviceEndpoint.getProviderAddress(), tokenStr);
	}

	protected void clearToken() {
		if (serviceEndpoint.getServiceInstanceDid() != null)
			dataStorage.clearAccessToken(serviceEndpoint.getServiceInstanceDid());
		dataStorage.clearAccessTokenByAddress(serviceEndpoint.getProviderAddress());
	}
}
