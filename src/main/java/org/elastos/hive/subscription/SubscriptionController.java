package org.elastos.hive.subscription;

import org.elastos.hive.connection.ConnectionManager;
import org.elastos.hive.exception.*;
import org.elastos.hive.subscription.payment.PricingPlan;

import java.io.IOException;
import java.util.List;

public class SubscriptionController extends ExceptionHandler {
	private SubscriptionAPI subscriptionAPI;

	public SubscriptionController(ConnectionManager connection) {
		this.subscriptionAPI = connection.createService(SubscriptionAPI.class);
	}

	public List<PricingPlan> getVaultPricingPlanList() throws HiveException {
		try {
			return subscriptionAPI.getPricePlans("vault", "").execute().body().getPricingPlans();
		} catch (RPCException e) {
			throw new UnknownException(e);
		} catch (IOException e) {
			throw new NetworkException(e.getMessage());
		}
	}

	public PricingPlan getVaultPricingPlan(String planName) throws HiveException {
		try {
			List<PricingPlan> plans = subscriptionAPI.getPricePlans("vault", planName).execute()
					.body().getPricingPlans();
			return plans.isEmpty() ? null : plans.get(0);
		} catch (RPCException e) {
			if (e.getCode() == RPCException.NOT_FOUND)
				throw new PricingPlanNotFoundException(e.getMessage());
			else
				throw new UnknownException(e);
		} catch (IOException e) {
			throw new NetworkException(e.getMessage());
		}
	}

	public VaultInfo getVaultInfo() throws HiveException {
        try {
        	return subscriptionAPI.getVaultInfo().execute().body();
		} catch (RPCException e) {
			throw new UnknownException(e);
		} catch (IOException e) {
			throw new NetworkException(e.getMessage());
		}
	}

	public VaultInfo subscribeToVault(String credential) throws HiveException {
		if (credential != null)
			throw new NotImplementedException();

		try {
			return subscriptionAPI.subscribeToVault(credential).execute().body();
		} catch (RPCException e) {
			switch (e.getCode()) {
			case RPCException.UNAUTHORIZED:
				throw new UnauthorizedException();

			case 200:
				throw new VaultAlreadyExistException();

			default:
				throw new UnknownException(e);
			}
		} catch (IOException e) {
			throw new NetworkException(e.getMessage());
		}
	}

	public void unsubscribeVault() throws HiveException {
		try {
			subscriptionAPI.unsubscribeVault().execute();
		} catch (RPCException e) {
			if (e.getCode() == RPCException.UNAUTHORIZED)
				throw new UnauthorizedException();
			else
				throw new UnknownException(e);
		} catch (IOException e) {
			throw new NetworkException(e.getMessage());
		}
	}

	public void activateVault() throws HiveException {
		try {
			subscriptionAPI.activateVault().execute();
		} catch (RPCException e) {
			throw new UnknownException(e);
		} catch (IOException e) {
			throw new NetworkException(e.getMessage());
		}
	}

	public void deactivateVault() throws HiveException {
		try {
        	subscriptionAPI.deactivateVault().execute();
		} catch (RPCException e) {
			throw new UnknownException(e);
		} catch (IOException e) {
			throw new NetworkException(e.getMessage());
		}
	}

	public List<PricingPlan> getBackupPricingPlanList() throws HiveException {
		try {
			return subscriptionAPI.getPricePlans("backup", "").execute().body().getBackupPlans();
		} catch (RPCException e) {
			throw new UnknownException(e);
		} catch (IOException e) {
			throw new NetworkException(e.getMessage());
		}
	}

	public PricingPlan getBackupPricingPlan(String planName) throws HiveException {
		try {
			List<PricingPlan> plans = subscriptionAPI.getPricePlans("backup", planName).execute()
					.body().getBackupPlans();
			return plans.isEmpty() ? null : plans.get(0);
		} catch (RPCException e) {
			if (e.getCode() == RPCException.NOT_FOUND)
				throw new PricingPlanNotFoundException(e.getMessage());
			else
				throw new UnknownException(e);
		} catch (IOException e) {
			throw new NetworkException(e.getMessage());
		}
	}

	public BackupInfo getBackupInfo() throws HiveException {
        try {
       	 	return subscriptionAPI.getBackupInfo().execute().body();
		} catch (RPCException e) {
			throw new UnknownException(e);
		} catch (IOException e) {
			throw new NetworkException(e.getMessage());
		}
	}

	public BackupInfo subscribeToBackup(String reserved) throws HiveException {
		try {
        	return subscriptionAPI.subscribeToBackup(reserved).execute().body();
		} catch (RPCException e) {
			switch (e.getCode()) {
			case RPCException.UNAUTHORIZED:
				throw new UnauthorizedException();

			case 200:
				throw new VaultAlreadyExistException();

			default:
				throw new UnknownException(e);
			}
		} catch (IOException e) {
			throw new NetworkException(e.getMessage());
		}
	}

	public void unsubscribeBackup() throws HiveException {
        try {
			subscriptionAPI.unsubscribeBackup().execute();
		} catch (RPCException e) {
			throw new UnknownException(e);
		} catch (IOException e) {
			throw new NetworkException(e.getMessage());
		}
	}

	public void activateBackup() throws HiveException {
		try {
			// TODO: Not implemented.
			subscriptionAPI.activateBackup().execute();
		} catch (RPCException e) {
			throw new UnknownException(e);
		} catch (IOException e) {
			throw new NetworkException(e.getMessage());
		}
	}

	public void deactivateBackup() throws HiveException {
		try {
			// TODO: Not implemented.
			subscriptionAPI.deactivateBackup().execute();
		} catch (RPCException e) {
			throw new UnknownException(e);
		} catch (IOException e) {
			throw new NetworkException(e.getMessage());
		}
	}
}
