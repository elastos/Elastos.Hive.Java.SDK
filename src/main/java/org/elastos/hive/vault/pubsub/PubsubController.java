package org.elastos.hive.vault.pubsub;

import org.elastos.hive.connection.ConnectionManager;

public class PubsubController {
	@SuppressWarnings("unused")
	private PubsubAPI pubsubAPI;

	public PubsubController(ConnectionManager connection) {
		pubsubAPI = connection.createService(PubsubAPI.class, true);
	}
}
