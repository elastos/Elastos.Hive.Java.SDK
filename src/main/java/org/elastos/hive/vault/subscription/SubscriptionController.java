package org.elastos.hive.vault.subscription;

import org.elastos.hive.ServiceEndpoint;
import org.elastos.hive.connection.HiveResponseBody;

import java.io.IOException;

public class SubscriptionController {
	private SubscriptionAPI subscriptionAPI;

	public SubscriptionController(ServiceEndpoint serviceEndpoint) {
		this.subscriptionAPI = serviceEndpoint.getConnectionManager().createService(SubscriptionAPI.class, true);
	}

	public boolean subscribe() throws IOException {
        return Boolean.TRUE.equals(HiveResponseBody.validateBody(
        		subscriptionAPI.createVault()
                        .execute()
                        .body()).getExisting());
	}

	public boolean subscribeBackup() throws IOException {
		return Boolean.TRUE.equals(HiveResponseBody.validateBody(
        		subscriptionAPI.createBackupVault()
                        .execute()
                        .body()).getExisting());
	}

	public void unsubscribe() throws IOException {
        HiveResponseBody.validateBody(subscriptionAPI.removeVault().execute().body());
	}

	public void activate() throws IOException {
        HiveResponseBody.validateBody(subscriptionAPI.unfreeze().execute().body());
	}

	public void deactivate() throws IOException {
        HiveResponseBody.validateBody(subscriptionAPI.freeze().execute().body());
	}

	public VaultInfoResponseBody getVaultInfo() throws IOException {
        return HiveResponseBody.validateBody(subscriptionAPI.getVaultInfo().execute().body());
	}

	public VaultInfoResponseBody getBackupVaultInfo() throws IOException {
        return HiveResponseBody.validateBody(subscriptionAPI.getBackupVaultInfo().execute().body());
	}
}
