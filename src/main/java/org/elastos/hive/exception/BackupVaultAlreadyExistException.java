package org.elastos.hive.exception;

public class BackupVaultAlreadyExistException extends RuntimeException {

	private static final long serialVersionUID = -2233519241549521919L;

	public BackupVaultAlreadyExistException() {
		super();
	}

	public BackupVaultAlreadyExistException(String message) {
		super(message);
	}

	public BackupVaultAlreadyExistException(String message, Throwable cause) {
		super(message, cause);
	}

	public BackupVaultAlreadyExistException(Throwable cause) {
		super(cause);
	}
}
