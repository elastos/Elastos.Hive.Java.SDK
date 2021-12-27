package org.elastos.hive.endpoint;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;

import java.util.List;

interface ManagementAPI {
    @GET("/api/v2/management/node/vaults")
    Call<List<VaultDetail>> getVaults();

    @GET("/api/v2/management/node/backups")
    Call<List<BackupDetail>> getBackups();

    @GET("/api/v2/management/node/users")
    Call<List<UserDetail>> getUsers();

    @GET("/api/v2/management/node/payments")
    Call<List<PaymentDetail>> getPayments();

    @DELETE("/api/v2/management/node/vaults")
    Call<Void> deleteVaults(@Body DeleteVaultsParams body);

    @DELETE("/api/v2/management/node/backups")
    Call<Void> deleteBackups(@Body DeleteBackupsParams body);

    @GET("/api/v2/management/vault/apps")
    Call<List<VaultAppDetail>> getVaultApps();

    @DELETE("/api/v2/management/vault/apps")
    Call<Void> deleteVaultApps(@Body DeleteVaultAppsParams body);
}
