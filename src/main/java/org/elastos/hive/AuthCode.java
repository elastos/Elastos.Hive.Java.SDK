package org.elastos.hive;

class AuthCode implements ResultItem {
	private final String authCode;

	AuthCode(String authCode) {
		this.authCode = authCode;
	}

	String getAuthCode() {
		return authCode;
	}
}
