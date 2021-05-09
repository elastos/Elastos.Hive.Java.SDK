package org.elastos.hive.auth;

public abstract class AuthToken {
	private String accessToken;
	private long expiresTime;

	protected AuthToken(String accessToken, long expiresTime) {
		this.accessToken = accessToken;
		this.expiresTime = expiresTime;
	}

	public String getAccessToken() {
		return accessToken;
	}

	public long getExpiresTime() {
		return expiresTime;
	}

	public abstract String getCanonicalizedAccessToken();
	public abstract boolean isExpired();
}
