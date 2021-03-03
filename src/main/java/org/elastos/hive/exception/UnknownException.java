package org.elastos.hive.exception;

public class UnknownException extends RuntimeException {
	private static final long serialVersionUID = 5210865275817148567L;

	public UnknownException() {
		super();
	}

	public UnknownException(String message) {
		super(message);
	}

	public UnknownException(String message, Throwable cause) {
		super(message, cause);
	}

	public UnknownException(Throwable cause) {
		super(cause);
	}
}
