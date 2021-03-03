package org.elastos.hive;

import org.elastos.hive.service.BackupServiceInfo;
import org.elastos.hive.service.VaultServiceInfo;

import java.util.concurrent.CompletableFuture;

public interface Management {

	/**
	 * create vault
	 *
	 * @return
	 */
	CompletableFuture<Vault> createVault();

	/**
	 * destory vault
	 *
	 * @return
	 */
	CompletableFuture<Boolean> destroyVault();

	/**
	 * freeze vault
	 *
	 * @return
	 */
	CompletableFuture<Boolean> freezeVault();

	/**
	 * unfreeze vault
	 *
	 * @return
	 */
	CompletableFuture<Boolean> unfreezeVault();

	/**
	 * create backup
	 *
	 * @return
	 */
	CompletableFuture<Backup> createBackup();

	/**
	 * get vault service information
	 *
	 * @return
	 */
	CompletableFuture<VaultServiceInfo> getVaultServiceInfo();

	/**
	 * get backup service information
	 *
	 * @return
	 */
	CompletableFuture<BackupServiceInfo> getBackupServiceInfo();

	/**
	 * Check if the vault exists
	 * @return
	 */
	CompletableFuture<Boolean> checkVaultExist();

	/**
	 * Check if the backup exists
	 * @return
	 */
	CompletableFuture<Boolean> checkBackupExist();

	/**
	 * Get interface as Payment instance
	 * @return interface instance of Payment
	 */
	Payment getPayment();

	/**
	 * Get Node version
	 * @return
	 */
	Version getVersion();

}
