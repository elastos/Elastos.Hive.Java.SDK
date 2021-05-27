package org.elastos.hive.exception;

public class InvalidParameterV1Exception extends RuntimeException {
    private static final long serialVersionUID = 1L;

    public InvalidParameterV1Exception() {
        super();
    }

    public InvalidParameterV1Exception(String message) {
        super(message);
    }

    public InvalidParameterV1Exception(String message, Throwable cause) {
        super(message, cause);
    }

    public InvalidParameterV1Exception(Throwable cause) {
        super(cause);
    }
}
