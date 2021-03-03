package org.elastos.hive.exception;

public class BadRequestException extends RuntimeException {
	private static final long serialVersionUID = -6086008937915912537L;

	public BadRequestException() {
		super();
	}

	public BadRequestException(String message) {
		super(message);
	}

	public BadRequestException(String message, Throwable cause) {
		super(message, cause);
	}

	public BadRequestException(Throwable cause) {
		super(cause);
	}
}
