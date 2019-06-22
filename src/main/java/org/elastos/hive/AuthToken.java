package org.elastos.hive;

public class AuthToken {
	@SuppressWarnings("unused")
	private final String scope;
	private final String refreshToken;
	private final String accessToken;
	private long experitime;
	private final long createdTime;

	public AuthToken(String scope, String refreshToken, String accessToken, long experitime) {
		this.scope = scope;
		this.accessToken = accessToken;
		this.refreshToken = refreshToken;
		this.experitime = experitime;
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
		if (currentSeconds >= (createdTime + experitime)) {
			return true;
		}

		return false;
	}

	public void expired() {
		this.experitime = 0;
	}
}
