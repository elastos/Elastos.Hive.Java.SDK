package org.elastos.hive.exception;

public class NoEnoughSpaceException extends Exception {
	private static final long serialVersionUID = 1L;

	public NoEnoughSpaceException() {
		super();
	}

	public NoEnoughSpaceException(String message) {
		super(message);
	}

	public NoEnoughSpaceException(String message, Throwable cause) {
		super(message, cause);
	}

	public NoEnoughSpaceException(Throwable cause) {
		super(cause);
	}
}
