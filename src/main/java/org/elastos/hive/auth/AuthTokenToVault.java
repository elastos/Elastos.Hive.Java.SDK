package org.elastos.hive.auth;

public class AuthTokenToVault extends AuthToken {
	public static final String TOKEN_TYPE = "token";

	public AuthTokenToVault(String accessToken, long expiresTime) {
		super(accessToken, expiresTime);
	}

	@Override
	public String getCanonicalizedAccessToken() {
		return TOKEN_TYPE + " " + getAccessToken();
	}

	@Override
	public boolean isExpired() {
		return System.currentTimeMillis() >= (getExpiresTime() * 1000);
	}
}
