package org.elastos.hive.service;

import java.util.concurrent.CompletableFuture;

public interface BackupService {
	enum BackupResult {

	}

	CompletableFuture<Void> setupContext(BackupContext context);

	CompletableFuture<Void> startBackup();

	CompletableFuture<Void> stopBackup();

	CompletableFuture<Void> restoreFrom();

	CompletableFuture<Void> stopRestore();

	CompletableFuture<BackupResult> checkResult();
}
