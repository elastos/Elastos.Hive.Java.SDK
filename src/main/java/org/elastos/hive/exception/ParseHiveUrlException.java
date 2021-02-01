package org.elastos.hive.exception;

public class ParseHiveUrlException extends RuntimeException {

	private static final long serialVersionUID = 3662498097028371267L;

	public ParseHiveUrlException() {
		super();
	}

	public ParseHiveUrlException(String message) {
		super(message);
	}

	public ParseHiveUrlException(String message, Throwable cause) {
		super(message, cause);
	}

	public ParseHiveUrlException(Throwable cause) {
		super(cause);
	}
}
