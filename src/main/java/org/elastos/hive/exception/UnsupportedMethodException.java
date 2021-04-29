package org.elastos.hive.exception;

public class UnsupportedMethodException extends UnsupportedOperationException {
	private static final long serialVersionUID = -2812121049991227217L;
	private static final String message = "This method will be supported in the later versions";

	public UnsupportedMethodException() {
		super(message);
	}

	public UnsupportedMethodException(Throwable cause) {
		super(cause);
	}
}
