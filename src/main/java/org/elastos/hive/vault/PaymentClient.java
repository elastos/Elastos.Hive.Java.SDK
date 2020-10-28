package org.elastos.hive.vault;

import org.elastos.hive.Payment;
import org.elastos.hive.connection.ConnectionManager;
import org.elastos.hive.exception.HiveException;
import org.elastos.hive.payment.ServiceInfo;
import org.elastos.hive.payment.order.OrderInfo;
import org.elastos.hive.payment.pkg.PackageInfo;
import org.elastos.hive.utils.JsonUtil;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import retrofit2.Response;

public class PaymentClient implements Payment {

	private AuthHelper authHelper;
	private ConnectionManager connectionManager;

	public PaymentClient(AuthHelper authHelper) {
		this.authHelper = authHelper;
		this.connectionManager = authHelper.getConnectionManager();
	}


	@Override
	public CompletableFuture<PackageInfo> packageInfo() {
		return authHelper.checkValid()
				.thenCompose(result -> packageInfoImp());
	}

	private CompletableFuture<PackageInfo> packageInfoImp() {
		return CompletableFuture.supplyAsync(() -> {
			try {
				Response<PackageInfo> response = this.connectionManager.getHiveVaultApi()
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
	public CompletableFuture<Boolean> freeTrial() {
		return authHelper.checkValid()
				.thenCompose(result -> freeTrialImp());
	}

	private CompletableFuture<Boolean> freeTrialImp() {
		return CompletableFuture.supplyAsync(() -> {
			try {
				Response<PackageInfo> response = this.connectionManager.getHiveVaultApi()
						.getPackageInfo()
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
	public CompletableFuture<Boolean> createOrder(String packageName, String priceName) {
		return authHelper.checkValid()
				.thenCompose(result -> createOrderImp(packageName, priceName));
	}

	private CompletableFuture<Boolean> createOrderImp(String packageName, String priceName) {

		return CompletableFuture.supplyAsync(() -> {
			try {
				Map map = new HashMap<>();
				map.put("package_name", packageName);
				map.put("price_name", priceName);
				String json = JsonUtil.getJsonFromObject(map);
				Response response = this.connectionManager.getHiveVaultApi()
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
	public CompletableFuture<Boolean> pay(String orderId, List<String> txids) {
		return authHelper.checkValid()
				.thenCompose(result -> payImp(orderId, txids));
	}

	private CompletableFuture<Boolean> payImp(String orderId, List<String> txids) {

		return CompletableFuture.supplyAsync(() -> {
			try {
				Map map = new HashMap<>();
				map.put("order_id", orderId);
				map.put("pay_txids", txids);
				String json = JsonUtil.getJsonFromObject(map);
				Response response = this.connectionManager.getHiveVaultApi()
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
	public CompletableFuture<OrderInfo> orderInfo(String orderId) {
		return authHelper.checkValid()
				.thenCompose(result -> orderInfoImp(orderId));
	}

	private CompletableFuture<OrderInfo> orderInfoImp(String orderId) {
		return CompletableFuture.supplyAsync(() -> {
			try {
				Response<OrderInfo> response = this.connectionManager.getHiveVaultApi()
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
	public CompletableFuture<List<OrderInfo>> orderList() {
		return authHelper.checkValid()
				.thenCompose(result -> orderInfosImp());
	}

	private CompletableFuture<List<OrderInfo>> orderInfosImp() {
		return CompletableFuture.supplyAsync(() -> {
			try {
				Response<List<OrderInfo>> response = this.connectionManager.getHiveVaultApi()
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
	public CompletableFuture<ServiceInfo> serviceInfo() {
		return authHelper.checkValid()
				.thenCompose(result -> serviceInfoImp());
	}

	private CompletableFuture<ServiceInfo> serviceInfoImp() {
		return CompletableFuture.supplyAsync(() -> {
			try {
				Response<ServiceInfo> response = this.connectionManager.getHiveVaultApi()
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
