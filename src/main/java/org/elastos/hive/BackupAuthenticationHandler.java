package org.elastos.hive;

import java.util.concurrent.CompletableFuture;

public interface BackupAuthenticationHandler {

	/**
	 * This is the interface to make authorization from users, and it would be
	 * provided by application.
	 * @param sourceDid source service did
	 * @param targetDid backup service did
	 * @param targetHost backup service did
	 * @return
	 */
	CompletableFuture<String> getAuthorization(String sourceDid, String targetDid, String targetHost);

}
