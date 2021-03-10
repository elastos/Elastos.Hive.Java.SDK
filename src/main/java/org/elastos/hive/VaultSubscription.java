package org.elastos.hive;

import java.util.List;
import java.util.concurrent.CompletableFuture;

import org.elastos.hive.exception.HiveException;
import org.elastos.hive.payment.Order;
import org.elastos.hive.payment.PricingPlan;
import org.elastos.hive.payment.Receipt;
import org.elastos.hive.service.PaymentService;
import org.elastos.hive.service.SubscriptionService;

public class VaultSubscription {
	private SubscriptionRender render;

	public VaultSubscription(AppContext context, String userDid, String providerAddress) throws HiveException {
		render = new SubscriptionRender(context, userDid, providerAddress);
	}

	public CompletableFuture<VaultInfo> subscribe(String pricingPlan) {
		return service.subscribe(pricingPlan, VaultInfo.class);
	}

	public CompletableFuture<Void> unsubscribe() {
		return service.unsubscribe();
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

	public CompletableFuture<List<PricingPlan>> getPringPlanList() {
		return render.getPringPlanList();
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
		// TODO;
	}

	class SubscriptionRender extends ServiceEndpoint implements SubscriptionService, PaymentService {
		SubscriptionRender(AppContext context, String userDid, String providerAddress) throws HiveException {
			super(context, providerAddress, userDid);
		}

		@Override
		public <T> CompletableFuture<T> subscribe(String pricingPlan, Class<T> type) {
			return null;
		}

		@Override
		public CompletableFuture<Void> unsubscribe() {
			return null;
		}

		@Override
		public CompletableFuture<Void> activate() {
			return null;
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
