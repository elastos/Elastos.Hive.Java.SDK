package org.elastos.hive;

public class AuthToken implements ResultItem {
	@SuppressWarnings("unused")
	private final String scope;
	private final String refreshToken;
	private final String accessToken;
	private final long expiresIn;
	private final long createdTime;

	public AuthToken(String scope, String refreshToken, String accessToken, long expiresIn) {
		this.scope = scope;
		this.accessToken = accessToken;
		this.refreshToken = refreshToken;
		this.expiresIn = expiresIn;
		this.createdTime = System.currentTimeMillis() / 1000;
	}

	public String getRefreshToken() {
		return refreshToken;
	}

	public String getAccessToken() {
		return accessToken;
	}

	public boolean isExpired() {
		long currentSeconds = System.currentTimeMillis() / 1000;
		if (currentSeconds >= (createdTime + expiresIn)) {
			return true;
		}

		return false;
	}
}
