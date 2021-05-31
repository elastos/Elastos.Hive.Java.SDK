package org.elastos.hive.exception;

import java.io.IOException;

/**
 * Main error response exception for internal usage.
 */
public class HiveHttpException extends IOException {
    private static final long serialVersionUID = 1L;

    private final int code;
    private final int internalCode;

    public HiveHttpException(int code, int internalCode, String message) {
        super(message);
        this.code = code;
        this.internalCode = internalCode;
    }

    public int getCode() {
        return code;
    }

    public int getInternalCode() {
        return internalCode;
    }
}
