package org.elastos.hive.exception;

public class ProviderNotFoundException extends RuntimeException {
	private static final long serialVersionUID = 2421102649824293940L;
	
	public ProviderNotFoundException() {
		super();
	}

	public ProviderNotFoundException(String message) {
		super(message);
	}

	public ProviderNotFoundException(String message, Throwable cause) {
		super(message, cause);
	}

	public ProviderNotFoundException(Throwable cause) {
		super(cause);
	}
}
