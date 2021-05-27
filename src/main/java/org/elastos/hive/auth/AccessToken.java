package org.elastos.hive.auth;

import org.elastos.hive.ServiceEndpoint;

public class AccessToken extends AuthToken {
	private String jwtCode;
	private CodeResolver resolver;

	public AccessToken(ServiceEndpoint endpoint) {
		CodeResolver remoteResolver = new AccessTokenRemoteResolver(endpoint);
		resolver = new AccessTokenLocalResolver(endpoint, remoteResolver);
	}

	public String getToken() {
		if (jwtCode != null)
			return jwtCode;

		try {
			jwtCode = resolver.resolve();
		} catch (Exception e) {
			// TODO:
			e.printStackTrace();
		}

		return jwtCode;
	}

	@Override
	public String getCanonicalizedAccessToken() {
		return "token " + getToken();
	}

	@Override
	public boolean isExpired() {
		//return System.currentTimeMillis() >= (getExpiresTime() * 1000);
		return false;
	}

	public void invalidateToken() {
		resolver.invalidate();
	}
}
