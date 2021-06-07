package org.elastos.hive.exception;

public class BackupAlreadyExistsException extends EntityAlreadyExistsException{
	private static final long serialVersionUID = 1319394694943416410L;
	private static final String message = "Backup service already exists on the given hive node";

	public BackupAlreadyExistsException() {
		super(message);
	}

	public BackupAlreadyExistsException(String message) {
		super(message);
	}

	public BackupAlreadyExistsException(String message, Throwable cause) {
		super(message, cause);
	}

	public BackupAlreadyExistsException(Throwable cause) {
		super(cause);
	}
}
