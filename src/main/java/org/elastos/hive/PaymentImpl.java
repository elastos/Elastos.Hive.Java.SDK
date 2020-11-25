package org.elastos.hive;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.elastos.hive.connection.ConnectionManager;
import org.elastos.hive.exception.HiveException;
import org.elastos.hive.payment.Order;
import org.elastos.hive.payment.PricingInfo;
import org.elastos.hive.payment.PricingPlan;
import org.elastos.hive.payment.UsingPlan;
import org.elastos.hive.utils.JsonUtil;
import org.elastos.hive.utils.ResponseHelper;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Response;

public class PaymentImpl implements Payment {

	private AuthHelper authHelper;
	private ConnectionManager connectionManager;

	public PaymentImpl(AuthHelper authHelper) {
		this.authHelper = authHelper;
		this.connectionManager = authHelper.getConnectionManager();
	}


	@Override
	public CompletableFuture<PricingInfo> getPaymentInfo() {
		return authHelper.checkValid().thenApply(aVoid -> {
			try {
				return getAllPricingPlansImp();
			} catch (HiveException e) {
				e.printStackTrace();
				throw new CompletionException(e);
			}
		});
	}

	private PricingInfo getAllPricingPlansImp() throws HiveException {
		try {
			Response response = this.connectionManager.getPaymentApi()
					.getPackageInfo()
					.execute();
			authHelper.checkResponseWithRetry(response);
			String ret = ResponseHelper.getValue(response, String.class);
			return PricingInfo.deserialize(ret);
		} catch (Exception e) {
			throw new HiveException(e.getMessage());
		}
	}

	@Override
	public CompletableFuture<PricingPlan> getPricingPlan(String planName) {
		return authHelper.checkValid().thenApply(aVoid -> {
			try {
				return getPricingPlansImp(planName);
			} catch (HiveException e) {
				e.printStackTrace();
				throw new CompletionException(e);
			}
		});
	}

	private PricingPlan getPricingPlansImp(String planName) throws HiveException {
		try {
			Response response = this.connectionManager.getPaymentApi()
					.getPricingPlan(planName)
					.execute();
			authHelper.checkResponseWithRetry(response);
			String ret = ResponseHelper.getValue(response, String.class);
			return PricingPlan.deserialize(ret);
		} catch (Exception e) {
			throw new HiveException(e.getMessage());
		}
	}

	@Override
	public CompletableFuture<String> placeOrder(String priceName) {
		return authHelper.checkValid().thenApply(aVoid -> {
			try {
				return placeOrderImp(priceName);
			} catch (HiveException e) {
				e.printStackTrace();
				throw new CompletionException(e);
			}
		});
	}

	private String placeOrderImp(String priceName) throws HiveException {
		try {
			Map<String, Object> map = new HashMap<>();
			map.put("pricing_name", priceName);
			String json = JsonUtil.serialize(map);
			Response<ResponseBody> response = this.connectionManager.getPaymentApi()
					.createOrder(createJsonRequestBody(json))
					.execute();
			authHelper.checkResponseWithRetry(response);
			JsonNode ret = ResponseHelper.getValue(response, JsonNode.class);
			return ret.get("order_id").textValue();
		} catch (Exception e) {
			throw new HiveException(e.getMessage());
		}
	}

	@Override
	public CompletableFuture<Boolean> payOrder(String orderId, List<String> txids) {
		return authHelper.checkValid().thenApply(aVoid -> {
			try {
				return payOrderImp(orderId, txids);
			} catch (HiveException e) {
				e.printStackTrace();
				throw new CompletionException(e);
			}
		});
	}

	private boolean payOrderImp(String orderId, List<String> txids) throws HiveException {
		try {
			Map<String, Object> map = new HashMap<>();
			map.put("order_id", orderId);
			map.put("pay_txids", txids);
			String json = JsonUtil.serialize(map);
			Response<ResponseBody> response = this.connectionManager.getPaymentApi()
					.payOrder(createJsonRequestBody(json))
					.execute();
			authHelper.checkResponseWithRetry(response);
			return true;
		} catch (Exception e) {
			throw new HiveException(e.getMessage());
		}
	}

	@Override
	public CompletableFuture<Order> getOrder(String orderId) {
		return authHelper.checkValid().thenApply(aVoid -> {
			try {
				return getOrderImp(orderId);
			} catch (HiveException e) {
				e.printStackTrace();
				throw new CompletionException(e);
			}
		});
	}

	private Order getOrderImp(String orderId) throws HiveException {
		try {
			Response response = this.connectionManager.getPaymentApi()
					.getOrderInfo(orderId)
					.execute();
			authHelper.checkResponseWithRetry(response);
			JsonNode ret = ResponseHelper.getValue(response, JsonNode.class);
			String orderInfo = ret.get("order_info").toString();
			return Order.deserialize(orderInfo);
		} catch (Exception e) {
			throw new HiveException(e.getMessage());
		}
	}

	@Override
	public CompletableFuture<List<Order>> getAllOrders() {
		return authHelper.checkValid().thenApply(aVoid -> {
			try {
				return getAllOrdersImp();
			} catch (HiveException e) {
				e.printStackTrace();
				throw new CompletionException(e);
			}
		});
	}

	private List<Order> getAllOrdersImp() throws HiveException {
		try {
			Response response = this.connectionManager.getPaymentApi()
					.getOrderList()
					.execute();
			authHelper.checkResponseWithRetry(response);
			JsonNode ret = ResponseHelper.getValue(response, JsonNode.class);
			String orderInfo = ret.get("order_info_list").toString();
			ObjectMapper mapper = new ObjectMapper();
			List<Order> orders = mapper.readValue(orderInfo,new TypeReference<List<Order>>(){});
			return orders;
		} catch (Exception e) {
			throw new HiveException(e.getMessage());
		}
	}

	@Override
	public CompletableFuture<UsingPlan> getUsingPricePlan() {
		return authHelper.checkValid().thenApply(aVoid -> {
			try {
				return getUsingPricePlanImp();
			} catch (HiveException e) {
				e.printStackTrace();
				throw new CompletionException(e);
			}
		});
	}


	private UsingPlan getUsingPricePlanImp() throws HiveException {
		try {
			Response response = this.connectionManager.getPaymentApi()
					.getServiceInfo()
					.execute();
			authHelper.checkResponseWithRetry(response);
			JsonNode ret = ResponseHelper.getValue(response, JsonNode.class).get("vault_service_info");
			if(null == ret) return null;
			return UsingPlan.deserialize(ret.toString());
		} catch (Exception e) {
			throw new HiveException(e.getMessage());
		}
	}

	@Override
	public CompletableFuture<String> getPaymentVersion() {
		return authHelper.checkValid().thenApply(aVoid -> {
			try {
				return getPaymentVersionImp();
			} catch (HiveException e) {
				e.printStackTrace();
				throw new CompletionException(e);
			}
		});
	}

	private String getPaymentVersionImp() throws HiveException {
		try {
			Response response = this.connectionManager.getPaymentApi()
					.getPaymentVersion()
					.execute();
			authHelper.checkResponseWithRetry(response);
			JsonNode ret = ResponseHelper.getValue(response, JsonNode.class);
			String version = ret.get("version").textValue();
			return version;
		} catch (Exception e) {
			throw new HiveException(e.getMessage());
		}
	}

	private RequestBody createJsonRequestBody(String json) {
		return RequestBody.create(MediaType.parse("Content-Type, application/json"), json);
	}
}
