package org.elastos.hive.exception;

public class NotFoundException extends RuntimeException {
	private static final long serialVersionUID = -7579248662682963982L;

	public NotFoundException() {
		super();
	}

	public NotFoundException(String message) {
		super(message);
	}

	public NotFoundException(String message, Throwable cause) {
		super(message, cause);
	}

	public NotFoundException(Throwable cause) {
		super(cause);
	}
}
