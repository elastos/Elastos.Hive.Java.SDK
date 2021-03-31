package org.elastos.hive.vault;

import org.elastos.hive.Vault;
import org.elastos.hive.connection.ConnectionManager;
import org.elastos.hive.exception.HiveException;
import org.elastos.hive.exception.NoEnoughSpaceException;
import org.elastos.hive.exception.VaultLockedException;
import org.elastos.hive.network.response.HiveResponseBody;

import javax.security.sasl.AuthenticationException;

public abstract class HiveVaultRender {
    private ConnectionManager connectionManager;

    protected HiveVaultRender(Vault vault) {
        this.connectionManager = vault.getAppContext().getConnectionManager();
    }

    public ConnectionManager getConnectionManager() {
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
        if (HiveResponseBody.msgContainsCode(e.getMessage(), 401))
            return new AuthenticationException();
        else if (HiveResponseBody.msgContainsCode(e.getMessage(), 423))
            return new VaultLockedException();
        else if (HiveResponseBody.msgContainsCode(e.getMessage(), 452))
            return new NoEnoughSpaceException();
        else
            return new HiveException(e.getMessage());
    }
}
