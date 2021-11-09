package org.elastos.hive.exception;

import org.elastos.hive.connection.NodeRPCException;

public class VaultAlreadyExistsException extends AlreadyExistsException {
	private static final long serialVersionUID = -586039279266427101L;

	public VaultAlreadyExistsException() {
		super();
	}

	public VaultAlreadyExistsException(String message) {
		super(message);
	}

	public VaultAlreadyExistsException(String message, Throwable cause) {
		super(message, cause);
	}

	public VaultAlreadyExistsException(Throwable cause) {
		super(cause);
	}

	public VaultAlreadyExistsException(NodeRPCException e) {
		super(e.getMessage());
	}
}
