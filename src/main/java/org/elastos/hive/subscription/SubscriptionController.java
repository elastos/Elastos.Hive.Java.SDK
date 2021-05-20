package org.elastos.hive.subscription;

import org.elastos.hive.ServiceEndpoint;
import org.elastos.hive.exception.HiveException;

public class SubscriptionController {
	private SubscriptionAPI subscriptionAPI;

	public SubscriptionController(ServiceEndpoint serviceEndpoint) {
		this.subscriptionAPI = serviceEndpoint.getConnectionManager().createService(SubscriptionAPI.class, true);
	}

	public boolean subscribe() throws HiveException {
        try {
        	return subscriptionAPI.createVault().execute().body().getExisting();
		 } catch (Exception e) {
			 // TODO:
			 e.printStackTrace();
			 throw new HiveException(e.getMessage());
		 }
	}

	public boolean subscribeBackup() throws HiveException {
		try {
        	return subscriptionAPI.createBackupVault().execute().body().getExisting();
		 } catch (Exception e) {
			 // TODO:
			 e.printStackTrace();
			 throw new HiveException(e.getMessage());
		 }
	}

	public void unsubscribe() throws HiveException {
         try {
        	subscriptionAPI.removeVault().execute().body();
		 } catch (Exception e) {
			 // TODO:
			 e.printStackTrace();
			 throw new HiveException(e.getMessage());
		 }
	}

	public void activate() throws HiveException {
         try {
        	subscriptionAPI.unfreeze().execute().body();
		 } catch (Exception e) {
			 // TODO:
			 e.printStackTrace();
			 throw new HiveException(e.getMessage());
		 }
	}

	public void deactivate() throws HiveException {
        try {
        	subscriptionAPI.freeze().execute().body();
		 } catch (Exception e) {
			 // TODO:
			 e.printStackTrace();
			 throw new HiveException(e.getMessage());
		 }
	}

	public VaultInfoResponseBody getVaultInfo() throws HiveException {
         try {
        	return subscriptionAPI.getVaultInfo().execute().body();
		 } catch (Exception e) {
			 // TODO:
			 e.printStackTrace();
			 throw new HiveException(e.getMessage());
		 }
	}

	public VaultInfoResponseBody getBackupVaultInfo() throws HiveException {
         try {
        	 return subscriptionAPI.getBackupVaultInfo().execute().body();
		 } catch (Exception e) {
			 // TODO:
			 e.printStackTrace();
			 throw new HiveException(e.getMessage());
		 }
	}
}
