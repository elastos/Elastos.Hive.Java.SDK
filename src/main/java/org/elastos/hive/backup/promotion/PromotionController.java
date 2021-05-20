package org.elastos.hive.backup.promotion;

import org.elastos.hive.ServiceEndpoint;
import org.elastos.hive.connection.EmptyRequestBody;
import org.elastos.hive.exception.HiveException;

public class PromotionController {
	private PromotionAPI api;

	public PromotionController(ServiceEndpoint serviceEndpoint) {
		api = serviceEndpoint.getConnectionManager().createService(PromotionAPI.class, true);
	}

	public void promote() throws HiveException {
		try {
			api.activeToVault(new EmptyRequestBody()).execute().body();
		} catch (Exception e) {
			// TOOD:
			throw new HiveException(e);
		}
	}
}
