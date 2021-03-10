package org.elastos.hive;

import retrofit2.Call;
import retrofit2.http.POST;

interface VaultSubscriptionApi extends BaseApi{

	@POST(API_VERSION + "/service/vault/create")
	Call<Void> createVault();

	@POST(API_VERSION + "/service/vault/freeze")
	Call<Void> freeze();

	@POST(API_VERSION + "/service/vault/freeze")
	Call<Void> unfreeze();


}
