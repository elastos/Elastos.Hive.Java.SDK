package org.elastos.hive.auth;

import org.elastos.hive.ServiceEndpoint;
import org.elastos.hive.exception.HttpFailedException;
import org.elastos.hive.storage.DataStorage;

public class LocalResolver implements CodeResolver {
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
		String serviceDid = serviceEndpoint.getServiceInstanceDid();
		String jwtCode = null;

		if (serviceDid != null)
			jwtCode = storage.loadAccessToken(serviceDid);

		String address = serviceEndpoint.getProviderAddress();
		if (serviceDid == null && jwtCode == null)
			jwtCode = storage.loadAccessToken(address);

		return jwtCode;
	}

	protected void saveToken(String jwtCode) {
		String serviceDid = serviceEndpoint.getServiceInstanceDid();
		String address    = serviceEndpoint.getProviderAddress();

		if (jwtCode == null)
			return;

		storage.storeAccessToken(serviceDid, jwtCode);
		storage.storeAccessToken(address, jwtCode);
	}

	protected void clearToken() {
		String serviceDid = serviceEndpoint.getServiceInstanceDid();
		String address    = serviceEndpoint.getProviderAddress();

		storage.clearAccessToken(serviceDid);
		storage.clearAccessTokenByAddress(address);
	}
}
