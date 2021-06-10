package org.elastos.hive.subscription.payment;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Query;

interface PaymentAPI {
	@PUT("/api/v2/payment/order")
	Call<Order> createOrder(@Body CreateOrderParams params);

	@POST("/api/v2/payment/order/{order_id}")
	Call<Receipt> payOrder(@Body PayOrderParams params);

	@GET("/api/v2/payment/order")
	Call<OrderCollection> getOrders(@Query("subscription") String subscription);

	@GET("/api/v2/payment/receipt")
	Call<ReceiptCollection> getReceipts(@Query("subscription") String subscription);

	@GET("/api/v2/payment/version")
	Call<String> getVersion();
}
