package org.elastos.hive.exception;

class EntityNotFoundException extends RuntimeException {
	private static final long serialVersionUID = -7579248662682963982L;

	public EntityNotFoundException() {
		super();
	}

	public EntityNotFoundException(String message) {
		super(message);
	}

	public EntityNotFoundException(String message, Throwable cause) {
		super(message, cause);
	}

	public EntityNotFoundException(Throwable cause) {
		super(cause);
	}
}
