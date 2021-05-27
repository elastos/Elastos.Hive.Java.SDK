package org.elastos.hive.subscription;

import org.elastos.hive.ServiceEndpoint;
import org.elastos.hive.exception.ExceptionHandler;
import org.elastos.hive.exception.HiveException;

import java.io.IOException;

public class SubscriptionController extends ExceptionHandler {
	private SubscriptionV1API subscriptionV1API;
	private SubscriptionAPI subscriptionAPI;

	public SubscriptionController(ServiceEndpoint serviceEndpoint) {
		this.subscriptionV1API = serviceEndpoint.getConnectionManager().createService(SubscriptionV1API.class, true);
		this.subscriptionAPI = serviceEndpoint.getConnectionManager().createService(SubscriptionAPI.class);
	}

	public VaultSubscribeResponse subscribe(String credential) throws HiveException {
        try {
        	return subscriptionAPI.vaultSubscribe(credential).execute().body();
		 } catch (IOException e) {
			 throw super.toHiveException(e);
		 }
	}

	public boolean subscribeBackup() throws HiveException {
		try {
        	return subscriptionV1API.createBackupVault().execute().body().getExisting();
		 } catch (Exception e) {
			 // TODO:
			 e.printStackTrace();
			 throw new HiveException(e.getMessage());
		 }
	}

	public void unsubscribe() throws HiveException {
         try {
        	 subscriptionAPI.vaultUnsubscribe().execute();
		 } catch (IOException e) {
			 throw super.toHiveException(e);
		 }
	}

	public void activate() throws HiveException {
         try {
        	 subscriptionAPI.vaultActivate().execute();
		 } catch (IOException e) {
			 throw super.toHiveException(e);
		 }
	}

	public void deactivate() throws HiveException {
        try {
        	subscriptionAPI.vaultDeactivate().execute();
		 } catch (IOException e) {
			throw super.toHiveException(e);
		 }
	}

	public VaultInfoResponse getVaultInfo() throws HiveException {
         try {
        	return subscriptionAPI.getVaultInfo().execute().body();
		 } catch (IOException e) {
			 throw super.toHiveException(e);
		 }
	}

	public BackupInfoResponse getBackupVaultInfo() throws HiveException {
         try {
        	 return subscriptionV1API.getBackupVaultInfo().execute().body();
		 } catch (Exception e) {
			 // TODO:
			 e.printStackTrace();
			 throw new HiveException(e.getMessage());
		 }
	}
}
