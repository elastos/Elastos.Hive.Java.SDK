package org.elastos.hive.service;

import java.util.concurrent.CompletableFuture;

public interface BackupService {
	enum BackupResult {

	}

	public CompletableFuture<Void> setupContext(BackupContext context);

	public CompletableFuture<Void> startBackup();

	public CompletableFuture<Void> stopBackup();

	public CompletableFuture<Void> restoreFrom();

	public CompletableFuture<Void> stopRestore();

	public CompletableFuture<BackupResult> checkResult();
}
