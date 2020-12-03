package org.elastos.hive.exception;

public class FileNotFoundException extends RuntimeException {

	private static final long serialVersionUID = -4043732547009352315L;

	public static final String EXCEPTION = "Download file not found";

	public FileNotFoundException() {
		super();
	}

	public FileNotFoundException(String message) {
		super(message);
	}

	public FileNotFoundException(String message, Throwable cause) {
		super(message, cause);
	}

	public FileNotFoundException(Throwable cause) {
		super(cause);
	}
}
