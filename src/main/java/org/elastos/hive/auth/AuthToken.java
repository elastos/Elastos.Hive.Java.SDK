package org.elastos.hive.auth;

public abstract class AuthToken {
	public abstract String getCanonicalizedAccessToken();
	public abstract boolean isExpired();
}
