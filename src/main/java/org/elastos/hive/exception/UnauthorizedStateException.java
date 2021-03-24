package org.elastos.hive.exception;

public class UnauthorizedStateException extends RuntimeException {
	private static final long serialVersionUID = 7078907892960662444L;

	public UnauthorizedStateException() {
		super();
	}

	public UnauthorizedStateException(String message) {
		super(message);
	}

	public UnauthorizedStateException(String message, Throwable cause) {
		super(message, cause);
	}

	public UnauthorizedStateException(Throwable cause) {
		super(cause);
	}
}
