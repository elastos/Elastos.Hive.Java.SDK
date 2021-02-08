package org.elastos.hive.exception;

public class DidNetTypeException extends RuntimeException {

	private static final long serialVersionUID = -1183962675274591515L;

	public DidNetTypeException() {
		super();
	}

	public DidNetTypeException(String message) {
		super(message);
	}

	public DidNetTypeException(String message, Throwable cause) {
		super(message, cause);
	}

	public DidNetTypeException(Throwable cause) {
		super(cause);
	}
}
