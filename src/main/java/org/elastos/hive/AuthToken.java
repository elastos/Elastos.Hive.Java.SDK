package org.elastos.hive;

public class AuthToken implements ResultItem {
	@SuppressWarnings("unused")
	private final String scope;
	private final String refreshToken;
	private final String accessToken;
	@SuppressWarnings("unused")
	private final long expiresIn;

	public AuthToken(String scope, String refreshToken, String accessToken, long expiresIn) {
		this.scope = scope;
		this.accessToken = accessToken;
		this.refreshToken = refreshToken;
		this.expiresIn = expiresIn;
	}

	public String getRefreshToken() {
		return refreshToken;
	}

	public String getAccessToken() {
		return accessToken;
	}

	public boolean isExpired() {
		// TODO;
		return true;
	}
}
