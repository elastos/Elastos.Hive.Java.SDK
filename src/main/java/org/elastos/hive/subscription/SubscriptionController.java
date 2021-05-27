package org.elastos.hive.subscription;

import org.elastos.hive.ServiceEndpoint;
import org.elastos.hive.exception.ExceptionHandler;
import org.elastos.hive.exception.HiveException;
import java.io.IOException;

public class SubscriptionController extends ExceptionHandler {
	private SubscriptionAPI subscriptionAPI;

	public SubscriptionController(ServiceEndpoint serviceEndpoint) {
		this.subscriptionAPI = serviceEndpoint.getConnectionManager().createService(SubscriptionAPI.class);
	}

	public VaultInfo getVaultInfo() throws HiveException {
        try {
       	return subscriptionAPI.getVaultInfo().execute().body();
		 } catch (IOException e) {
			 throw super.toHiveException(e);
		 }
	}

	public VaultInfo subscribeToVault(String credential) throws HiveException {
        try {
        	return subscriptionAPI.subscribeToVault(credential).execute().body();
		 } catch (IOException e) {
			 throw super.toHiveException(e);
		 }
	}

	public void unsubscribeVault() throws HiveException {
         try {
        	 subscriptionAPI.unsubscribeVault().execute();
		 } catch (IOException e) {
			 throw super.toHiveException(e);
		 }
	}

	public void activateVault() throws HiveException {
         try {
        	 subscriptionAPI.activateVault().execute();
		 } catch (IOException e) {
			 throw super.toHiveException(e);
		 }
	}

	public void deactivateVault() throws HiveException {
        try {
        	subscriptionAPI.deactivateVault().execute();
		 } catch (IOException e) {
			throw super.toHiveException(e);
		 }
	}

	public BackupInfo getBackupInfo() throws HiveException {
        try {
       	 return subscriptionAPI.getBackupInfo().execute().body();
		 } catch (Exception e) {
			 // TODO:
			 e.printStackTrace();
			 throw new HiveException(e.getMessage());
		 }
	}

	public BackupInfo subscribeToBackup(String reserved) throws HiveException {
		try {
        	return subscriptionAPI.subscribeToBackup(reserved).execute().body();
		 } catch (Exception e) {
			 // TODO:
			 e.printStackTrace();
			 throw new HiveException(e.getMessage());
		 }
	}

	public void unsubscribeBackup() throws HiveException {
        try {
       	 subscriptionAPI.unsubscribeVault().execute();
		 } catch (IOException e) {
			 throw super.toHiveException(e);
		 }
	}

	public void activateBackup() throws HiveException {
        try {
       	 subscriptionAPI.activateBackup().execute();
		 } catch (IOException e) {
			 throw super.toHiveException(e);
		 }
	}

	public void deactivateBackup() throws HiveException {
       try {
       	subscriptionAPI.deactivateBackup().execute();
		 } catch (IOException e) {
			throw super.toHiveException(e);
		 }
	}
}
