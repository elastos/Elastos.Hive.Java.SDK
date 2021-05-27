package org.elastos.hive.subscription;

import org.elastos.hive.connection.HiveResponseBody;
import retrofit2.Call;
import retrofit2.http.*;

interface SubscriptionAPI {
	@GET("/api/v2/subscription/vault")
	Call<VaultInfoResponse> getVaultInfo();

	@PUT("/api/v2/subscription/vault")
	Call<VaultSubscribeResponse> vaultSubscribe(@Query("credential") String credential);

	@POST("/api/v2/subscription/vault?op=activation")
	Call<Void> vaultActivate();

	@POST("/api/v2/subscription/vault?op=deactivation")
	Call<Void> vaultDeactivate();

	@DELETE("/api/v2/subscription/vault")
	Call<HiveResponseBody> vaultUnsubscribe();
}
