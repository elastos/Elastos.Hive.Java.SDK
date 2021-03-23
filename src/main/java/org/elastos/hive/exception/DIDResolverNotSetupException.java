package org.elastos.hive.exception;

public class DIDResolverNotSetupException extends RuntimeException {
	private static final long serialVersionUID = 4868071788117985924L;
	private static final String message = "DID Resolver has not been setup before";

	public DIDResolverNotSetupException() {
		super(message);
	}

	public DIDResolverNotSetupException(Throwable cause) {
		super(cause);
	}
}
