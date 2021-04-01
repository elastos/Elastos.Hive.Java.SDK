package org.elastos.hive.exception;

import java.io.IOException;

public class NoEnoughSpaceException extends IOException {
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
