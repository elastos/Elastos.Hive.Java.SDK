package org.elastos.hive.exception;

public class UncaughtException extends HiveException {
    private static final long serialVersionUID = -586039279266427101L;

    public UncaughtException() {
        super();
    }

    public UncaughtException(String message) {
        super(message);
    }

    public UncaughtException(String message, Throwable cause) {
        super(message, cause);
    }

    public UncaughtException(Throwable cause) {
        super(cause);
    }
}
