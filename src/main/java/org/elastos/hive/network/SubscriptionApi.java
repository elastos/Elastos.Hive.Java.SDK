package org.elastos.hive.network;

import org.elastos.hive.network.response.HiveResponseBody;
import org.elastos.hive.network.response.VaultCreateResponseBody;
import org.elastos.hive.network.response.VaultInfoResponseBody;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.POST;

public interface SubscriptionApi {

	@POST(BaseApi.API_VERSION + "/service/vault/create")
	Call<VaultCreateResponseBody> createVault();

	@POST(BaseApi.API_VERSION + "/service/vault/freeze")
	Call<HiveResponseBody> freeze();

	@POST(BaseApi.API_VERSION + "/service/vault/unfreeze")
	Call<HiveResponseBody> unfreeze();

	@POST(BaseApi.API_VERSION + "/service/vault/remove")
	Call<HiveResponseBody> removeVault();

	@GET(BaseApi.API_VERSION + "/service/vault")
	Call<VaultInfoResponseBody> getVaultInfo();

	@POST(BaseApi.API_VERSION + "/service/vault_backup/create")
	Call<VaultCreateResponseBody> createBackupVault();

	@GET(BaseApi.API_VERSION + "/service/vault_backup")
	Call<VaultInfoResponseBody> getBackupVaultInfo();

}
