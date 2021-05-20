package org.elastos.hive.vault.backup;

import org.elastos.hive.ServiceEndpoint;
import org.elastos.hive.connection.HiveResponseBody;
import org.elastos.hive.service.BackupService;

import java.io.IOException;

public class BackupController {
	private BackupAPI backupAPI;

	public BackupController(ServiceEndpoint serviceEndpoint) {
		backupAPI = serviceEndpoint.getConnectionManager().createService(BackupAPI.class, true);
	}

	public void startBackup(String token) throws IOException {
		HiveResponseBody.validateBody(
				backupAPI.saveToNode(new BackupSaveRequestBody(token)).execute().body());
	}

	public void restoreFrom(String token) throws IOException {
		HiveResponseBody.validateBody(backupAPI.restoreFromNode(new BackupRestoreRequestBody(token)).execute().body());
	}

	public BackupService.BackupResult checkResult() throws IOException {
		return HiveResponseBody.validateBody(backupAPI.getState().execute().body()).getStatusResult();
	}
}
