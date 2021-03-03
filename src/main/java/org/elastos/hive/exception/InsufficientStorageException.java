package org.elastos.hive.exception;

public class InsufficientStorageException extends RuntimeException {
	private static final long serialVersionUID = -6062688696002294663L;

	public InsufficientStorageException() {
		super();
	}

	public InsufficientStorageException(String message) {
		super(message);
	}

	public InsufficientStorageException(String message, Throwable cause) {
		super(message, cause);
	}

	public InsufficientStorageException(Throwable cause) {
		super(cause);
	}
}
