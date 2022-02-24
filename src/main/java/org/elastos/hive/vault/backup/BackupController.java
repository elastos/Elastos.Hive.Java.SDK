package org.elastos.hive.vault.backup;

import org.elastos.hive.connection.NodeRPCConnection;
import org.elastos.hive.connection.NodeRPCException;
import org.elastos.hive.exception.*;

import java.io.IOException;
import java.security.InvalidParameterException;

/**
 * The backup controller is the wrapper class to access the backup module of the hive node.
 */
public class BackupController {
	private BackupAPI backupAPI;

	/**
	 * Create with the RPC connection.
	 *
	 * @param connection The RPC connection.
	 */
	public BackupController(NodeRPCConnection connection) {
		backupAPI = connection.createService(BackupAPI.class, true);
	}

	/**
	 * Start the backup process which backups the data of the vault to other place.
	 *
	 * @param credential The credential for the hive node to access the backup service.
	 * @throws HiveException The error comes from the hive node.
	 */
	public void startBackup(String credential) throws HiveException {
		try {
			backupAPI.saveToNode(new RequestParams(credential)).execute().body();
		} catch (NodeRPCException e) {
			switch (e.getCode()) {
				case NodeRPCException.UNAUTHORIZED:
					throw new UnauthorizedException(e);
				case NodeRPCException.FORBIDDEN:
					throw new VaultForbiddenException(e);
				case NodeRPCException.BAD_REQUEST:
					if (e.getInternalCode() == NodeRPCException.IC_BACKUP_IS_IN_PROCESSING)
						throw new BackupIsInProcessingException(e);
					else
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

	/**
	 * Restore the data of the vault from other place.
	 *
	 * @param credential The credential for the hive node to access the backup service.
	 * @throws HiveException The error comes from the hive node.
	 */
	public void restoreFrom(String credential) throws HiveException {
		try {
			backupAPI.restoreFromNode(new RequestParams(credential)).execute().body();
		} catch (NodeRPCException e) {
			switch (e.getCode()) {
				case NodeRPCException.UNAUTHORIZED:
					throw new UnauthorizedException(e);
				case NodeRPCException.FORBIDDEN:
					throw new VaultForbiddenException(e);
				case NodeRPCException.BAD_REQUEST:
					if (e.getInternalCode() == NodeRPCException.IC_BACKUP_IS_IN_PROCESSING)
						throw new BackupIsInProcessingException(e);
					else
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

	/**
	 * Check the result of the backup process.
	 *
	 * @return The result of the backup process.
	 * @throws HiveException The error comes from the hive node.
	 */
	public BackupResult checkResult() throws HiveException {
		try {
			return backupAPI.getState().execute().body();
		} catch (NodeRPCException e) {
			switch (e.getCode()) {
				case NodeRPCException.UNAUTHORIZED:
					throw new UnauthorizedException(e);
				case NodeRPCException.FORBIDDEN:
					throw new VaultForbiddenException(e);
				case NodeRPCException.NOT_FOUND:
					throw new NotFoundException(e);
				default:
					throw new ServerUnknownException(e);
			}
		} catch (IOException e) {
			throw new NetworkException(e);
		}
	}
}
