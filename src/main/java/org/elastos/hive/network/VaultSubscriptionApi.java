package org.elastos.hive.network;

import retrofit2.Call;
import retrofit2.http.POST;

public interface VaultSubscriptionApi extends BaseApi{
	@POST(API_PATH + "/service/vault/create")
	Call<Void> createVault();

	@POST(API_PATH + "/service/vault/freeze")
	Call<Void> freeze();

	@POST(API_PATH + "/service/vault/freeze")
	Call<Void> unfreeze();
}
