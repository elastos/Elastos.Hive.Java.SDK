package org.elastos.hive;

import org.elastos.hive.payment.UsingPlan;
import org.elastos.hive.service.BackupUsingPlan;

import java.util.concurrent.CompletableFuture;

public interface Service {

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
	CompletableFuture<UsingPlan> getVaultServiceInfo();


	/**
	 * create free backup vault service
	 * @return
	 */
	CompletableFuture<Boolean> createBackupVault();


	/**
	 * Get backup vault service info
	 * @return
	 */
	CompletableFuture<BackupUsingPlan> getBackupServiceInfo();


}
