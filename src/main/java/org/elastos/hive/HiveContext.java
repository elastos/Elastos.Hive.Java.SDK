package org.elastos.hive;

import org.elastos.did.DIDDocument;

public interface HiveContext {
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
	String getAuthorization(String jwtToken);
}