package org.elastos.hive.vault;

import org.elastos.hive.AppContext;
import org.elastos.hive.Vault;
import org.elastos.hive.connection.ConnectionManager;

public abstract class HiveVaultRender {
    private AppContext context;
    private Vault vault;
    private ConnectionManager connectionManager;

    protected HiveVaultRender(Vault vault) {
        this.vault = vault;
        this.context = vault.getAppContext();
        this.connectionManager = vault.getAppContext().getConnectionManager();
    }

    protected HiveVaultRender(AppContext context) {
        this.context = context;
        this.connectionManager = context.getConnectionManager();
    }

    protected Vault getVault() {
        return this.vault;
    }

    protected AppContext getAppContext() {
        return this.context;
    }

    protected ConnectionManager getConnectionManager() {
        return this.connectionManager;
    }
}
