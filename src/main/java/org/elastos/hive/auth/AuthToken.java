package org.elastos.hive.auth;

public class AuthToken {
	private String accessToken;
	private long expiresTime;
	private String tokenType;

	public AuthToken(String accessToken, long expiresTime, String tokenType) {
		this.accessToken = accessToken;
		this.expiresTime = expiresTime;
		this.tokenType = tokenType;
	}

	public String getAccessToken() {
		return accessToken;
	}

	public long getExpiresTime() {
		return expiresTime;
	}

	public String getTokenType() {
		return tokenType;
	}

	public String getCanonicalizedAccessToken() {
		return tokenType + " " + accessToken;
	}

	public boolean isExpired() {
		return System.currentTimeMillis() >= (expiresTime * 1000);
	}
}
