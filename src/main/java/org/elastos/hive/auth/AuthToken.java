package org.elastos.hive.auth;

import org.elastos.did.VerifiableCredential;
import org.elastos.hive.utils.LogUtil;

public class AuthToken {
	public static final String TYPE_TOKEN = "token";
	public static final String TYPE_BACKUP = "backup";

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
		if (TYPE_BACKUP.equals(this.tokenType)) {
			return isBackupExpired();
		}
		return System.currentTimeMillis() >= (expiresTime * 1000);
	}

	private boolean isBackupExpired() {
		try {
			return VerifiableCredential.fromJson(this.accessToken).isExpired();
		} catch (Exception e) {
			LogUtil.e("Failed to check backup credential with message:" + e.getMessage());
			return true;
		}
	}
}
