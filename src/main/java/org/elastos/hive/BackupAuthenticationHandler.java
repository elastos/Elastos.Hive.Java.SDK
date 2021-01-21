package org.elastos.hive;

import java.util.concurrent.CompletableFuture;

public interface BackupAuthenticationHandler {

	/**
	 * This is the interface to make authorization from users, and it would be
	 * provided by application.
	 * @param serviceDid source service did
	 * @return
	 */
	CompletableFuture<String> getAuthorization(String serviceDid);

	/**
	 * backup target host
	 * @return
	 */
	String getTargetHost();

	/**
	 * backup target did
	 * @return
	 */
	String getTargetDid();
}
