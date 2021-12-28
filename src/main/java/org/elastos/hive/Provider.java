package org.elastos.hive;

import org.elastos.hive.endpoint.*;
import org.elastos.hive.exception.HiveException;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;

/**
 * This class is used to fetch some possible information from remote hive node.
 * eg. version;
 *
 * <ul>
 * <li>Latest commit Id;</li>
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

	public CompletableFuture<List<VaultDetail>> getVaults() {
		return CompletableFuture.supplyAsync(() -> {
			try {
				return managementController.getVaults();
			} catch (HiveException | RuntimeException e) {
				throw new CompletionException(e);
			}
		});
	}

	public CompletableFuture<List<BackupDetail>> getBackups() {
		return CompletableFuture.supplyAsync(() -> {
			try {
				return managementController.getBackups();
			} catch (HiveException | RuntimeException e) {
				throw new CompletionException(e);
			}
		});
	}

	public CompletableFuture<List<UserDetail>> getUsers() {
		return CompletableFuture.supplyAsync(() -> {
			try {
				return managementController.getUsers();
			} catch (HiveException | RuntimeException e) {
				throw new CompletionException(e);
			}
		});
	}

	public CompletableFuture<List<PaymentDetail>> getPayments() {
		return CompletableFuture.supplyAsync(() -> {
			try {
				return managementController.getPayments();
			} catch (HiveException | RuntimeException e) {
				throw new CompletionException(e);
			}
		});
	}

	public CompletableFuture<Void> deleteVaults(List<String> userDids) {
		return CompletableFuture.runAsync(() -> {
			try {
				managementController.deleteVaults(userDids);
			} catch (HiveException | RuntimeException e) {
				throw new CompletionException(e);
			}
		});
	}

	public CompletableFuture<Void> deleteBackups(List<String> userDids) {
		return CompletableFuture.runAsync(() -> {
			try {
				managementController.deleteBackups(userDids);
			} catch (HiveException | RuntimeException e) {
				throw new CompletionException(e);
			}
		});
	}

	public CompletableFuture<List<VaultAppDetail>> getVaultApps() {
		return CompletableFuture.supplyAsync(() -> {
			try {
				return managementController.getVaultApps();
			} catch (HiveException | RuntimeException e) {
				throw new CompletionException(e);
			}
		});
	}

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
