package org.elastos.hive;

import org.elastos.hive.endpoint.*;
import org.elastos.hive.exception.HiveException;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;

/**
 * This class is used to fetch some possible information from remote hive node.
 * eg. all vaults information;
 *
 * <ul>
 * <li>How many DID involved;</li>
 * <li>How many vault service running there;</li>
 * <li>How many backup service running there;</li>
 * <li>How much disk storage filled there;</li>
 * <li>etc.</li>
 * </ul>
 */
public class Provider extends ServiceEndpoint {
	private ManagementController managementController;

	public Provider(AppContext context) {
		this(context, null);
	}

	/**
	 * Create by the application context and the provider address.
	 *
	 * @param context The application context
	 * @param providerAddress The provider address
	 */
	public Provider(AppContext context, String providerAddress) {
		super(context, providerAddress);
		managementController = new ManagementController(this);
	}

	/**
	 * Get all vault service information of the hive node.
	 * The access user DID MUST be the owner of the hive node.
	 * @return vault service list.
	 */
	public CompletableFuture<List<VaultDetail>> getVaults() {
		return CompletableFuture.supplyAsync(() -> {
			try {
				return managementController.getVaults();
			} catch (HiveException | RuntimeException e) {
				throw new CompletionException(e);
			}
		});
	}

	/**
	 * Get all backup service information of the hive node.
	 * The access user DID MUST be the owner of the hive node.
	 * @return backup service list.
	 */
	public CompletableFuture<List<BackupDetail>> getBackups() {
		return CompletableFuture.supplyAsync(() -> {
			try {
				return managementController.getBackups();
			} catch (HiveException | RuntimeException e) {
				throw new CompletionException(e);
			}
		});
	}

	/**
	 * Get all users in the hive node.
	 * The access user DID MUST be the owner of the hive node.
	 * @return users list.
	 */
	public CompletableFuture<List<UserDetail>> getUsers() {
		return CompletableFuture.supplyAsync(() -> {
			try {
				return managementController.getUsers();
			} catch (HiveException | RuntimeException e) {
				throw new CompletionException(e);
			}
		});
	}

	/**
	 * Get all payments information of the hive node.
	 * The access user DID MUST be the owner of the hive node.
	 * @return payment list.
	 */
	public CompletableFuture<List<PaymentDetail>> getPayments() {
		return CompletableFuture.supplyAsync(() -> {
			try {
				return managementController.getPayments();
			} catch (HiveException | RuntimeException e) {
				throw new CompletionException(e);
			}
		});
	}

	/**
	 * Delete vault services by user DIDs.
	 * The access user DID MUST be the owner of the hive node.
	 * @param userDids user DIDs whose vault services will be removed.
	 * @return void
	 */
	public CompletableFuture<Void> deleteVaults(List<String> userDids) {
		return CompletableFuture.runAsync(() -> {
			try {
				managementController.deleteVaults(userDids);
			} catch (HiveException | RuntimeException e) {
				throw new CompletionException(e);
			}
		});
	}

	/**
	 * Delete backup services by user DIDs.
	 * The access user DID MUST be the owner of the hive node.
	 * @param userDids user DIDs whose vault services will be removed.
	 * @return void
	 */
	public CompletableFuture<Void> deleteBackups(List<String> userDids) {
		return CompletableFuture.runAsync(() -> {
			try {
				managementController.deleteBackups(userDids);
			} catch (HiveException | RuntimeException e) {
				throw new CompletionException(e);
			}
		});
	}

	/**
	 * Get all application information of the vault service.
	 * The access user DID MUST be the owner of the vault service.
	 * @return application list.
	 */
	public CompletableFuture<List<VaultAppDetail>> getVaultApps() {
		return CompletableFuture.supplyAsync(() -> {
			try {
				return managementController.getVaultApps();
			} catch (HiveException | RuntimeException e) {
				throw new CompletionException(e);
			}
		});
	}

	/**
	 * Delete applications of the vault service by application DIDs.
	 * The access user DID MUST be the owner of the vault service.
	 * @param appDids application DIDs whose data will be removed.
	 * @return void
	 */
	public CompletableFuture<Void> deleteVaultApps(List<String> appDids) {
		return CompletableFuture.runAsync(() -> {
			try {
				managementController.deleteVaultApps(appDids);
			} catch (HiveException | RuntimeException e) {
				throw new CompletionException(e);
			}
		});
	}
}
