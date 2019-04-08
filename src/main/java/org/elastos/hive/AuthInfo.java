package org.elastos.hive;

import org.jetbrains.annotations.NotNull;

public class AuthInfo {
	// private final @NotNull String tokenType;
	private final @NotNull String scopes;
	private final @NotNull String refreshToken;
	private String accessToken;
	private long expiredIn;

	public AuthInfo(String scopes, String accessToken, String refreshToken, long expiredIn) {
		this.scopes = scopes;
		this.accessToken = accessToken;
		this.refreshToken = refreshToken;
		this.expiredIn = expiredIn;
	}

	public void resetAccessToken(String accessToken) {
		this.accessToken = accessToken;
	}

	public String getRefreshToken() {
		return refreshToken;
	}

	public boolean isExpired() {
		// TODO;
		return false;
	}
}
