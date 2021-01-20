package org.elastos.hive;

import org.elastos.hive.payment.UsingPlan;
import org.elastos.hive.service.BackupUsingPlan;

import java.util.concurrent.CompletableFuture;

public class Manager {
	private String providerAddress;
	private String ownerDid;
	private AuthHelper authHelper;

	private ServiceManager serviceManager;

	Manager(AuthHelper authHelper, String providerAddress, String ownerDid) {
		this.authHelper = authHelper;
		this.providerAddress = providerAddress;
		this.ownerDid = ownerDid;

		this.serviceManager = new ServiceManagerImpl(authHelper);
	}

	/**
	 * create vault
	 * @return
	 */
	public CompletableFuture<Vault> createVault() {
		return this.serviceManager.createVault()
				.thenApplyAsync(aBoolean -> aBoolean?new Vault(this.authHelper, this.providerAddress, this.ownerDid):null);
	}

	/**
	 * destory vault
	 * @return
	 */
	public CompletableFuture<Boolean> destroyVault() {
		return this.serviceManager.removeVault();
	}

	/**
	 * freeze vault
	 * @return
	 */
	public CompletableFuture<Boolean> freezeVault() {
		return this.serviceManager.freezeVault();
	}

	/**
	 * unfreeze vault
	 * @return
	 */
	public CompletableFuture<Boolean> unfreezeVault() {
		return this.serviceManager.unfreezeVault();
	}

	/**
	 * create backup
	 * @return
	 */
	public CompletableFuture<Boolean> createBackup() {
		return this.serviceManager.createBackupVault()
				.handleAsync((aBoolean, throwable) -> (aBoolean && throwable==null));
	}

	/**
	 * get vault service information
	 * @return
	 */
	public CompletableFuture<UsingPlan> getVaultServiceInfo() {
		return this.serviceManager.getVaultServiceInfo();
	}

	/**
	 * get backup service information
	 * @return
	 */
	public CompletableFuture<BackupUsingPlan> getBackupServiceInfo() {
		return this.serviceManager.getBackupServiceInfo();
	}

	//TODO 是否保留，待定
	public CompletableFuture<Boolean> checkVaultExist() {
		return this.serviceManager.getVaultServiceInfo()
				.handleAsync((usingPlan, throwable) ->
						(null==throwable && usingPlan!=null));
	}

	//TODO 是否保留，待定
	public CompletableFuture<Boolean> checkBackupVaultExist() {
		return this.serviceManager.getBackupServiceInfo()
				.handleAsync((usingPlan, throwable) -> (null==throwable && usingPlan!=null));
	}
}
