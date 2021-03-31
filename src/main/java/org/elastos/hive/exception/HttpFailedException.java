package org.elastos.hive.exception;

public class HttpFailedException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    private int code;

    public HttpFailedException(int code, String message) {
        super(message);
        this.code = code;
    }

    public int getCode() {
        return code;
    }
}
