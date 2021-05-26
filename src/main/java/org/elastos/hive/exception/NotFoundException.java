package org.elastos.hive.exception;

public class NotFoundException extends HttpResponseException {
    private static final long serialVersionUID = -586039279266427101L;

    public NotFoundException() {
        super();
    }

    public NotFoundException(String message) {
        super(message);
    }

    public NotFoundException(String message, Throwable cause) {
        super(message, cause);
    }

    public NotFoundException(Throwable cause) {
        super(cause);
    }
}
