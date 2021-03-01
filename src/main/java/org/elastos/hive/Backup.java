package org.elastos.hive;

import org.elastos.hive.backup.State;

import java.util.concurrent.CompletableFuture;

public interface Backup {

	/**
	 * Get backup/restore state
	 *
	 * @return  stop, backup, restore
	 */
	CompletableFuture<State> getState();

	/**
	 * Backup hive vault to other hive node
	 *
	 * @param handler BackupAuthenticationHandler
	 * @return true(success), false(failed)
	 */
	CompletableFuture<Boolean> store(BackupAuthenticationHandler handler);

	/**
	 * Restore hive vault from other hive node
	 *
	 * @param handler BackupAuthenticationHandler
	 * @return  true(success), false(failed)
	 */
	CompletableFuture<Boolean> restore(BackupAuthenticationHandler handler);

	/**
	 * Active hive backup data to vault
	 *
	 * @return true(success), false(failed)
	 */
	CompletableFuture<Boolean> activate();

}
