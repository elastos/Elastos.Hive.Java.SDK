package org.elastos.hive.vault.backup;

import org.elastos.hive.connection.ConnectionManager;
import org.elastos.hive.exception.HiveException;
import org.elastos.hive.service.BackupService;

import java.io.IOException;

public class BackupController {
	private BackupAPI backupAPI;

	public BackupController(ConnectionManager connection) {
		backupAPI = connection.createService(BackupAPI.class, true);
	}

	public void startBackup(String token) throws HiveException {
		try {
			backupAPI.saveToNode(new BackupSaveRequestBody(token)).execute().body();
		} catch (IOException e) {
			// TODO:
			e.printStackTrace();
		}
	}

	public void restoreFrom(String token) throws HiveException {
		try {
			backupAPI.restoreFromNode(new BackupRestoreRequestBody(token)).execute().body();
		} catch (IOException e) {
			// TODO:
			e.printStackTrace();
		}
	}

	public BackupService.BackupResult checkResult() throws HiveException {
		try {
			return backupAPI.getState().execute().body().getStatusResult();
		} catch (IOException e) {
			// TODO:
			e.printStackTrace();
		}
		return null;
	}
}
