package org.elastos.hive.network;

import okhttp3.ResponseBody;
import org.elastos.hive.connection.HiveResponseBody;
import org.elastos.hive.network.request.*;
import org.elastos.hive.network.response.*;
import retrofit2.Call;
import retrofit2.http.*;

public interface CallAPI {

	// scripting
	String API_SCRIPT_UPLOAD = "/scripting/run_script_upload";

	@POST("/api/v1/scripting/set_script")
	Call<RegisterScriptResponseBody> registerScript(@Body RegisterScriptRequestBody body);

	@POST("/api/v1/scripting/run_script")
	Call<ResponseBody> callScript(@Body CallScriptRequestBody body);

	@GET("/api/v1/scripting/run_script_url/{targetDid}@{appDid}/{scriptName}")
	Call<ResponseBody> callScriptUrl(@Path("targetDid") String targetDid,
									 @Path("appDid") String appDid,
									 @Path("scriptName") String scriptName,
									 @Query("params") String params);

	@POST("/api/v1/scripting/run_script_download/{transaction_id}")
	Call<ResponseBody> callDownload(@Path("transaction_id") String transactionId);

	// backup



	// payment

	@GET("/api/v1/payment/vault_package_info")
	Call<PaymentPackageResponseBody> getPackageInfo();

	@GET("/api/v1/payment/vault_pricing_plan")
	Call<PaymentPlanResponseBody> getPricingPlan(@Query("name") String name);

	@GET("/api/v1/payment/vault_backup_plan")
	Call<PaymentPlanResponseBody> getBackupPlan(@Query("name") String name);

	@POST("/api/v1/payment/create_vault_package_order")
	Call<PaymentCreateResponseBody> createOrder(@Body PaymentCreateRequestBody body);

	@POST("/api/v1/payment/pay_vault_package_order")
	Call<HiveResponseBody> payOrder(@Body PayOrderRequestBody body);

	@GET("/api/v1/payment/vault_package_order")
	Call<OrderInfoResponseBody> getOrderInfo(@Query("order_id") String orderId);

	@GET("/api/v1/payment/vault_package_order_list")
	Call<OrderListResponseBody> getOrderList();

	@GET("/api/v1/payment/version")
	Call<PaymentVersionResponseBody> getPaymentVersion();

	// vault

	@POST("/api/v1/service/vault/create")
	Call<VaultCreateResponseBody> createVault();

	@POST("/api/v1/service/vault/freeze")
	Call<HiveResponseBody> freeze();

	@POST("/api/v1/service/vault/unfreeze")
	Call<HiveResponseBody> unfreeze();

	@POST("/api/v1/service/vault/remove")
	Call<HiveResponseBody> removeVault();

	@GET("/api/v1/service/vault")
	Call<VaultInfoResponseBody> getVaultInfo();

	@POST("/api/v1/service/vault_backup/create")
	Call<VaultCreateResponseBody> createBackupVault();

	@GET("/api/v1/service/vault_backup")
	Call<VaultInfoResponseBody> getBackupVaultInfo();

	// pubsub

	@POST("/api/v1/pubsub/publish")
	Call<HiveResponseBody> publish(@Body PubsubRequestBody body);

	@POST("/api/v1/pubsub/remove")
	Call<HiveResponseBody> remove(@Body PubsubRequestBody body);

	@GET("/api/v1/pubsub/pub/channels")
	Call<PubsubChannelsResponseBody> getPublishedChannels();

	@GET("/api/v1/pubsub/sub/channels")
	Call<PubsubChannelsResponseBody> getSubscribedChannels();

	@POST("/api/v1/pubsub/subscribe")
	Call<HiveResponseBody> subscribe(@Body PubsubSubscribeRequestBody body);

	@POST("/api/v1/pubsub/unsubscribe")
	Call<HiveResponseBody> unsubscribe(@Body PubsubSubscribeRequestBody body);

	@POST("/api/v1/pubsub/push")
	Call<HiveResponseBody> push(@Body PushMessageRequestBody body);

	@POST("/api/v1/pubsub/pop")
	Call<PopMessageResponseBody> pop(@Body PopMessageRequestBody body);

}
