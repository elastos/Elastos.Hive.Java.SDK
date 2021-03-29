package org.elastos.hive.vault;

import org.elastos.hive.AppContext;
import org.elastos.hive.connection.ConnectionManager;
import org.elastos.hive.exception.HiveException;
import org.elastos.hive.exception.VaultAlreadyExistException;
import org.elastos.hive.network.response.HiveResponseBody;
import org.elastos.hive.network.response.VaultCreateResponseBody;

import java.io.IOException;

/**
 * Helper class for subscription api.
 */
public class SubscriptionServiceRender {
    private ConnectionManager connectionManager;

    public SubscriptionServiceRender(AppContext context) {
        this.connectionManager = context.getConnectionManager();
    }

    public void subscribe() throws HiveException {
        try {
            VaultCreateResponseBody respBody = connectionManager.getSubscriptionApi()
                    .createVault()
                    .execute()
                    .body();
            if (HiveResponseBody.validateBody(respBody).getExisting()) {
                throw new VaultAlreadyExistException("The vault already exists");
            }
        } catch (IOException | HiveException e) {
            throw new HiveException(e.getMessage());
        }
    }

    public void subscribeBackup() throws HiveException {
        try {
            VaultCreateResponseBody respBody = connectionManager.getSubscriptionApi()
                    .createBackupVault()
                    .execute()
                    .body();
            if (HiveResponseBody.validateBody(respBody).getExisting()) {
                throw new VaultAlreadyExistException("The backup vault already exists");
            }
        } catch (IOException | HiveException e) {
            throw new HiveException(e.getMessage());
        }
    }

    public void unsubscribe() throws HiveException {
        try {
            HiveResponseBody.validateBody(connectionManager.getSubscriptionApi()
                    .removeVault()
                    .execute()
                    .body());
        } catch (IOException | HiveException e) {
            throw new HiveException(e.getMessage());
        }
    }

    public void activate() throws HiveException {
        try {
            HiveResponseBody.validateBody(connectionManager.getSubscriptionApi()
                    .unfreeze()
                    .execute()
                    .body());
        } catch (IOException | HiveException e) {
            throw new HiveException(e.getMessage());
        }
    }

    public void deactivate() throws HiveException {
        try {
            HiveResponseBody.validateBody(connectionManager.getSubscriptionApi()
                    .freeze()
                    .execute()
                    .body());
        } catch (IOException | HiveException e) {
            throw new HiveException(e.getMessage());
        }
    }
}
