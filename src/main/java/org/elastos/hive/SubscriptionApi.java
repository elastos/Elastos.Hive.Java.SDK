package org.elastos.hive;

import org.elastos.hive.network.BaseApi;
import org.elastos.hive.subscribe.CreateServiceResult;

import retrofit2.Call;
import retrofit2.http.POST;

public interface SubscriptionApi extends BaseApi {

	@POST(API_PATH + "/service/vault/create")
	Call<CreateServiceResult> createVault();

	@POST(API_PATH + "/service/vault/freeze")
	Call<Void> freeze();

	@POST(API_PATH + "/service/vault/freeze")
	Call<Void> unfreeze();


}
