package org.elastos.hive.exception;

public class ScriptNotFoundException extends EntityNotFoundException {
    private static final long serialVersionUID = -586039279266427101L;

    public ScriptNotFoundException() {
        super();
    }

    public ScriptNotFoundException(String message) {
        super(message);
    }

    public ScriptNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }

    public ScriptNotFoundException(Throwable cause) {
        super(cause);
    }
}
