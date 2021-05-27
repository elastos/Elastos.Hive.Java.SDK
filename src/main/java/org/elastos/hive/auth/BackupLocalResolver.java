package org.elastos.hive.auth;

import org.elastos.hive.ServiceEndpoint;

public class BackupLocalResolver extends AccessTokenLocalResolver {

    public BackupLocalResolver(ServiceEndpoint serviceEndpoint, CodeResolver next) {
        super(serviceEndpoint, next);
    }

    @Override
    protected String restoreToken() {
        if (serviceEndpoint.getServiceInstanceDid() == null)
            return null;

        return storage.loadBackupCredential(serviceEndpoint.getServiceInstanceDid());
    }

    @Override
    protected void saveToken(String token) {
        if (serviceEndpoint.getServiceInstanceDid() != null)
        	storage.storeBackupCredential(serviceEndpoint.getServiceInstanceDid(), token);
    }

    @Override
    protected void clearToken() {
        if (serviceEndpoint.getServiceInstanceDid() != null)
        	storage.clearBackupCredential(serviceEndpoint.getServiceInstanceDid());
    }
}
