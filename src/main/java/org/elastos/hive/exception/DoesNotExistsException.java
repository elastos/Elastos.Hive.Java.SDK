package org.elastos.hive.exception;

public class DoesNotExistsException extends HiveException {
    private static final long serialVersionUID = -586039279266427101L;

    public DoesNotExistsException() {
        super();
    }

    public DoesNotExistsException(String message) {
        super(message);
    }

    public DoesNotExistsException(String message, Throwable cause) {
        super(message, cause);
    }

    public DoesNotExistsException(Throwable cause) {
        super(cause);
    }
}
