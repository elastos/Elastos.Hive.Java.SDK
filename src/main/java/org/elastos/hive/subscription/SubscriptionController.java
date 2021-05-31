package org.elastos.hive.subscription;

import org.elastos.hive.connection.ConnectionManager;
import org.elastos.hive.exception.ExceptionHandler;
import org.elastos.hive.exception.HiveException;
import org.elastos.hive.subscription.payment.PricingPlan;

import java.io.IOException;
import java.util.List;

public class SubscriptionController extends ExceptionHandler {
	private SubscriptionAPI subscriptionAPI;

	public SubscriptionController(ConnectionManager connection) {
		this.subscriptionAPI = connection.createService(SubscriptionAPI.class);
	}

	public List<PricingPlan> getPricingPlanList() throws HiveException {
		try {
			return subscriptionAPI.getPricePlans("vault", "").execute().body().getPricingPlans();
		} catch (IOException e) {
			throw super.toHiveException(e);
		}
	}

	public PricingPlan getPricingPlan(String planName) throws HiveException {
		try {
			List<PricingPlan> plans = subscriptionAPI.getPricePlans("vault", planName).execute()
					.body().getPricingPlans();
			return plans.isEmpty() ? null : plans.get(0);
		} catch (IOException e) {
			throw super.toHiveException(e);
		}
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

	public List<PricingPlan> getBackupPlanList() throws HiveException {
		try {
			return subscriptionAPI.getPricePlans("backup", "").execute().body().getBackupPlans();
		} catch (IOException e) {
			throw super.toHiveException(e);
		}
	}

	public PricingPlan getBackupPlan(String planName) throws HiveException {
		try {
			List<PricingPlan> plans = subscriptionAPI.getPricePlans("backup", planName).execute()
					.body().getBackupPlans();
			return plans.isEmpty() ? null : plans.get(0);
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
