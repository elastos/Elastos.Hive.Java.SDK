package org.elastos.hive.backup.promotion;

import org.elastos.hive.connection.NodeRPCConnection;
import org.elastos.hive.connection.NodeRPCException;
import org.elastos.hive.exception.*;

import java.io.IOException;
import java.security.InvalidParameterException;

public class PromotionController {
	private PromotionAPI promotionAPI;

	public PromotionController(NodeRPCConnection connection) {
		this.promotionAPI = connection.createService(PromotionAPI.class, true);
	}

	public void promote() throws HiveException {
		try {
			promotionAPI.promoteToVault().execute();
		} catch (NodeRPCException e) {
			switch (e.getCode()) {
				case NodeRPCException.UNAUTHORIZED:
					throw new UnauthorizedException(e);
				case NodeRPCException.ALREADY_EXISTS:
					throw new VaultAlreadyExistsException(e);
				case NodeRPCException.NOT_FOUND:
					throw new NotFoundException(e);
				case NodeRPCException.BAD_REQUEST:
					throw new InvalidParameterException(e.getMessage());
				case NodeRPCException.INSUFFICIENT_STORAGE:
					throw new InsufficientStorageException(e);
				default:
					throw new ServerUnknownException(e);
			}
		} catch (IOException e) {
			throw new NetworkException(e);
		}
	}
}
