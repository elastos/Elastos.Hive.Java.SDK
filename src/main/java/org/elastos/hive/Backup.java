package org.elastos.hive;


import org.elastos.hive.backup.State;

import java.util.concurrent.CompletableFuture;

public interface Backup {

	/**
	 * Get backup state
	 * @return
	 */
	CompletableFuture<State> state();

	/**
	 * Backup hive vault to other hive node
	 * @param credential
	 * @param handler
	 * @return
	 */
	CompletableFuture<Boolean> save(String credential, BackupAuthenticationHandler handler);

	/**
	 * Restore hive vault from other hive node
	 * @param credential
	 * @return
	 */
	CompletableFuture<Boolean> restore(String credential);

	/**
	 * Active hive backup data to vault
	 * @return
	 */
	CompletableFuture<Boolean> active();
}


