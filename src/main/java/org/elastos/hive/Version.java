package org.elastos.hive;

import java.util.concurrent.CompletableFuture;

/**
 * Version format: Major.Minor.Hotfix
 */
public interface Version {

	/**
	 * Get Node server version
	 *
	 * @return
	 */
	 CompletableFuture<String> getFullName();

	/**
	 * Get the last commit ID on the hive node git repository
	 *
	 * @return
	 */
	CompletableFuture<String> getLastCommitId();

	/**
	 * Major number
	 * @return
	 */
	CompletableFuture<Integer> getMajorNumber();

	/**
	 * Minor number
	 * @return
	 */
	CompletableFuture<Integer> getMinorNumber();

	/**
	 * fix number
	 * @return
	 */
	CompletableFuture<Integer> getFixNumber();

	/**
	 * full number
	 * @return
	 */
	CompletableFuture<Integer> getFullNumber();

}
