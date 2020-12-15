package org.elastos.hive.exception;

public class CreateVaultFailedException extends RuntimeException {
	private static final long serialVersionUID = -6628861961910255071L;

	public static final String EXCEPTION = "create vault failed";

	public CreateVaultFailedException() {
		super();
	}

	public CreateVaultFailedException(String message) {
		super(message);
	}

	public CreateVaultFailedException(String message, Throwable cause) {
		super(message, cause);
	}

	public CreateVaultFailedException(Throwable cause) {
		super(cause);
	}
}
