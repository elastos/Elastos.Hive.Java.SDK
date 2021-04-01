package org.elastos.hive.exception;

import java.io.IOException;

/**
 * This exception means vault have no write permission.
 * You need active vault before user it.
 */
public class VaultLockedException extends IOException {
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
