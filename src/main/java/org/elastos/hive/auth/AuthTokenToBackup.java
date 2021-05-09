package org.elastos.hive.auth;

import org.elastos.did.VerifiableCredential;
import org.elastos.hive.utils.LogUtil;

public class AuthTokenToBackup extends AuthToken {
	public static final String TOKEN_TYPE = "backup";

	public AuthTokenToBackup(String accessToken, long expiresTime) {
		super(accessToken, expiresTime);
	}

	@Override
	public String getCanonicalizedAccessToken() {
		return TOKEN_TYPE + " " + getAccessToken();
	}

	@Override
	public boolean isExpired() {
		try {
			return VerifiableCredential.fromJson(getAccessToken()).isExpired();
		} catch (Exception e) {
			LogUtil.e("Failed to check backup credential with message:" + e.getMessage());
			return true;
		}
	}
}
