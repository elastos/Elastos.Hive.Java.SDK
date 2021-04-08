package org.elastos.hive.vault;

import org.elastos.hive.AppContext;
import org.elastos.hive.Backup;
import org.elastos.hive.Vault;
import org.elastos.hive.connection.ConnectionManager;

public abstract class HiveVaultRender {
    private AppContext context;
    private Vault vault;
    private Backup backup;
    private ConnectionManager connectionManager;

    protected HiveVaultRender(Vault vault) {
        this.vault = vault;
        this.context = vault.getAppContext();
        this.connectionManager = vault.getAppContext().getConnectionManager();
    }

    protected HiveVaultRender(Backup backup) {
        this.backup = backup;
        this.context = backup.getAppContext();
        this.connectionManager = backup.getAppContext().getConnectionManager();
    }

    protected HiveVaultRender(AppContext context) {
        this.context = context;
        this.connectionManager = context.getConnectionManager();
    }

    protected HiveVaultRender(AppContext context, ConnectionManager connectionManager) {
        this.context = context;
        this.connectionManager = connectionManager;
    }

    protected Vault getVault() {
        return this.vault;
    }

    protected Backup getBackup() {
        return this.backup;
    }

    protected AppContext getAppContext() {
        return this.context;
    }

    protected ConnectionManager getConnectionManager() {
        return this.connectionManager;
    }
}
