package org.elastos.hive.backup.promotion;

import java.io.IOException;

import org.elastos.hive.ServiceEndpoint;
import org.elastos.hive.connection.EmptyRequestBody;
import org.elastos.hive.connection.HiveResponseBody;

public class PromotionController {
	private PromotionAPI api;

	public PromotionController(ServiceEndpoint serviceEndpoint) {
		api = serviceEndpoint.getConnectionManager().createService(PromotionAPI.class, true);
	}

	public void promote() throws IOException {
		HiveResponseBody.validateBody(api.activeToVault(new EmptyRequestBody()).execute().body());
	}
}
