package org.elastos.hive.exception;

public class VaultNoPermissionException extends HiveException {
    private static final long serialVersionUID = -586039279266427101L;

    public VaultNoPermissionException() {
        super();
    }

    public VaultNoPermissionException(String message) {
        super(message);
    }

    public VaultNoPermissionException(String message, Throwable cause) {
        super(message, cause);
    }

    public VaultNoPermissionException(Throwable cause) {
        super(cause);
    }
}
