package org.elastos.hive.exception;

public class BadContextProviderException extends IllegalArgumentException {
	private static final long serialVersionUID = -2006776914757095959L;

	public BadContextProviderException(String message) {
		super(message);
	}

	public BadContextProviderException(String message, Throwable cause) {
		super(message, cause);
	}

	public BadContextProviderException(Throwable cause) {
		super(cause);
	}
}
