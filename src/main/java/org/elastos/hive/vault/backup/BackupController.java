package org.elastos.hive.vault.backup;

import org.elastos.hive.connection.NodeRPCConnection;
import org.elastos.hive.connection.NodeRPCException;
import org.elastos.hive.exception.HiveException;
import org.elastos.hive.exception.NetworkException;
import org.elastos.hive.exception.UnauthorizedException;
import org.elastos.hive.exception.ServerUnkownException;
import org.elastos.hive.service.BackupService;

import java.io.IOException;

public class BackupController {
	private BackupAPI backupAPI;

	public BackupController(NodeRPCConnection connection) {
		backupAPI = connection.createService(BackupAPI.class, true);
	}

	public void startBackup(String credential) throws HiveException {
		try {
			backupAPI.saveToNode(new RequestParams(credential)).execute().body();
		} catch (NodeRPCException e) {
			switch (e.getCode()) {
			case NodeRPCException.UNAUTHORIZED:
				throw new UnauthorizedException(e);

			// TODO: check more exception here.
			default:
				throw new ServerUnkownException(e);
			}
		} catch (IOException e) {
			throw new NetworkException(e);
		}
	}

	public void restoreFrom(String credential) throws HiveException {
		try {
			backupAPI.restoreFromNode(new RequestParams(credential)).execute().body();
		} catch (NodeRPCException e) {
			switch (e.getCode()) {
			case NodeRPCException.UNAUTHORIZED:
				throw new UnauthorizedException(e);

			// TODO: check more exception here.
			default:
				throw new ServerUnkownException(e);
			}
		} catch (IOException e) {
			throw new NetworkException(e);
		}
	}

	public BackupService.BackupResult checkResult() throws HiveException {
		try {
			return backupAPI.getState().execute().body().getStatusResult();
		} catch (NodeRPCException e) {
			switch (e.getCode()) {
			case NodeRPCException.UNAUTHORIZED:
				throw new UnauthorizedException(e);

			// TODO: check more exception here.
			default:
				throw new ServerUnkownException(e);
			}
		} catch (IOException e) {
			throw new NetworkException(e);
		}
	}
}
