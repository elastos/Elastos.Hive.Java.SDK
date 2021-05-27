package org.elastos.hive.vault;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;

import org.elastos.hive.ServiceEndpoint;
import org.elastos.hive.vault.backup.BackupController;
import org.elastos.hive.vault.backup.credential.CredentialCode;
import org.elastos.hive.service.BackupContext;
import org.elastos.hive.service.BackupService;

class BackupServiceRender implements BackupService, ExceptionConvertor {
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
        return null;
    }

    @Override
    public CompletableFuture<Void> startBackup() {
        return CompletableFuture.runAsync(() -> {
            try {
                controller.startBackup(credentialCode.getToken());
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
                controller.restoreFrom(credentialCode.getToken());
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
