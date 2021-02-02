package org.elastos.hive.network;

import org.elastos.hive.service.CreateServiceResult;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.POST;

public interface ServiceManagerApi {

	@POST(Constance.API_PATH + "/service/vault/create")
	Call<CreateServiceResult> createVault();

	@POST(Constance.API_PATH + "/service/vault/remove")
	Call<ResponseBody> removeVault();

	@POST(Constance.API_PATH + "/service/vault/freeze")
	Call<ResponseBody> freezeVault();

	@POST(Constance.API_PATH + "/service/vault/unfreeze")
	Call<ResponseBody> unfreezeVault();

	@GET(Constance.API_PATH + "/service/vault")
	Call<ResponseBody> getVaultServiceInfo();

	@POST(Constance.API_PATH + "/service/vault_backup/create")
	Call<CreateServiceResult> createBackupVault();

	@GET(Constance.API_PATH + "/service/vault_backup")
	Call<ResponseBody> getBackupVaultInfo();
}
