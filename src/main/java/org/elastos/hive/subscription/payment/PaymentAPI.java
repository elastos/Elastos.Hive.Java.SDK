package org.elastos.hive.subscription.payment;

import org.elastos.hive.connection.HiveResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

interface PaymentAPI {
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
}
