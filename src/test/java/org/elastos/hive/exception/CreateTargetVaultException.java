package org.elastos.hive.exception;

public class CreateTargetVaultException extends RuntimeException {

	private static final long serialVersionUID = 1495935787017150393L;

	public CreateTargetVaultException() {
		super();
	}

	public CreateTargetVaultException(String message) {
		super(message);
	}

	public CreateTargetVaultException(String message, Throwable cause) {
		super(message, cause);
	}

	public CreateTargetVaultException(Throwable cause) {
		super(cause);
	}
}
