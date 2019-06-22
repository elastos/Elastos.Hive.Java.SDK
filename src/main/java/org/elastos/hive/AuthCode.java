package org.elastos.hive;

class AuthCode extends Result {
	private final String authCode;

	AuthCode(String authCode) {
		this.authCode = authCode;
	}

	String getAuthCode() {
		return authCode;
	}
}
