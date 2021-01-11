package org.elastos.hive.network;

import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface ServiceApi {

	@POST(Constance.API_PATH + "/service/vault/create")
	Call<ResponseBody> createVault(@Body RequestBody body);

	@POST(Constance.API_PATH + "/service/vault/remove")
	Call<ResponseBody> removeVault(@Body RequestBody body);

	@POST(Constance.API_PATH + "/service/vault/freeze")
	Call<ResponseBody> freezeVault(@Body RequestBody body);

	@POST(Constance.API_PATH + "/service/vault/freeze")
	Call<ResponseBody> unfreezeVault(@Body RequestBody body);

	@POST(Constance.API_PATH + "/service/vault")
	Call<ResponseBody> getVaultServiceInfo(@Body RequestBody body);

	@POST(Constance.API_PATH + "/service/vault_backup/create")
	Call<ResponseBody> createBackupVault(@Body RequestBody body);

	@POST(Constance.API_PATH + "/service/vault_backup")
	Call<ResponseBody> getBackupVaultInfo(@Body RequestBody body);
}
