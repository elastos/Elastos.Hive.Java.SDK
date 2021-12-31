package org.elastos.hive.provider;

import retrofit2.Call;
import retrofit2.http.GET;

interface RunStatsAPI {
    @GET("/api/v2/provider/vaults")
    Call<VaultStats> getVaults();

    @GET("/api/v2/provider/backups")
    Call<BackupStats> getBackups();

    @GET("/api/v2/provider/filled_orders")
    Call<FilledOrderStats> getFilledOrders();
}
