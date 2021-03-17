package org.elastos.hive;

import com.fasterxml.jackson.databind.JsonNode;

import org.elastos.hive.connection.ConnectionManager;
import org.elastos.hive.exception.HiveException;
import org.elastos.hive.exception.VaultAlreadyExistException;
import org.elastos.hive.payment.Order;
import org.elastos.hive.payment.PricingInfo;
import org.elastos.hive.payment.PricingPlan;
import org.elastos.hive.payment.Receipt;
import org.elastos.hive.service.PaymentService;
import org.elastos.hive.service.SubscriptionService;
import org.elastos.hive.subscribe.CreateServiceResult;
import org.elastos.hive.utils.JsonUtil;
import org.elastos.hive.utils.ResponseHelper;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.function.Supplier;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Response;

public class VaultSubscription {
	private SubscriptionRender render;

	public VaultSubscription(AppContext context, String userDid, String providerAddress) throws HiveException {
		this.render = new SubscriptionRender(context, userDid, providerAddress);
	}

	public CompletableFuture<VaultInfo> subscribe() {
		return render.subscribe(null, VaultInfo.class);
	}

	public CompletableFuture<VaultInfo> subscribe(String pricingPlan) {
		return render.subscribe(pricingPlan, VaultInfo.class);
	}

	public CompletableFuture<Void> unsubscribe() {
		return render.unsubscribe();
	}

	public CompletableFuture<Void> activate() {
		return render.activate();
	}

	public CompletableFuture<Void> deactivate() {
		return render.deactivate();
	}

	public CompletableFuture<VaultInfo> checkSubscription() {
		return render.checkSubscription();
	}

	public CompletableFuture<List<PricingPlan>> getPricingPlanList() {
		return render.getPricingPlanList();
	}

	public CompletableFuture<PricingPlan> getPricingPlan(String planName) {
		return render.getPricingPlan(planName);
	}

	public CompletableFuture<Order> placeOrder(String planName) {
		return render.placeOrder(planName);
	}

	public CompletableFuture<Order> getOrder(String orderId) {
		return render.getOrder(orderId);
	}

	public CompletableFuture<Receipt> payOrder(String orderId, String transId) {
		return render.payOrder(orderId, transId);
	}

	public CompletableFuture<Receipt> getReceipt(String receiptId) {
		return render.getReceipt(receiptId);
	}

	public class VaultInfo {
		private String appInstanceDid;
		private String userDid;
		private String serviceDid;

		public VaultInfo(String appInstanceDid, String userDid, String serviceDid) {
			this.appInstanceDid = appInstanceDid;
			this.userDid = userDid;
			this.serviceDid = serviceDid;
		}

		public String getAppInstanceDid() {
			return appInstanceDid;
		}

		public String getUserDid() {
			return userDid;
		}

		public String getServiceDid() {
			return serviceDid;
		}
	}

	class SubscriptionRender extends ServiceEndpoint implements SubscriptionService, PaymentService {
		private AppContext appContext;
		private ConnectionManager connectionManager;

		SubscriptionRender(AppContext context, String userDid, String providerAddress) throws HiveException {
			super(context, providerAddress, userDid);
			this.appContext = context;
			this.connectionManager = appContext.getConnectionManager();
		}

		@Override
		public <T> CompletableFuture<T> subscribe(String pricingPlan, Class<T> type) {
			return CompletableFuture.supplyAsync(() -> {
				VaultInfo vaultInfo = new VaultInfo(null, appContext.getUserDid(), null);
				Response<CreateServiceResult> response;
				try {
					response = connectionManager.getSubscriptionApi().createVault().execute();
					if(response.body().existing()) {
						throw new VaultAlreadyExistException("The vault already exists");
					}
				} catch (IOException e) {
					e.printStackTrace();
				}
				return (T) vaultInfo;
			});
		}

		@Override
		public CompletableFuture<Void> unsubscribe() {
			return CompletableFuture.runAsync(() -> {
				try {
					connectionManager.getSubscriptionApi().removeVault().execute();
				} catch (IOException e) {
					e.printStackTrace();
				}
			});
		}

		@Override
		public CompletableFuture<Void> activate() {
			return CompletableFuture.runAsync(() -> {
				try {
					connectionManager.getSubscriptionApi().unfreeze().execute();
				} catch (IOException e) {
					e.printStackTrace();
				}
			});
		}

		@Override
		public CompletableFuture<Void> deactivate() {
			return CompletableFuture.runAsync(() -> {
				try {
					connectionManager.getSubscriptionApi().freeze().execute();
				} catch (IOException e) {
					e.printStackTrace();
				}
			});
		}

		@Override
		public <T> CompletableFuture<T> checkSubscription() {
			return null;
		}

		@Override
		public CompletableFuture<List<PricingPlan>> getPricingPlanList() {
			return CompletableFuture.supplyAsync(() -> {
				Response response = null;
				try {
					response = connectionManager.getPaymentApi()
							.getPackageInfo()
							.execute();
					String ret = ResponseHelper.getValue(response, String.class);
					return PricingInfo.deserialize(ret).pricingPlans();
				} catch (IOException e) {
					e.printStackTrace();
				}
				return null;
			});

		}

		@Override
		public CompletableFuture<PricingPlan> getPricingPlan(String planName) {
			return CompletableFuture.supplyAsync(() -> {
				Response response = null;
				try {
					response = connectionManager.getPaymentApi()
							.getPricingPlan(planName)
							.execute();
					String ret = ResponseHelper.getValue(response, String.class);
					return PricingPlan.deserialize(ret, PricingPlan.class);
				} catch (IOException e) {
					e.printStackTrace();
				}
				return null;
			});
		}

		@Override
		public CompletableFuture<Order> placeOrder(String planName) {
			return CompletableFuture.supplyAsync(() -> {
				try {
					Map<String, Object> map = new HashMap<>();
					map.put("pricing_name", planName);
					String json = JsonUtil.serialize(map);
					Response<ResponseBody> response = connectionManager.getPaymentApi()
							.createOrder(RequestBody.create(MediaType.parse("Content-Type, application/json"), json))
							.execute();
					String ret = ResponseHelper.getValue(response, String.class);
					return Order.deserialize(ret, Order.class);
				} catch (IOException e) {
					e.printStackTrace();
				}
				return null;
			});
		}

		@Override
		public CompletableFuture<Order> getOrder(String orderId) {
			return CompletableFuture.supplyAsync(() -> {
				try {
					Response response = connectionManager.getPaymentApi()
							.getOrderInfo(orderId)
							.execute();
					JsonNode ret = ResponseHelper.getValue(response, JsonNode.class);
					return Order.deserialize(ret.get("order_info").toString(), Order.class);
				} catch (IOException e) {
					e.printStackTrace();
				}
				return null;
			});
		}

		@Override
		public CompletableFuture<Receipt> payOrder(String orderId, String transId) {
			return CompletableFuture.supplyAsync(() -> {
				try {
					Map<String, Object> map = new HashMap<>();
					map.put("order_id", orderId);
					map.put("pay_txids", transId);
					String json = JsonUtil.serialize(map);
					Response<ResponseBody> response = connectionManager.getPaymentApi()
							.payOrder(RequestBody.create(MediaType.parse("Content-Type, application/json"), json))
							.execute();
					JsonNode ret = ResponseHelper.getValue(response, JsonNode.class);
					return Receipt.deserialize(ret.get("order_info").toString(), Receipt.class);
				} catch (IOException e) {
					e.printStackTrace();
				}
				return null;
			});
		}

		@Override
		public CompletableFuture<Receipt> getReceipt(String receiptId) {
			return null;
		}
	}
}
