package org.elastos.hive.exception;

public class InternalAuthException extends RuntimeException {
	private static final long serialVersionUID = 6937541784786596198L;

	public InternalAuthException() {
		super();
	}

	public InternalAuthException(String message) {
		super(message);
	}

	public InternalAuthException(String message, Throwable cause) {
		super(message, cause);
	}

	public InternalAuthException(Throwable cause) {
		super(cause);
	}
}
