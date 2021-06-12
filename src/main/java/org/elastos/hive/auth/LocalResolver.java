package org.elastos.hive.auth;

import org.elastos.hive.ServiceEndpoint;
import org.elastos.hive.exception.NodeRPCException;
import org.elastos.hive.storage.DataStorage;

class LocalResolver implements CodeResolver {
	private CodeResolver nextResolver;
	protected ServiceEndpoint serviceEndpoint;

	public LocalResolver(ServiceEndpoint endpoint, CodeResolver next) {
		this.serviceEndpoint = endpoint;
		this.nextResolver = next;
	}

	@Override
	public String resolve() throws NodeRPCException {
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
		DataStorage storage = serviceEndpoint.getStorage();
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
		DataStorage storage = serviceEndpoint.getStorage();

		if (jwtCode == null)
			return;

		storage.storeAccessToken(serviceEndpoint.getServiceInstanceDid(), jwtCode);
		storage.storeAccessTokenByAddress(serviceEndpoint.getProviderAddress(), jwtCode);
	}

	protected void clearToken() {
		DataStorage storage = serviceEndpoint.getStorage();

		storage.clearAccessToken(serviceEndpoint.getServiceInstanceDid());
		storage.clearAccessTokenByAddress(serviceEndpoint.getProviderAddress());
	}
}
