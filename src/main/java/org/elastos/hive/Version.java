package org.elastos.hive;

import java.util.concurrent.CompletableFuture;

public interface Version {

	/**
	 * Get Node server version
	 *
	 * @return
	 */
	 CompletableFuture<String> getVersionName();

	/**
	 * Get the last commit ID on the hive node git repository
	 *
	 * @return
	 */
	CompletableFuture<String> getLastCommitId();
}
