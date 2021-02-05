package org.elastos.hive.exception;

public class ActivityNotFoundException extends RuntimeException {

	private static final long serialVersionUID = -793493640242610604L;


	public ActivityNotFoundException() {
		super();
	}

	public ActivityNotFoundException(String message) {
		super(message);
	}

	public ActivityNotFoundException(String message, Throwable cause) {
		super(message, cause);
	}

	public ActivityNotFoundException(Throwable cause) {
		super(cause);
	}
}
