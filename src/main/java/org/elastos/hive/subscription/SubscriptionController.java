package org.elastos.hive.subscription;

import org.elastos.hive.connection.NodeRPCConnection;
import org.elastos.hive.connection.NodeRPCException;
import org.elastos.hive.exception.*;

import java.io.IOException;
import java.util.List;

/**
 * The subscription controller is for subscribing the vault or the backup.
 */
public class SubscriptionController {
	private final SubscriptionAPI subscriptionAPI;

	public SubscriptionController(NodeRPCConnection connection) {
		this.subscriptionAPI = connection.createService(SubscriptionAPI.class, true);
	}

	/**
	 * Get the pricing plan list of the vault which can be used for upgrading the vault.
	 *
	 * @return The price plan list.
	 * @throws HiveException The error comes from the hive node.
	 */
	public List<PricingPlan> getVaultPricingPlanList() throws HiveException {
		try {
			return subscriptionAPI.getPricePlans("vault", null)
					.execute()
					.body()
					.getPricingPlanCollection();
		} catch (NodeRPCException e) {
			switch (e.getCode()) {
				case NodeRPCException.UNAUTHORIZED:
					throw new UnauthorizedException(e);
				case NodeRPCException.NOT_FOUND:
					throw new PricingPlanNotFoundException(e);
				default:
					throw new ServerUnknownException(e);
			}
		} catch (IOException e) {
			throw new NetworkException(e);
		}
	}

	/**
	 * Get the pricing plan for the vault by name.
	 *
	 * @param planName The name of the pricing plan.
	 * @return The pricing plan
	 * @throws HiveException The error comes from the hive node.
	 */
	public PricingPlan getVaultPricingPlan(String planName) throws HiveException {
		try {
			return subscriptionAPI.getPricePlans("vault", planName)
								.execute()
								.body()
								.getPricingPlanCollection().get(0);
		} catch (NodeRPCException e) {
			switch (e.getCode()) {
			case NodeRPCException.UNAUTHORIZED:
				throw new UnauthorizedException(e);
			case NodeRPCException.NOT_FOUND:
				throw new PricingPlanNotFoundException(e);
			default:
				throw new ServerUnknownException(e);
			}
		} catch (IOException e) {
			throw new NetworkException(e.getMessage());
		}
	}

	/**
	 * Get the details of the vault.
	 *
	 * @return The details of the vault.
	 * @throws HiveException The error comes from the hive node.
	 */
	public VaultInfo getVaultInfo() throws HiveException {
		try {
			return subscriptionAPI.getVaultInfo().execute().body();
		} catch (NodeRPCException e) {
			switch (e.getCode()) {
			case NodeRPCException.UNAUTHORIZED:
				throw new UnauthorizedException(e);
			case NodeRPCException.NOT_FOUND:
				throw new VaultNotFoundException(e);
			default:
				throw new ServerUnknownException(e);
			}
		} catch (IOException e) {
			throw new NetworkException(e.getMessage());
		}
	}

	public List<AppInfo> getAppStats() throws HiveException {
		try {
			return subscriptionAPI.getVaultAppStats().execute().body().getApps();
		} catch (NodeRPCException e) {
			switch (e.getCode()) {
				case NodeRPCException.UNAUTHORIZED:
					throw new UnauthorizedException(e);
				case NodeRPCException.NOT_FOUND:
					throw new VaultNotFoundException(e);
				default:
					throw new ServerUnknownException(e);
			}
		} catch (IOException e) {
			throw new NetworkException(e.getMessage());
		}
	}

	/**
	 * Subscribe the vault with the free pricing plan.
	 *
	 * <p>TODO: remove the parameter "credential"</p>
	 *
	 * @return The details of the new created vault.
	 * @throws HiveException The error comes from the hive node.
	 */
	public VaultInfo subscribeToVault() throws HiveException {
		try {
			return subscriptionAPI.subscribeToVault().execute().body();
		} catch (NodeRPCException e) {
			switch (e.getCode()) {
				case NodeRPCException.UNAUTHORIZED:
					throw new UnauthorizedException(e);
				case NodeRPCException.ALREADY_EXISTS:
					throw new AlreadyExistsException(e);
				default:
					throw new ServerUnknownException(e);
			}
		} catch (IOException e) {
			throw new NetworkException(e);
		}
	}

	/**
	 * Activate vault
	 *
	 * @throws HiveException The error comes from the hive node.
	 */
	public void activateVault() throws HiveException {
		try {
			subscriptionAPI.activateVault().execute();
		} catch (NodeRPCException e) {
			switch (e.getCode()) {
				case NodeRPCException.UNAUTHORIZED:
					throw new UnauthorizedException(e);
				case NodeRPCException.NOT_FOUND:
					throw new NotFoundException(e);
				default:
					throw new ServerUnknownException(e);
			}
		} catch (IOException e) {
			throw new NetworkException(e);
		}
	}

	/**
	 * Deactivate vault
	 *
	 * @throws HiveException The error comes from the hive node.
	 */
	public void deactivateVault() throws HiveException {
		try {
			subscriptionAPI.deactivateVault().execute();
		} catch (NodeRPCException e) {
			switch (e.getCode()) {
				case NodeRPCException.UNAUTHORIZED:
					throw new UnauthorizedException(e);
				case NodeRPCException.NOT_FOUND:
					throw new NotFoundException(e);
				default:
					throw new ServerUnknownException(e);
			}
		} catch (IOException e) {
			throw new NetworkException(e);
		}
	}

	/**
	 * Unsubscribe the vault.
	 *
	 * @throws HiveException The error comes from the hive node.
	 */
	public void unsubscribeVault(boolean force) throws HiveException {
		try {
			subscriptionAPI.unsubscribeVault(force).execute();
		} catch (NodeRPCException e) {
			switch (e.getCode()) {
				case NodeRPCException.UNAUTHORIZED:
					throw new UnauthorizedException();
				case NodeRPCException.NOT_FOUND:
					throw new VaultNotFoundException();
				default:
					throw new ServerUnknownException(e);
			}
		} catch (IOException e) {
			throw new NetworkException(e);
		}
	}

	/**
	 * Get the pricing plan list of the backup service which can be used for upgrading the service.
	 *
	 * @return The price plan list.
	 * @throws HiveException The error comes from the hive node.
	 */
	public List<PricingPlan> getBackupPricingPlanList() throws HiveException {
		try {
			return subscriptionAPI.getPricePlans("backup", null)
					.execute()
					.body()
					.getBackupPlans();
		} catch (NodeRPCException e) {
			switch (e.getCode()) {
				case NodeRPCException.UNAUTHORIZED:
					throw new UnauthorizedException(e);
				case NodeRPCException.NOT_FOUND:
					throw new PricingPlanNotFoundException(e);
				default:
					throw new ServerUnknownException(e);
			}
		} catch (IOException e) {
			throw new NetworkException(e);
		}
	}

	/**
	 * Get the pricing plan for the backup by name.
	 *
	 * @param planName The name of the pricing plan.
	 * @return The pricing plan
	 * @throws HiveException The error comes from the hive node.
	 */
	public PricingPlan getBackupPricingPlan(String planName) throws HiveException {
		try {
			return subscriptionAPI.getPricePlans("backup", planName).execute()
					.body().getBackupPlans().get(0);
		} catch (NodeRPCException e) {
			switch (e.getCode()) {
				case NodeRPCException.UNAUTHORIZED:
					throw new UnauthorizedException(e);
				case NodeRPCException.NOT_FOUND:
					throw new PricingPlanNotFoundException(e);
				default:
					throw new ServerUnknownException(e);
			}
		} catch (IOException e) {
			throw new NetworkException(e);
		}
	}

	/**
	 * Get the details of the backup service.
	 *
	 * @return The details of the backup service.
	 * @throws HiveException The error comes from the hive node.
	 */
	public BackupInfo getBackupInfo() throws HiveException {
		try {
	   	 	return subscriptionAPI.getBackupInfo().execute().body();
		} catch (NodeRPCException e) {
			switch (e.getCode()) {
			case NodeRPCException.UNAUTHORIZED:
				throw new UnauthorizedException(e);
			case NodeRPCException.NOT_FOUND:
				throw new BackupNotFoundException(e);
			default:
				throw new ServerUnknownException(e);
			}
		} catch (IOException e) {
			throw new NetworkException(e);
		}
	}

	/**
	 * Subscribe the backup service with the free pricing plan.
	 *
	 * @return The details of the new created backup service.
	 * @throws HiveException The error comes from the hive node.
	 */
	public BackupInfo subscribeToBackup() throws HiveException {
		try {
			return subscriptionAPI.subscribeToBackup().execute().body();
		} catch (NodeRPCException e) {
			switch (e.getCode()) {
				case NodeRPCException.UNAUTHORIZED:
					throw new UnauthorizedException(e);
				case NodeRPCException.ALREADY_EXISTS:
					throw new AlreadyExistsException(e);
				default:
					throw new ServerUnknownException(e);
			}
		} catch (IOException e) {
			throw new NetworkException(e);
		}
	}

	/**
	 * Unsubscribe the backup service.
	 *
	 * @throws HiveException The error comes from the hive node.
	 */
	public void unsubscribeBackup() throws HiveException {
		try {
			subscriptionAPI.unsubscribeBackup().execute();
		} catch (NodeRPCException e) {
			switch (e.getCode()) {
				case NodeRPCException.UNAUTHORIZED:
					throw new UnauthorizedException();
				case NodeRPCException.NOT_FOUND:
					throw new BackupNotFoundException();
				default:
					throw new ServerUnknownException(e);
			}
		} catch (IOException e) {
			throw new NetworkException(e);
		}
	}
}
