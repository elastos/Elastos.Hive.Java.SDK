package org.elastos.hive.exception;

public class VaultAlreadyExistException extends RuntimeException {

	private static final long serialVersionUID = 3332367416260335612L;

	public static final String EXCEPTION = "The vault aleady exist in the node";

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
