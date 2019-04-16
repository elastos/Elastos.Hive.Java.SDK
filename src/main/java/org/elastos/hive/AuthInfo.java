package org.elastos.hive;

public class AuthInfo {
	private String scopes;
	private String refreshToken;
	private String accessToken;
	private long expiredIn;

	public AuthInfo() {
	}

	public AuthInfo withScopes(String scopes) {
		this.scopes = scopes;
		return this;
	}

	public AuthInfo withAccessToken(String accessToken) {
		this.accessToken = accessToken;
		return this;
	}

	public AuthInfo withRefreshToken(String refreshToken) {
		this.refreshToken = refreshToken;
		return this;
	}

	public AuthInfo withExpiredIn(long expiredIn) {
		this.expiredIn = expiredIn;
		return this;
	}

	public void resetAccessToken(String accessToken) {
		this.accessToken = accessToken;
	}

	public String getRefreshToken() {
		return refreshToken;
	}

	public String getAccessToken() {
		return accessToken;
	}

	public boolean isExpired() {
		// TODO;
		return false;
	}
}
