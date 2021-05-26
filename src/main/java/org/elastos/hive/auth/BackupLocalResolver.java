package org.elastos.hive.auth;

import org.elastos.hive.ServiceEndpoint;

public class BackupLocalResolver extends LocalResolver {

    public BackupLocalResolver(ServiceEndpoint serviceEndpoint) {
        super(serviceEndpoint);
    }

    @Override
    protected String restoreToken() {
        if (serviceEndpoint.getServiceInstanceDid() == null)
            return null;

        return dataStorage.loadBackupCredential(serviceEndpoint.getServiceInstanceDid());
    }

    @Override
    protected void saveToken(String token) {
        if (serviceEndpoint.getServiceInstanceDid() != null)
            dataStorage.storeBackupCredential(serviceEndpoint.getServiceInstanceDid(), token);
    }

    @Override
    protected void clearToken() {
        if (serviceEndpoint.getServiceInstanceDid() != null)
            dataStorage.clearBackupCredential(serviceEndpoint.getServiceInstanceDid());
    }
}
