package org.elastos.hive.exception;

public class VaultNotFoundException extends RuntimeException {

	private static final long serialVersionUID = -455891070244326338L;

	public VaultNotFoundException() {
		super();
	}

	public VaultNotFoundException(String message) {
		super(message);
	}

	public VaultNotFoundException(String message, Throwable cause) {
		super(message, cause);
	}

	public VaultNotFoundException(Throwable cause) {
		super(cause);
	}
}
