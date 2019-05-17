package org.elastos.hive;

public class AuthCode implements BaseItem {
	private final String ID = "Authorization Code";
	private String authCode;

	AuthCode(String authCode) {
		this.authCode = authCode;
	}

	@Override
	public String getId() {
		return ID;
	}

	String getAuthCode() {
		return authCode;
	}
}
