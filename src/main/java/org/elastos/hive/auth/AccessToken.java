package org.elastos.hive.auth;

import org.elastos.hive.ServiceEndpoint;
import org.elastos.hive.connection.NodeRPCException;
import org.elastos.hive.storage.DataStorage;

public class AccessToken implements CodeResolver {
	private String jwtCode;
	private CodeResolver nextResolver;
	private DataStorage storage;
	private ServiceEndpoint endpoint;
	private UpdationHandler handler;

	public AccessToken(ServiceEndpoint endpoint, DataStorage storage, UpdationHandler handler) {
		nextResolver = new RemoteResolver(endpoint);
		this.storage = storage;
		this.endpoint = endpoint;
		this.handler = handler;
	}

	public String getCanonicalizedAccessToken() {
		try {
			jwtCode = resolve();
		} catch (Exception e) {
			// TODO:
			return null;
		}
		return "token " + jwtCode;
	}

	public boolean isExpired() {
		//return System.currentTimeMillis() >= (getExpiresTime() * 1000);
		return false;
	}

	@Override
	public String resolve() throws NodeRPCException {
		jwtCode = restoreToken();
		if (jwtCode == null) {
			jwtCode = nextResolver.resolve();
			handler.flush(jwtCode);
			saveToken(jwtCode);
		}
		return jwtCode;
	}

	@Override
	public void invalidate() {
		clearToken();
	}

	private String restoreToken() {
		String jwtCode = null;

		String serviceDid = endpoint.getServiceInstanceDid();
		if (serviceDid != null) {
			jwtCode = storage.loadAccessToken(serviceDid);
			if (jwtCode != null && isTokenExpired(jwtCode))
				storage.clearAccessToken(serviceDid);
		}

		if (jwtCode == null) {
			String address = endpoint.getProviderAddress();
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

	private void saveToken(String jwtCode) {
		if (jwtCode == null)
			return;

		storage.storeAccessToken(endpoint.getServiceInstanceDid(), jwtCode);
		storage.storeAccessTokenByAddress(endpoint.getProviderAddress(), jwtCode);
	}

	private void clearToken() {
		storage.clearAccessToken(endpoint.getServiceInstanceDid());
		storage.clearAccessTokenByAddress(endpoint.getProviderAddress());
	}
}
