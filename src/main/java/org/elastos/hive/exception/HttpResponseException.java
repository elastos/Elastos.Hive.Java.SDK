package org.elastos.hive.exception;

import java.io.IOException;

public class HttpResponseException extends IOException {
    private static final long serialVersionUID = -586039279266427101L;

    public HttpResponseException() {
        super();
    }

    public HttpResponseException(String message) {
        super(message);
    }

    public HttpResponseException(String message, Throwable cause) {
        super(message, cause);
    }

    public HttpResponseException(Throwable cause) {
        super(cause);
    }
}
