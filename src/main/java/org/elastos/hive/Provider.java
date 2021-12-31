package org.elastos.hive;

import org.elastos.hive.exception.HiveException;
import org.elastos.hive.provider.BackupDetail;
import org.elastos.hive.provider.FilledOrderDetail;
import org.elastos.hive.provider.RunStatsController;
import org.elastos.hive.provider.VaultDetail;

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
	private RunStatsController managementController;

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
		managementController = new RunStatsController(this);
	}

	/**
	 * Get all vault service stats of the hive node.
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
	 * Get all backup service stats of the hive node.
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
	 * Get all filled order stats of the hive node.
	 * The access user DID MUST be the owner of the hive node.
	 * @return payment list.
	 */
	public CompletableFuture<List<FilledOrderDetail>> getFilledOrders() {
		return CompletableFuture.supplyAsync(() -> {
			try {
				return managementController.getFilledOrders();
			} catch (HiveException | RuntimeException e) {
				throw new CompletionException(e);
			}
		});
	}

}
