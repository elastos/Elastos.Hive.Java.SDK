package org.elastos.hive.exception;

public class ProviderNotSetException extends RuntimeException {

	private static final long serialVersionUID = -2957612675751731404L;


	public ProviderNotSetException() {
		super();
	}

	public ProviderNotSetException(String message) {
		super(message);
	}

	public ProviderNotSetException(String message, Throwable cause) {
		super(message, cause);
	}

	public ProviderNotSetException(Throwable cause) {
		super(cause);
	}
}
