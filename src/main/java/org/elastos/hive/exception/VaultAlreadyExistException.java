package org.elastos.hive.exception;

public class VaultAlreadyExistException extends HiveException {
	private static final long serialVersionUID = 3332367416260335612L;

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
