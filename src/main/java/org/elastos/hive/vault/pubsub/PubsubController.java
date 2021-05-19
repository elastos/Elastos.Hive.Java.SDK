package org.elastos.hive.vault.pubsub;

import org.elastos.hive.ServiceEndpoint;

public class PubsubController {
	private PubsubAPI pubsubAPI;

	public PubsubController(ServiceEndpoint serviceEndpoint) {
		pubsubAPI = serviceEndpoint.getConnectionManager().createService(PubsubAPI.class, true);
	}
}
