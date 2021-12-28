package org.elastos.hive.endpoint;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.HTTP;

interface ManagementAPI {
    @GET("/api/v2/management/node/vaults")
    Call<VaultsInfo> getVaults();

    @GET("/api/v2/management/node/backups")
    Call<BackupsInfo> getBackups();

    @GET("/api/v2/management/node/users")
    Call<UsersInfo> getUsers();

    @GET("/api/v2/management/node/payments")
    Call<PaymentsInfo> getPayments();

    @HTTP(method = "DELETE", path = "/api/v2/management/node/vaults", hasBody = true)
    Call<Void> deleteVaults(@Body DeleteVaultsParams body);

    @HTTP(method = "DELETE", path = "/api/v2/management/node/backups", hasBody = true)
    Call<Void> deleteBackups(@Body DeleteBackupsParams body);

    @GET("/api/v2/management/vault/apps")
    Call<VaultAppsInfo> getVaultApps();

    @HTTP(method = "DELETE", path = "/api/v2/management/vault/apps", hasBody = true)
    Call<Void> deleteVaultApps(@Body DeleteVaultAppsParams body);
}
