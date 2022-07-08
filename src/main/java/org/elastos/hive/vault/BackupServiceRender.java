package org.elastos.hive.vault;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;

import org.elastos.hive.ServiceEndpoint;
import org.elastos.hive.exception.HiveException;
import org.elastos.hive.exception.NotImplementedException;
import org.elastos.hive.service.BackupServiceProgress;
import org.elastos.hive.vault.backup.BackupController;
import org.elastos.hive.vault.backup.BackupResult;
import org.elastos.hive.vault.backup.credential.CredentialCode;
import org.elastos.hive.service.BackupContext;
import org.elastos.hive.service.BackupService;

class BackupServiceRender implements BackupService {
	private ServiceEndpoint serviceEndpoint;
	private BackupController controller;
	private CredentialCode credentialCode;

	public BackupServiceRender(ServiceEndpoint serviceEndpoint) {
		this.serviceEndpoint = serviceEndpoint;
		this.controller = new BackupController(serviceEndpoint);
	}

	@Override
	public CompletableFuture<Void> setupContext(BackupContext backupContext) {
		this.credentialCode = new CredentialCode(serviceEndpoint, backupContext);
		return CompletableFuture.runAsync(() -> {
			return;
		});
	}

	private void waitBackupRestoreEnd(BackupServiceProgress callback) {
		try {
			BackupResult result = null;
			do {
				result = controller.checkResult();
				if (callback != null) {
					callback.onProgress(result.getState(), result.getResult(), result.getMessage());
				}
				Thread.sleep(1000);
			} while (result.getResult() == BackupResult.Result.RESULT_PROCESS);
		} catch (HiveException | RuntimeException | InterruptedException e) {
			throw new CompletionException(e);
		}
	}

	@Override
	public CompletableFuture<Void> startBackup(BackupServiceProgress callback) {
		return CompletableFuture.runAsync(() -> {
			try {
				controller.startBackup(credentialCode.getToken());
			} catch (HiveException | RuntimeException e) {
				throw new CompletionException(e);
			}

			this.waitBackupRestoreEnd(callback);
		});
	}

	@Override
	public CompletableFuture<Void> stopBackup() {
		return CompletableFuture.runAsync(() -> {
			throw new NotImplementedException();
		});
	}

	@Override
	public CompletableFuture<Void> restoreFrom(BackupServiceProgress callback) {
		return CompletableFuture.runAsync(() -> {
			try {
				controller.restoreFrom(credentialCode.getToken());
			} catch (HiveException | RuntimeException e) {
				throw new CompletionException(e);
			}

			this.waitBackupRestoreEnd(callback);
		});
	}

	@Override
	public CompletableFuture<Void> stopRestore() {
		return CompletableFuture.runAsync(() -> {
			throw new NotImplementedException();
		});
	}

	@Override
	public CompletableFuture<BackupResult> checkResult() {
		return CompletableFuture.supplyAsync(() -> {
			try {
				return controller.checkResult();
			} catch (HiveException | RuntimeException e) {
				throw new CompletionException(e);
			}
		});
	}
}
