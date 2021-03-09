package org.elastos.hive.vault;

import java.util.concurrent.CompletableFuture;

import org.elastos.hive.Vault;
import org.elastos.hive.service.BackupContext;
import org.elastos.hive.service.BackupService;

class BackupServiceRender implements BackupService {
	public BackupServiceRender(Vault vault) {
		// TODO Auto-generated constructor stub
	}

	@Override
	public CompletableFuture<Void> setupContext(BackupContext context) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public CompletableFuture<Void> startBackup() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public CompletableFuture<Void> stopBackup() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public CompletableFuture<Void> restoreFrom() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public CompletableFuture<Void> stopRestore() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public CompletableFuture<BackupResult> checkResult() {
		// TODO Auto-generated method stub
		return null;
	}
}
