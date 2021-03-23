package org.elastos.hive.exception;

public class DIDResoverAlreadySetupException extends RuntimeException {
	private static final long serialVersionUID = -8823832432398445720L;
	private static final String message = "Resolver already settup, replicated setup not allowed";

	public DIDResoverAlreadySetupException() {
		super(message);
	}

	public DIDResoverAlreadySetupException(Throwable cause) {
		super(message, cause);
	}
}
