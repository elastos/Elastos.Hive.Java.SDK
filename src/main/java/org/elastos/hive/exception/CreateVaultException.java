package org.elastos.hive.exception;

public class CreateVaultException extends RuntimeException {

	private static final long serialVersionUID = 3332367416260335612L;

	public static final String EXCEPTION = "Vault has been created";

	public CreateVaultException() {
		super();
	}

	public CreateVaultException(String message) {
		super(message);
	}

	public CreateVaultException(String message, Throwable cause) {
		super(message, cause);
	}

	public CreateVaultException(Throwable cause) {
		super(cause);
	}
}
