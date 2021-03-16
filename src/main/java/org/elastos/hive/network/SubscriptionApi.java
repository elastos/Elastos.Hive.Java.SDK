package org.elastos.hive.network;

import org.elastos.hive.subscribe.CreateServiceResult;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.POST;

public interface SubscriptionApi {

	@POST(BaseApi.API_VERSION + "/service/vault/create")
	Call<CreateServiceResult> createVault();

	@POST(BaseApi.API_VERSION + "/service/vault/freeze")
	Call<ResponseBody> freeze();

	@POST(BaseApi.API_VERSION + "/service/vault/unfreeze")
	Call<ResponseBody> unfreeze();

	@POST(BaseApi.API_VERSION + "/service/vault/remove")
	Call<ResponseBody> removeVault();
}
