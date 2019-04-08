package org.elastos.hive;

public class AuthResult {
	private String authorCode;
	private long errorCode;

	public AuthResult(String authorCode) {
		this.authorCode = authorCode;
	}

	public AuthResult(long errorCode) {
		this.errorCode = errorCode;
	}

	public boolean isAuthorized() {
		return authorCode != null;
	}

	public String getAuthorCode() {
		return authorCode;
	}

	public long getErrorCode() {
		return errorCode;
	}
}
