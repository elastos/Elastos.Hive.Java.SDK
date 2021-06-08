package org.elastos.hive.backup.promotion;

import java.io.IOException;

import org.elastos.hive.connection.ConnectionManager;
import org.elastos.hive.connection.EmptyRequestBody;
import org.elastos.hive.exception.HiveException;
import org.elastos.hive.exception.NetworkException;
import org.elastos.hive.exception.NodeRPCException;
import org.elastos.hive.exception.UnknownServerException;

public class PromotionController {
	private PromotionAPI api;

	public PromotionController(ConnectionManager connection) {
		api = connection.createService(PromotionAPI.class, true);
	}

	public void promote() throws HiveException {
		try {
			api.activeToVault(new EmptyRequestBody()).execute().body();
		} catch (NodeRPCException e) {
			throw new UnknownServerException(e);
		} catch (IOException e) {
			throw new NetworkException(e.getMessage());
		}
	}
}
