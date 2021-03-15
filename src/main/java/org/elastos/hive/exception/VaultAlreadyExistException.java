package org.elastos.hive.exception;

public class VaultAlreadyExistException extends RuntimeException {


	private static final long serialVersionUID = 1657471213935972912L;

	public VaultAlreadyExistException() {
		super();
	}

	public VaultAlreadyExistException(String message) {
		super(message);
	}

	public VaultAlreadyExistException(String message, Throwable cause) {
		super(message, cause);
	}

	public VaultAlreadyExistException(Throwable cause) {
		super(cause);
	}
}
