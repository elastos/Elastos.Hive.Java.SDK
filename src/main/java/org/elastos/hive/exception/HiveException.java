package org.elastos.hive.exception;

/**
 * Base class of other hive exceptions.
 * Most of the sub-classes is the wrapper of the error comes from the backend APIs.
 */
public class HiveException extends Exception {
	private static final long serialVersionUID = 1L;
	
	public HiveException() {
		super();
	}

	public HiveException(String message) {
		super(message);
	}

	public HiveException(String message, Throwable cause) {
		super(message, cause);
	}

	public HiveException(Throwable cause) {
		super(cause);
	}
}
