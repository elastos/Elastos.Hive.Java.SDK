package org.elastos.hive.exception;

public class DAppNullException extends RuntimeException {

	private static final long serialVersionUID = -3427222153685882504L;

	public DAppNullException() {
		super();
	}

	public DAppNullException(String message) {
		super(message);
	}

	public DAppNullException(String message, Throwable cause) {
		super(message, cause);
	}

	public DAppNullException(Throwable cause) {
		super(cause);
	}
}
