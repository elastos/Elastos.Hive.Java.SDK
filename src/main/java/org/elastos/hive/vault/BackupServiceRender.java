package org.elastos.hive.vault;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;

import org.elastos.hive.ServiceEndpoint;
import org.elastos.hive.auth.AuthTokenToBackup;
import org.elastos.hive.vault.backup.BackupController;
import org.elastos.hive.service.BackupContext;
import org.elastos.hive.service.BackupService;

class BackupServiceRender implements BackupService, ExceptionConvertor {
    private ServiceEndpoint serviceEndpoint;
    private BackupController controller;
    private AuthTokenToBackup authToken;

    public BackupServiceRender(ServiceEndpoint serviceEndpoint) {
    	this.serviceEndpoint = serviceEndpoint;
    	this.controller = new BackupController(serviceEndpoint);
    }

    @Override
    public CompletableFuture<Void> setupContext(BackupContext backupContext) {
    	this.authToken = new AuthTokenToBackup(serviceEndpoint, backupContext);
        return null;
    }

    @Override
    public CompletableFuture<Void> startBackup() {
        return CompletableFuture.runAsync(() -> {
            try {
                controller.startBackup(authToken.getToken());
            } catch (Exception e) {
                throw new CompletionException(toHiveException(e));
            }
        });
    }

    @Override
    public CompletableFuture<Void> stopBackup() {
        throw new UnsupportedOperationException();
    }

    @Override
    public CompletableFuture<Void> restoreFrom() {
        return CompletableFuture.runAsync(() -> {
            try {
                controller.restoreFrom(authToken.getToken());
            } catch (Exception e) {
                throw new CompletionException(toHiveException(e));
            }
        });
    }

    @Override
    public CompletableFuture<Void> stopRestore() {
        throw new UnsupportedOperationException();
    }

    @Override
    public CompletableFuture<BackupResult> checkResult() {
        return CompletableFuture.supplyAsync(() -> {
            try {
                return controller.checkResult();
            } catch (Exception e) {
                throw new CompletionException(toHiveException(e));
            }
        });
    }
}
