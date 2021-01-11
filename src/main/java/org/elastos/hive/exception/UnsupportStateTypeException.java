package org.elastos.hive.exception;

public class UnsupportStateTypeException extends RuntimeException {

	private static final long serialVersionUID = -8056033362026965708L;

	public UnsupportStateTypeException() {
		super();
	}

	public UnsupportStateTypeException(String message) {
		super(message);
	}

	public UnsupportStateTypeException(String message, Throwable cause) {
		super(message, cause);
	}

	public UnsupportStateTypeException(Throwable cause) {
		super(cause);
	}
}
