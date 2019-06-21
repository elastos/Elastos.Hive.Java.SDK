package org.elastos.hive;

public class AuthToken implements ResultItem {
	private final String refreshToken;
	private final String accessToken;
	private long experitime;

	public AuthToken(String refreshToken, String accessToken, long experitime) {
		this.refreshToken = refreshToken;
		this.accessToken = accessToken;
		this.experitime = experitime;
	}

	public String getRefreshToken() {
		return refreshToken;
	}

	public String getAccessToken() {
		return accessToken;
	}

	public long getExpiredTime() {
		return experitime;
	}

	public boolean isExpired() {
		long currentSeconds = System.currentTimeMillis() / 1000;
		if (currentSeconds >= experitime) {
			return true;
		}

		return false;
	}
	
	public void expired() {
		this.experitime = 0;
	}
}
