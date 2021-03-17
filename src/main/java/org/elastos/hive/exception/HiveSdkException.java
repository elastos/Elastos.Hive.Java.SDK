package org.elastos.hive.exception;

public class HiveSdkException extends RuntimeException {
	private static final long serialVersionUID = 1L;

	public HiveSdkException() {
		super();
	}

	public HiveSdkException(String message) {
		super(message);
	}

	public HiveSdkException(String message, Throwable cause) {
		super(message, cause);
	}

	public HiveSdkException(Throwable cause) {
		super(cause);
	}
}
