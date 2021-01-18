package org.elastos.hive;

import java.util.concurrent.CompletableFuture;

public interface BackupAuthenticationHandler {

	/**
	 * This is the interface to make authorization from users, and it would be
	 * provided by application.
	 * @param serviceDid source service did
	 * @return
	 */
	CompletableFuture<String> authorization(String serviceDid);
}
