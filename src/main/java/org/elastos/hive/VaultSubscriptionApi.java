package org.elastos.hive;

import org.elastos.hive.subscribe.CreateServiceResult;

import retrofit2.Call;
import retrofit2.http.POST;

public interface VaultSubscriptionApi extends BaseApi{

	@POST(API_VERSION + "/service/vault/create")
	Call<CreateServiceResult> createVault();

	@POST(API_VERSION + "/service/vault/freeze")
	Call<Void> freeze();

	@POST(API_VERSION + "/service/vault/freeze")
	Call<Void> unfreeze();


}
