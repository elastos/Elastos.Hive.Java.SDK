package org.elastos.hive.exception;

public class BackupAlreadyExistException extends RuntimeException{

	private static final long serialVersionUID = 1319394694943416410L;

	public BackupAlreadyExistException() {
		super();
	}

	public BackupAlreadyExistException(String message) {
		super(message);
	}

	public BackupAlreadyExistException(String message, Throwable cause) {
		super(message, cause);
	}

	public BackupAlreadyExistException(Throwable cause) {
		super(cause);
	}
}
