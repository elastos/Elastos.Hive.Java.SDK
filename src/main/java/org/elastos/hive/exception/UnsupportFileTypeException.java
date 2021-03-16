package org.elastos.hive.exception;

public class UnsupportFileTypeException extends RuntimeException {

	private static final long serialVersionUID = 1317688325780398765L;


	public static final String EXCEPTION = "Unsupport file type";

	public UnsupportFileTypeException() {
		super();
	}

	public UnsupportFileTypeException(String message) {
		super(message);
	}

	public UnsupportFileTypeException(String message, Throwable cause) {
		super(message, cause);
	}

	public UnsupportFileTypeException(Throwable cause) {
		super(cause);
	}
}
