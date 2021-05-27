package org.elastos.hive.subscription;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.POST;

interface SubscriptionV1API {
    @POST("/api/v1/service/vault_backup/create")
    Call<BackupSubscribeResponse> createBackupVault();

    @GET("/api/v1/service/vault_backup")
    Call<BackupInfoResponse> getBackupVaultInfo();
}
