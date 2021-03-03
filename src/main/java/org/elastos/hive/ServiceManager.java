package org.elastos.hive;

import org.elastos.hive.service.BackupServiceInfo;
import org.elastos.hive.service.VaultServiceInfo;

import java.util.concurrent.CompletableFuture;

interface ServiceManager {

	/**
	 * create free vault service
	 * @return
	 */
	CompletableFuture<Boolean> createVault();


	/**
	 * remove vault service
	 * @return
	 */
	CompletableFuture<Boolean> removeVault();

	/**
	 * freeze vault service
	 * @return
	 */
	CompletableFuture<Boolean> freezeVault();

	/**
	 * unfreeze vault service
	 * @return
	 */
	CompletableFuture<Boolean> unfreezeVault();


	/**
	 * Get vault service info
	 * @return
	 */
	CompletableFuture<VaultServiceInfo> getVaultServiceInfo();


	/**
	 * create free backup vault service
	 * @return
	 */
	CompletableFuture<Boolean> createBackup();


	/**
	 * Get backup vault service info
	 * @return
	 */
	CompletableFuture<BackupServiceInfo> getBackupServiceInfo();


}
