package org.elastos.hive.exception;

public class UnauthorizedV1Exception extends HttpResponseException {
    private static final long serialVersionUID = -586039279266427101L;

    public UnauthorizedV1Exception() {
        super();
    }

    public UnauthorizedV1Exception(String message) {
        super(message);
    }

    public UnauthorizedV1Exception(String message, Throwable cause) {
        super(message, cause);
    }

    public UnauthorizedV1Exception(Throwable cause) {
        super(cause);
    }
}
