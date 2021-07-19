package org.elastos.hive.connection.auth;

import org.elastos.hive.DataStorage;
import org.elastos.hive.ServiceEndpoint;

/**
 * The bridge handler is for the {@link AccessToken#AccessToken(ServiceEndpoint, DataStorage, BridgeHandler)} ()}
 */
public interface BridgeHandler {
	/**
	 * Flush the value of the access token.
	 *
	 * @param value The value of the access token.
	 */
	void flush(String value);

	/**
	 * The target is what the access token for.
	 *
	 * @return The target object, such as service end point, etc.
	 */
	Object target();
}
