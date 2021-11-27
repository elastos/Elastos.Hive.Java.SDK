package org.elastos.hive.demo.sdk;

import android.util.Log;

import org.elastos.hive.BackupSubscription;
import org.elastos.hive.exception.HiveException;
import org.elastos.hive.service.BackupService;

import java.util.concurrent.CompletableFuture;

public class BackupVault {

    private final SdkContext sdkContext;
    private BackupService backupService;
    private BackupSubscription backupSubscription;

    public BackupVault(SdkContext sdkContext) {
        this.sdkContext = sdkContext;
        try {
            this.backupSubscription = sdkContext.newBackupSubscription();
        } catch (HiveException e) {
            Log.e("HomeViewModel", "Failed to initialize the vault subscription object.");
        }
        this.backupService = sdkContext.getBackupService();
    }

    public CompletableFuture<Void> startBackup() {
        return this.backupSubscription.unsubscribe()
                .thenCompose(result->this.backupSubscription.subscribe())
                .thenCompose(result->this.backupService.startBackup());
    }

}
