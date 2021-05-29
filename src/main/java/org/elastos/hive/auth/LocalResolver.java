package org.elastos.hive.auth;

import org.elastos.hive.ServiceEndpoint;
import org.elastos.hive.exception.HttpFailedException;
import org.elastos.hive.storage.DataStorage;

class LocalResolver implements CodeResolver {
	private CodeResolver nextResolver;
	protected DataStorage storage;
	protected ServiceEndpoint serviceEndpoint;

	public LocalResolver(ServiceEndpoint endpoint, CodeResolver next) {
		this.storage  = endpoint.getAppContext().getDataStorage();
		this.serviceEndpoint = endpoint;
		this.nextResolver = next;
	}

	@Override
	public String resolve() throws HttpFailedException {
		String token = restoreToken();
		if (token == null) {
			token = nextResolver.resolve();
			saveToken(token);
		}
		return token;
	}

	@Override
	public void invalidate() {
		clearToken();
	}

	protected String restoreToken() {
		String jwtCode = null;

		String serviceDid = serviceEndpoint.getServiceInstanceDid();
		if (serviceDid != null) {
			jwtCode = storage.loadAccessToken(serviceDid);
			if (jwtCode != null && isTokenExpired(jwtCode))
				storage.clearAccessToken(serviceDid);
		}

		if (jwtCode == null) {
			String address = serviceEndpoint.getProviderAddress();
			jwtCode = storage.loadAccessToken(address);
			if (jwtCode != null && isTokenExpired(jwtCode))
				storage.clearAccessToken(address);
		}

		return jwtCode;
	}

	private boolean isTokenExpired(String jwtCode) {
		// TODO: check the expiration of the access token.
		return false;
	}

	protected void saveToken(String jwtCode) {
		if (jwtCode == null)
			return;

		storage.storeAccessToken(serviceEndpoint.getServiceInstanceDid(), jwtCode);
		storage.storeAccessTokenByAddress(serviceEndpoint.getProviderAddress(), jwtCode);
	}

	protected void clearToken() {
		storage.clearAccessToken(serviceEndpoint.getServiceInstanceDid());
		storage.clearAccessTokenByAddress(serviceEndpoint.getProviderAddress());
	}
}
