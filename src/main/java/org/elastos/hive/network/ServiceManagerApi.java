package org.elastos.hive.network;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.POST;

public interface ServiceManagerApi {

	@POST(Constance.API_PATH + "/service/vault/create")
	Call<ResponseBody> createVault();

	@POST(Constance.API_PATH + "/service/vault/remove")
	Call<ResponseBody> removeVault();

	@POST(Constance.API_PATH + "/service/vault/freeze")
	Call<ResponseBody> freezeVault();

	@POST(Constance.API_PATH + "/service/vault/freeze")
	Call<ResponseBody> unfreezeVault();

	@GET(Constance.API_PATH + "/service/vault")
	Call<ResponseBody> getVaultServiceInfo();

	@POST(Constance.API_PATH + "/service/vault_backup/create")
	Call<ResponseBody> createBackupVault();

	@GET(Constance.API_PATH + "/service/vault_backup")
	Call<ResponseBody> getBackupVaultInfo();
}
