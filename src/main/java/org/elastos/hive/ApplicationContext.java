package org.elastos.hive;

import org.elastos.did.DIDDocument;

import java.util.concurrent.CompletableFuture;

public interface ApplicationContext {
	/**
	 * token cache path
	 *
	 * @return
	 */
	String getLocalDataDir();

	/**
	 * app instance DIDDocument
	 *
	 * @return
	 */
	DIDDocument getAppInstanceDocument();

	/**
	 * This is the interface to make authorization from users, and it would be
	 * provided by application.
	 *
	 * @return
	 */
	CompletableFuture<String> getAuthorization(String jwtToken);

}