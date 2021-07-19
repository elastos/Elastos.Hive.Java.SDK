package org.elastos.hive.connection.auth;

import org.elastos.hive.connection.NodeRPCException;

/**
 * CodeFetcher is for accessing the code by the network and can be invalidate.
 */
public interface CodeFetcher {
	/**
	 * Fetch the code.
	 *
	 * @return The code.
	 * @throws NodeRPCException The exception shows the error returned by hive node.
	 */
	String fetch() throws NodeRPCException;

	/**
	 * Invalidate the code for getting the code from remote server.
	 */
	void invalidate();
}
