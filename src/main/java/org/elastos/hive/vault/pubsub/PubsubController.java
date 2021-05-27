package org.elastos.hive.vault.pubsub;

import org.elastos.hive.ServiceEndpoint;

public class PubsubController {
	@SuppressWarnings("unused")
	private PubsubAPI pubsubAPI;

	public PubsubController(ServiceEndpoint serviceEndpoint) {
		pubsubAPI = serviceEndpoint.getConnectionManager().createService(PubsubAPI.class, true);
	}
}
