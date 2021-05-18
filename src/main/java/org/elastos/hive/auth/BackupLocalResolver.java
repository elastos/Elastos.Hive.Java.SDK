package org.elastos.hive.auth;

import com.google.gson.Gson;
import org.elastos.hive.ServiceEndpoint;

public class BackupLocalResolver extends LocalResolver {

    public BackupLocalResolver(ServiceEndpoint serviceEndpoint) {
        super(serviceEndpoint);
    }

    @Override
    protected AuthToken restoreToken() {
        if (serviceEndpoint.getServiceInstanceDid() == null)
            return null;

        String tokenStr = dataStorage.loadBackupCredential(serviceEndpoint.getServiceInstanceDid());
        if (tokenStr == null)
            return null;

        return new Gson().fromJson(tokenStr, AuthTokenToBackup.class);
    }

    @Override
    protected void saveToken(AuthToken token) {
        if (serviceEndpoint.getServiceInstanceDid() != null)
            dataStorage.storeBackupCredential(serviceEndpoint.getServiceInstanceDid(), new Gson().toJson(token));
    }

    @Override
    protected void clearToken() {
        if (serviceEndpoint.getServiceInstanceDid() != null)
            dataStorage.clearBackupCredential(serviceEndpoint.getServiceInstanceDid());
    }
}
