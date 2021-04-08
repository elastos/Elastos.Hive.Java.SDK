package org.elastos.hive.vault;

import org.elastos.hive.AppContext;
import org.elastos.hive.ServiceEndpoint;
import org.elastos.hive.connection.ConnectionManager;

public abstract class HiveVaultRender {
    private AppContext context;
    private ServiceEndpoint serviceEndpoint;
    private ConnectionManager connectionManager;

    protected HiveVaultRender(ServiceEndpoint serviceEndpoint) {
        this.serviceEndpoint = serviceEndpoint;
        this.context = serviceEndpoint.getAppContext();
        this.connectionManager = serviceEndpoint.getConnectionManager();
    }

    protected HiveVaultRender(AppContext context, ConnectionManager connectionManager) {
        this.context = context;
        this.connectionManager = connectionManager;
    }

    protected AppContext getAppContext() {
        return this.context;
    }

    protected ServiceEndpoint getServiceEndpoint() {
        return this.serviceEndpoint;
    }

    protected ConnectionManager getConnectionManager() {
        return this.connectionManager;
    }
}
