package org.elastos.hive.exception;

public class UnauthorizedException extends SecurityException {
    private static final long serialVersionUID = -586039279266427101L;

    public UnauthorizedException() {
        super();
    }

    public UnauthorizedException(RPCException e) {
        super(e.getMessage());
    }

    public UnauthorizedException(String message) {
        super(message);
    }

    public UnauthorizedException(String message, Throwable cause) {
        super(message, cause);
    }

    public UnauthorizedException(Throwable cause) {
        super(cause);
    }
}
