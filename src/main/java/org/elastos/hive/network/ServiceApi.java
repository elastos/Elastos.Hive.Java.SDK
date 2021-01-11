package org.elastos.hive.network;

import org.elastos.hive.backup.State;
import org.elastos.hive.payment.UsingPlan;

import java.util.concurrent.CompletableFuture;

public interface ServiceApi {

	/**
	 * create free vault service
	 * @return
	 */
	CompletableFuture<Boolean> create();


	CompletableFuture<Boolean> remove();


	CompletableFuture<Boolean> freeze();


	CompletableFuture<Boolean> unfreeze();


	CompletableFuture<UsingPlan> getServiceInfo();


	CompletableFuture<Boolean> createBackupService();


	CompletableFuture<Object> getBackupServiceInfo();


}
