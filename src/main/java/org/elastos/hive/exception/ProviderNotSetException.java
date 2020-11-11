package org.elastos.hive.exception;

public class ProviderNotSetException extends RuntimeException {

	private static final long serialVersionUID = -2678382770014547654L;

	public static final String EXCEPTION = "Provider not set";

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
