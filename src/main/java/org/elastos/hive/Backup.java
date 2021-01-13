package org.elastos.hive;


import org.elastos.hive.backup.State;

import java.util.concurrent.CompletableFuture;

public interface Backup {

	/**
	 * Get backup state
	 * @return
	 */
	CompletableFuture<State> getState();

	/**
	 * Backup hive vault to other hive node
	 * @param handler
	 * @return
	 */
	CompletableFuture<Boolean> save(BackupAuthenticationHandler handler);

	/**
	 * Restore hive vault from other hive node
	 * @param handler
	 * @return
	 */
	CompletableFuture<Boolean> restore(BackupAuthenticationHandler handler);

	/**
	 * Active hive backup data to vault
	 * @return
	 */
	CompletableFuture<Boolean> active();
}


