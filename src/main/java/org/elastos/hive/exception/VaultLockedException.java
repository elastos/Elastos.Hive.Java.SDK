package org.elastos.hive.exception;

public class VaultLockedException extends Exception {
	private static final long serialVersionUID = 1L;

	public VaultLockedException() {
		super();
	}

	public VaultLockedException(String message) {
		super(message);
	}

	public VaultLockedException(String message, Throwable cause) {
		super(message, cause);
	}

	public VaultLockedException(Throwable cause) {
		super(cause);
	}
}
