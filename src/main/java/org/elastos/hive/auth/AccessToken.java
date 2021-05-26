package org.elastos.hive.auth;

import org.elastos.hive.ServiceEndpoint;

public class AccessToken extends AuthToken {
	private String jwtCode;
	private TokenResolver resolver;

	public AccessToken(ServiceEndpoint endpoint) {
		TokenResolver remoteResolver = new RemoteResolver(endpoint);
		resolver = new LocalResolver(endpoint);
		resolver.setNextResolver(remoteResolver);
	}

	public String getToken() {
		if (jwtCode != null)
			return jwtCode;

		try {
			jwtCode = resolver.getToken();
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
		resolver.invalidateToken();
	}
}
