package org.elastos.hive.network;

import org.elastos.hive.network.BaseApi;
import org.elastos.hive.subscribe.CreateServiceResult;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.POST;

public interface SubscriptionApi extends BaseApi {

	@POST(API_VERSION + "/service/vault/create")
	Call<CreateServiceResult> createVault();

	@POST(API_VERSION + "/service/vault/freeze")
	Call<ResponseBody> freeze();

	@POST(API_VERSION + "/service/vault/freeze")
	Call<ResponseBody> unfreeze();

	@POST(API_VERSION + "/service/vault/remove")
	Call<ResponseBody> removeVault();
}
