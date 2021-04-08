package org.elastos.hive.vault;

import org.elastos.hive.AppContext;
import org.elastos.hive.ServiceEndpoint;
import org.elastos.hive.connection.ConnectionManager;

public abstract class HiveVaultRender {
    private AppContext context;
    private ServiceEndpoint serviceEndpoint;

    protected HiveVaultRender(ServiceEndpoint serviceEndpoint) {
        this.serviceEndpoint = serviceEndpoint;
        this.context = serviceEndpoint.getAppContext();
    }

    protected AppContext getAppContext() {
        return this.context;
    }

    protected ServiceEndpoint getServiceEndpoint() {
        return this.serviceEndpoint;
    }

    protected ConnectionManager getConnectionManager() {
        return this.serviceEndpoint.getConnectionManager();
    }
}
