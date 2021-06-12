package org.elastos.hive.connection;

import java.io.IOException;

/**
 * Main error response exception for internal usage.
 */
public class NodeRPCException extends IOException {
    private static final long serialVersionUID = 1L;

    public static final int BAD_REQUEST     = 400;
    public static final int UNAUTHORIZED    = 401;
    public static final int NOT_FOUND       = 404;

    private final int code;
    private final int internalCode;

    public NodeRPCException(int code, int internalCode, String message) {
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
