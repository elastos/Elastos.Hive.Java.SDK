package org.elastos.hive.vault;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;

import org.elastos.hive.ServiceEndpoint;
import org.elastos.hive.auth.BackupLocalResolver;
import org.elastos.hive.auth.BackupRemoteResolver;
import org.elastos.hive.auth.TokenResolver;
import org.elastos.hive.network.request.BackupRestoreRequestBody;
import org.elastos.hive.network.request.BackupSaveRequestBody;
import org.elastos.hive.network.response.HiveResponseBody;
import org.elastos.hive.service.BackupContext;
import org.elastos.hive.service.BackupService;

class BackupServiceRender implements BackupService, ExceptionConvertor {
    private TokenResolver tokenResolver;
    private ServiceEndpoint serviceEndpoint;

    public BackupServiceRender(ServiceEndpoint serviceEndpoint) {
    	this.serviceEndpoint = serviceEndpoint;
    }

    @Override
    public CompletableFuture<Void> setupContext(BackupContext backupContext) {
        this.tokenResolver = new BackupLocalResolver(serviceEndpoint);
        this.tokenResolver.setNextResolver(new BackupRemoteResolver(
        		serviceEndpoint,
                backupContext,
                backupContext.getParameter("targetServiceDid"),
                backupContext.getParameter("targetAddress")));
        return null;
    }

    @Override
    public CompletableFuture<Void> startBackup() {
        return CompletableFuture.runAsync(() -> {
            try {
                HiveResponseBody.validateBody(
                        serviceEndpoint.getConnectionManager().getCallAPI()
                                .saveToNode(new BackupSaveRequestBody(tokenResolver.getToken().getAccessToken()))
                                .execute()
                                .body());
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
                HiveResponseBody.validateBody(
                        serviceEndpoint.getConnectionManager().getCallAPI()
                                .restoreFromNode(new BackupRestoreRequestBody(
                                        tokenResolver.getToken().getAccessToken()))
                                .execute()
                                .body());
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
                return HiveResponseBody.validateBody(
                        serviceEndpoint.getConnectionManager().getCallAPI()
                                .getState()
                                .execute()
                                .body()).getStatusResult();
            } catch (Exception e) {
                throw new CompletionException(toHiveException(e));
            }
        });
    }
}
