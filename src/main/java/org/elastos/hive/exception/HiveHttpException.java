package org.elastos.hive.exception;

import java.io.IOException;

public class HiveHttpException extends IOException {
    private static final long serialVersionUID = 1L;

    private final int code;

    public HiveHttpException(int code, String message) {
        super(message);
        this.code = code;
    }

    public int getCode() {
        return code;
    }
}
