package org.elastos.hive.exception;

public class VaultAlreadyExistsException extends EntityAlreadyExistsException {
	private static final long serialVersionUID = 1657471213935972912L;

	public VaultAlreadyExistsException() {
		super();
	}

	public VaultAlreadyExistsException(String message) {
		super(message);
	}

	public VaultAlreadyExistsException(String message, Throwable cause) {
		super(message, cause);
	}

	public VaultAlreadyExistsException(Throwable cause) {
		super(cause);
	}
}
