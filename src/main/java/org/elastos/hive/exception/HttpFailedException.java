package org.elastos.hive.exception;

import java.io.IOException;

public class HttpFailedException extends IOException {
    private static final long serialVersionUID = 1L;

    // response status code
    private int code;

    public HttpFailedException(int code, String message) {
        super(message);
        this.code = code;
    }

    public int getCode() {
        return code;
    }
}
