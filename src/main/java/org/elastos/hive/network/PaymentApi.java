package org.elastos.hive.network;

import org.elastos.hive.network.request.PayOrderRequestBody;
import org.elastos.hive.network.request.PaymentCreateRequestBody;
import org.elastos.hive.network.response.*;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface PaymentApi {
	@GET(BaseApi.API_VERSION + "/payment/vault_package_info")
	Call<PaymentPackageResponseBody> getPackageInfo();

	@GET(BaseApi.API_VERSION + "/payment/vault_pricing_plan")
	Call<PaymentPlanResponseBody> getPricingPlan(@Query("name") String name);

	@GET(BaseApi.API_VERSION + "/payment/vault_backup_plan")
	Call<PaymentPlanResponseBody> getBackupPlan(@Query("name") String name);

	@POST(BaseApi.API_VERSION + "/payment/create_vault_package_order")
	Call<PaymentCreateResponseBody> createOrder(@Body PaymentCreateRequestBody body);

	@POST(BaseApi.API_VERSION + "/payment/pay_vault_package_order")
	Call<HiveResponseBody> payOrder(@Body PayOrderRequestBody body);

	@GET(BaseApi.API_VERSION + "/payment/vault_package_order")
	Call<OrderInfoResponseBody> getOrderInfo(@Query("order_id") String orderId);

	@GET(BaseApi.API_VERSION + "/payment/vault_package_order_list")
	Call<OrderListResponseBody> getOrderList();

	@GET(BaseApi.API_VERSION + "/payment/version")
	Call<PaymentVersionResponseBody> getPaymentVersion();
}
