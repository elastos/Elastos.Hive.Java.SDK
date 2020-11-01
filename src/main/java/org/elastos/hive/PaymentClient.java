package org.elastos.hive;

import org.elastos.hive.connection.ConnectionManager;
import org.elastos.hive.exception.HiveException;
import org.elastos.hive.payment.Order;
import org.elastos.hive.payment.PricingPlan;
import org.elastos.hive.utils.JsonUtil;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Response;

public class PaymentClient implements Payment {

	private AuthHelper authHelper;
	private ConnectionManager connectionManager;

	public PaymentClient(AuthHelper authHelper) {
		this.authHelper = authHelper;
		this.connectionManager = authHelper.getConnectionManager();
	}


	@Override
	public CompletableFuture<List<PricingPlan>> getAllPricingPlans() {
		return authHelper.checkValid()
				.thenCompose(result -> getAllPricingPlansImp());
	}

	@Override
	public CompletableFuture<PricingPlan> getPricingPlan(String planName) {
		return null;
	}

	@Override
	public CompletableFuture<PricingPlan> getUsingPricePlan() {
		return null;
	}

	private CompletableFuture<List<PricingPlan>> getAllPricingPlansImp() {
		return CompletableFuture.supplyAsync(() -> {
			try {
				Response<List<PricingPlan>> response = this.connectionManager.getVaultApi()
						.getPackageInfo()
						.execute();
				authHelper.checkResponseCode(response);
				return response.body();
			} catch (Exception e) {
				HiveException exception = new HiveException(e.getLocalizedMessage());
				throw new CompletionException(exception);
			}
		});
	}

	@Override
	public CompletableFuture<Boolean> useTrial() {
		return authHelper.checkValid()
				.thenCompose(result -> useTrialImp());
	}

	private CompletableFuture<Boolean> useTrialImp() {
		return CompletableFuture.supplyAsync(() -> {
			try {
				Response<ResponseBody> response = this.connectionManager.getVaultApi()
						.freeTrial()
						.execute();
				authHelper.checkResponseCode(response);
				return true;
			} catch (Exception e) {
				HiveException exception = new HiveException(e.getLocalizedMessage());
				throw new CompletionException(exception);
			}
		});
	}

	@Override
	public CompletableFuture<Boolean> placeOrder(String packageName, String priceName) {
		return authHelper.checkValid()
				.thenCompose(result -> placeOrderImp(packageName, priceName));
	}

	private CompletableFuture<Boolean> placeOrderImp(String packageName, String priceName) {

		return CompletableFuture.supplyAsync(() -> {
			try {
				Map<String, Object> map = new HashMap<>();
				map.put("package_name", packageName);
				map.put("price_name", priceName);
				String json = JsonUtil.serialize(map);
				Response<ResponseBody> response = this.connectionManager.getVaultApi()
						.createOrder(createJsonRequestBody(json))
						.execute();
				authHelper.checkResponseCode(response);
				return true;
			} catch (Exception e) {
				HiveException exception = new HiveException(e.getLocalizedMessage());
				throw new CompletionException(exception);
			}
		});
	}

	@Override
	public CompletableFuture<Boolean> payOrder(String orderId, List<String> txids) {
		return authHelper.checkValid()
				.thenCompose(result -> payOrderImp(orderId, txids));
	}

	private CompletableFuture<Boolean> payOrderImp(String orderId, List<String> txids) {

		return CompletableFuture.supplyAsync(() -> {
			try {
				Map<String, Object> map = new HashMap<>();
				map.put("order_id", orderId);
				map.put("pay_txids", txids);
				String json = JsonUtil.serialize(map);
				Response<ResponseBody> response = this.connectionManager.getVaultApi()
						.pay(createJsonRequestBody(json))
						.execute();
				authHelper.checkResponseCode(response);
				return true;
			} catch (Exception e) {
				HiveException exception = new HiveException(e.getLocalizedMessage());
				throw new CompletionException(exception);
			}
		});
	}

	@Override
	public CompletableFuture<Order> getOrder(String orderId) {
		return authHelper.checkValid()
				.thenCompose(result -> getOrderImp(orderId));
	}

	private CompletableFuture<Order> getOrderImp(String orderId) {
		return CompletableFuture.supplyAsync(() -> {
			try {
				Response<Order> response = this.connectionManager.getVaultApi()
						.getOrderInfo(orderId)
						.execute();
				authHelper.checkResponseCode(response);
				return response.body();
			} catch (Exception e) {
				HiveException exception = new HiveException(e.getLocalizedMessage());
				throw new CompletionException(exception);
			}
		});
	}

	@Override
	public CompletableFuture<List<Order>> getAllOrders() {
		return authHelper.checkValid()
				.thenCompose(result -> getAllOrdersImp());
	}

	private CompletableFuture<List<Order>> getAllOrdersImp() {
		return CompletableFuture.supplyAsync(() -> {
			try {
				Response<List<Order>> response = this.connectionManager.getVaultApi()
						.getOrderInfos()
						.execute();
				authHelper.checkResponseCode(response);
				return response.body();
			} catch (Exception e) {
				HiveException exception = new HiveException(e.getLocalizedMessage());
				throw new CompletionException(exception);
			}
		});
	}

	@Override
	public CompletableFuture<PricingPlan> serviceInfo() {
		return authHelper.checkValid()
				.thenCompose(result -> serviceInfoImp());
	}

	private CompletableFuture<PricingPlan> serviceInfoImp() {
		return CompletableFuture.supplyAsync(() -> {
			try {
				Response<PricingPlan> response = this.connectionManager.getVaultApi()
						.getServiceInfo()
						.execute();
				authHelper.checkResponseCode(response);
				return response.body();
			} catch (Exception e) {
				HiveException exception = new HiveException(e.getLocalizedMessage());
				throw new CompletionException(exception);
			}
		});
	}

	private RequestBody createJsonRequestBody(String json) {
		return RequestBody.create(MediaType.parse("Content-Type, application/json"), json);
	}
}
