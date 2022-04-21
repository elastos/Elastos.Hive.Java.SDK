package org.elastos.hive.endpoint;

import java.io.IOException;

import org.elastos.hive.connection.NodeRPCConnection;
import org.elastos.hive.exception.HiveException;
import org.elastos.hive.exception.NetworkException;

/**
 * The AboutController is for getting the basic information of the hive node.
 */
public class AboutController {
	private AboutAPI aboutAPI;
	private AboutAPI aboutAPIAuth;

	/**
	 * Create the about controller by the RPC connection.
	 *
	 * @param connection The connection instance.
	 */
	public AboutController(NodeRPCConnection connection) {
		aboutAPI = connection.createService(AboutAPI.class, false);
		aboutAPIAuth = connection.createService(AboutAPI.class, true);
	}

	/**
	 * Get the version of the hive node.
	 *
	 * @return the version
	 * @throws HiveException The exception shows the error from the request.
	 */
	public NodeVersion getNodeVersion() throws HiveException {
		try {
			return aboutAPI.version().execute().body();
		} catch (IOException e) {
			throw new NetworkException(e);
		}
	}

	/**
	 * Get the commit id of the github of the hive node.
	 *
	 * @return The commit id.
	 * @throws HiveException The exception shows the error from the request.
	 */
	public String getCommitId() throws HiveException {
		try {
			return aboutAPI.commitId().execute().body().getCommitId();
		} catch (IOException e) {
			throw new NetworkException(e);
		}
	}

	/**
	 * Get the information of the hive node.
	 *
	 * @return The information of the hive node.
	 * @throws HiveException The exception shows the error from the request.
	 */
	public NodeInfo getNodeInfo() throws HiveException {
		try {
			return aboutAPIAuth.info().execute().body();
		} catch (IOException e) {
			throw new NetworkException(e);
		}
	}
}
