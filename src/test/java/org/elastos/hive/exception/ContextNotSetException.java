package org.elastos.hive.exception;

public class ContextNotSetException extends RuntimeException{

	private static final long serialVersionUID = -1454563437052905659L;

	public ContextNotSetException() {
		super();
	}

	public ContextNotSetException(String message) {
		super(message);
	}

	public ContextNotSetException(String message, Throwable cause) {
		super(message, cause);
	}

	public ContextNotSetException(Throwable cause) {
		super(cause);
	}
}
