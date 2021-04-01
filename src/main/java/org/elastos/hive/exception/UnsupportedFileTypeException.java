package org.elastos.hive.exception;

public class UnsupportedFileTypeException extends RuntimeException {

	private static final long serialVersionUID = 1317688325780398765L;


	public static final String EXCEPTION = "Unsupport file type";

	public UnsupportedFileTypeException() {
		super();
	}

	public UnsupportedFileTypeException(String message) {
		super(message);
	}

	public UnsupportedFileTypeException(String message, Throwable cause) {
		super(message, cause);
	}

	public UnsupportedFileTypeException(Throwable cause) {
		super(cause);
	}
}
