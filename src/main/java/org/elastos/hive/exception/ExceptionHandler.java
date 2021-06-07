package org.elastos.hive.exception;

import java.io.IOException;

public class ExceptionHandler {
    protected static final int UNCAUGHT_EXCEPTION = 100001;
    protected static final int UNAUTHORIZED = 100002;
    protected static final int VAULT_NOT_FOUND = 100003;
    protected static final int VAULT_NO_PERMISSION = 100004;
    protected static final int INVALID_PARAMETER = 100005;
    protected static final int ALREADY_EXISTS = 100006;
    protected static final int DOES_NOT_EXISTS = 100007;
    protected static final int SCRIPT_NOT_FOUND = 120001;

    protected boolean isHiveNodeException(IOException e) {
        return e instanceof RPCException;
    }

    protected HiveException toHiveException(IOException e) {
        if (isHiveNodeException(e)) {
            RPCException ex = (RPCException)e;
            if (ex.getCode() == UNCAUGHT_EXCEPTION)
                return new UncaughtException(ex.getMessage());
            else if (ex.getCode() == UNAUTHORIZED)
                return new InvalidParameterException(ex.getMessage());
            else if (ex.getCode() == VAULT_NOT_FOUND)
                return new VaultNotFoundException(ex.getMessage());
            else if (ex.getCode() == VAULT_NO_PERMISSION)
                return new VaultNoPermissionException(ex.getMessage());
            else if (ex.getCode() == INVALID_PARAMETER)
                return new UnauthorizedException(ex.getMessage());
            else if (ex.getCode() == ALREADY_EXISTS)
                return new AlreadyExistsException(ex.getMessage());
            else if (ex.getCode() == DOES_NOT_EXISTS)
                return new DoesNotExistsException(ex.getMessage());
            else if (ex.getCode() == SCRIPT_NOT_FOUND)
                return new UnauthorizedException(ex.getMessage());
            else
                return new HiveException(ex.getMessage());
        }
        return new HiveException(e.getMessage());
    }
}
