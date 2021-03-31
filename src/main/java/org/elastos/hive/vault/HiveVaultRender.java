package org.elastos.hive.vault;

import org.elastos.hive.Vault;
import org.elastos.hive.connection.ConnectionManager;
import org.elastos.hive.exception.HiveException;
import org.elastos.hive.exception.HttpFailedException;
import org.elastos.hive.exception.NoEnoughSpaceException;
import org.elastos.hive.exception.VaultLockedException;

import javax.security.sasl.AuthenticationException;

public abstract class HiveVaultRender {
    private Vault vault;
    private ConnectionManager connectionManager;

    protected HiveVaultRender(Vault vault) {
        this.vault = vault;
        this.connectionManager = vault.getAppContext().getConnectionManager();
    }

    protected Vault getVault() {
        return this.vault;
    }

    protected ConnectionManager getConnectionManager() {
        return this.connectionManager;
    }

    /**
     * Common exception conversion for response code.
     * Original exception comes from:
     *  1.class RequestInterceptor when handling response code.
     *  2.other sync/async logic.
     * Every service can override this for defining more specific ones.
     * @param e exception from http calling.
     * @return expect exception already defined.
     */
    protected Exception convertException(Exception e) {
        if (e instanceof HttpFailedException) {
            HttpFailedException ex = (HttpFailedException) e;
            switch (ex.getCode()) {
                case 401:
                    return new AuthenticationException();
                case 423:
                    return new VaultLockedException();
                case 452:
                    return new NoEnoughSpaceException();
                default:
                    break;
            }
        }
        return new HiveException(e.getMessage());
    }
}
