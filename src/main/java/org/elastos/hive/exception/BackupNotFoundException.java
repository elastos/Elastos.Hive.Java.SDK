package org.elastos.hive.exception;

public class BackupNotFoundException extends RuntimeException {

	private static final long serialVersionUID = -640602773357766888L;

	public BackupNotFoundException() {
		super();
	}

	public BackupNotFoundException(String message) {
		super(message);
	}

	public BackupNotFoundException(String message, Throwable cause) {
		super(message, cause);
	}

	public BackupNotFoundException(Throwable cause) {
		super(cause);
	}
}
