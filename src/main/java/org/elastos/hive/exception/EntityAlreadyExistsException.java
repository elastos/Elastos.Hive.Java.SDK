package org.elastos.hive.exception;

public class EntityAlreadyExistsException extends HiveException {
    private static final long serialVersionUID = -586039279266427101L;

    public EntityAlreadyExistsException() {
        super();
    }

    public EntityAlreadyExistsException(String message) {
        super(message);
    }

    public EntityAlreadyExistsException(String message, Throwable cause) {
        super(message, cause);
    }

    public EntityAlreadyExistsException(Throwable cause) {
        super(cause);
    }
}
