package org.elastos.hive;

import org.elastos.hive.exception.HiveException;
import org.elastos.hive.exception.VaultAlreadyExistException;
import org.elastos.hive.payment.Order;
import org.elastos.hive.payment.PricingPlan;
import org.elastos.hive.payment.Receipt;
import org.elastos.hive.service.PaymentService;
import org.elastos.hive.service.SubscriptionService;
import org.elastos.hive.subscribe.CreateServiceResult;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.function.Function;

import retrofit2.Response;

public class VaultSubscription {
	private SubscriptionRender render;
	private AppContext context;

	public VaultSubscription(AppContext context, String userDid, String providerAddress) throws HiveException {
		this.context = context;
		this.render = new SubscriptionRender(context, userDid, providerAddress);
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

		SubscriptionRender(AppContext context, String userDid, String providerAddress) throws HiveException {
			super(context, providerAddress, userDid);
			this.appContext = context;
		}

		@Override
		public <T> CompletableFuture<T> subscribe(String pricingPlan, Class<T> type) {
			return CompletableFuture.runAsync(() -> {
				try {
					appContext.checkToken();
				} catch (HiveException e) {
					throw new CompletionException(e);
				}
			}).thenApplyAsync((Function<Void, T>) aVoid -> {
				VaultInfo vaultInfo = new VaultInfo(null, appContext.getUserDid(), null);
				Response<CreateServiceResult> response;
				try {
					response = appContext.getConnectionManager().getVaultSubscriptionApi().createVault().execute();
					if(response.body().existing()) {
						throw new VaultAlreadyExistException("The vault already exists");
					}
				} catch (IOException e) {
					e.printStackTrace();
				}
				return (T)vaultInfo;
			});
		}

		@Override
		public CompletableFuture<Void> unsubscribe() {
			return null;
		}

		private void activateImpl(AuthToken token) {
			// TODO;
		}

		@Override
		public CompletableFuture<Void> activate() {
			return CompletableFuture.supplyAsync(() -> {
				try {
					System.out.print("Check access token here, otherwise request the access token");
					return context.getAuthToken();
				} catch (HiveException e) {
					return null;
				}
			}).thenAcceptAsync(token -> {
				System.out.print("Call activate API here");
				activateImpl(token);
			}).exceptionally(ex -> {
				System.out.print("Handle specific exception here.");
				return null;
			});
		}

		@Override
		public CompletableFuture<Void> deactivate() {
			return null;
		}

		@Override
		public <T> CompletableFuture<T> checkSubscription() {
			return null;
		}

		@Override
		public CompletableFuture<List<PricingPlan>> getPricingPlanList() {
			return null;
		}

		@Override
		public CompletableFuture<PricingPlan> getPricingPlan(String planName) {
			return null;
		}

		@Override
		public CompletableFuture<Order> placeOrder(String planName) {
			return null;
		}

		@Override
		public CompletableFuture<Order> getOrder(String orderId) {
			return null;
		}

		@Override
		public CompletableFuture<Receipt> payOrder(String orderId, String transId) {
			return null;
		}

		@Override
		public CompletableFuture<Receipt> getReceipt(String receiptId) {
			return null;
		}
	}
}
