package org.elastos.hive.auth;

import com.google.gson.Gson;
import org.elastos.hive.ServiceEndpoint;
import org.elastos.hive.exception.HttpFailedException;
import org.elastos.hive.storage.DataStorage;
import org.elastos.hive.storage.FileStorage;

import java.io.File;

public class LocalResolver implements TokenResolver {
	private TokenResolver nextResolver;
	private AuthToken token;
	private DataStorage dataStorage;
	private ServiceEndpoint serviceEndpoint;

	public LocalResolver(String userDid, ServiceEndpoint serviceEndpoint, String cacheDir) {
		this.dataStorage = new FileStorage(cacheDir + File.pathSeparator + "access-cache", userDid);
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

	private AuthToken restoreToken() {
		String token = null;
		if (serviceEndpoint.getServiceDid() != null) {
			token = dataStorage.loadAccessToken(serviceEndpoint.getServiceDid());
			if (token == null)
				token = dataStorage.loadAccessTokenByAddress(serviceEndpoint.getProviderAddress());
		}

		if (token == null)
			return null;

		return new Gson().fromJson(token, AuthTokenToVault.class);
	}

	private void saveToken(AuthToken token) {
		String tokenStr = new Gson().toJson(token);
		if (serviceEndpoint.getServiceDid() != null)
			dataStorage.storeAccessToken(serviceEndpoint.getServiceDid(), tokenStr);
		dataStorage.storeAccessTokenByAddress(serviceEndpoint.getProviderAddress(), tokenStr);
	}

	private void clearToken() {
		if (serviceEndpoint.getServiceDid() != null)
			dataStorage.clearAccessToken(serviceEndpoint.getServiceDid());
		dataStorage.clearAccessTokenByAddress(serviceEndpoint.getProviderAddress());
	}
}
