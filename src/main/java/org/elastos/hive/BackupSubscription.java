package org.elastos.hive;

import java.util.List;
import java.util.concurrent.CompletableFuture;

import org.elastos.hive.exception.HiveException;
import org.elastos.hive.payment.Order;
import org.elastos.hive.payment.PricingPlan;
import org.elastos.hive.payment.Receipt;
import org.elastos.hive.service.PaymentService;
import org.elastos.hive.service.SubscriptionService;

public class BackupSubscription {
	private SubscriptionService service;

	public BackupSubscription(AppContext context, String userDid, String providerAddress) throws HiveException {
		service = new SubscriptionRender(context, userDid, providerAddress);
	}

	public CompletableFuture<BackupInfo> subscribe(String pricingPlan) {
		return service.subscribe(pricingPlan, BackupInfo.class);
	}

	public CompletableFuture<Void> unsubscribe() {
		return service.unsubscribe();
	}

	public CompletableFuture<Void> activate() {
		return service.activate();
	}

	public CompletableFuture<Void> deactivate() {
		return service.deactivate();
	}

	public CompletableFuture<BackupInfo> checkSubscription() {
		return service.checkSubscription();
	}

	public class BackupInfo {
		// TODO;
	}

	class SubscriptionRender extends ServiceEndpoint implements SubscriptionService, PaymentService {
		protected SubscriptionRender(AppContext context, String userDid, String providerAddress)
				throws HiveException {
			super(context, providerAddress, userDid);
		}

		@Override
		public <T> CompletableFuture<T> subscribe(String pricingPlan, Class<T> type) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public CompletableFuture<Void> unsubscribe() {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public CompletableFuture<Void> activate() {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public CompletableFuture<Void> deactivate() {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public <T> CompletableFuture<T> checkSubscription() {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public CompletableFuture<List<PricingPlan>> getPricingPlanList() {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public CompletableFuture<PricingPlan> getPricingPlan(String planName) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public CompletableFuture<Order> placeOrder(String planName) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public CompletableFuture<Order> getOrder(String orderId) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public CompletableFuture<Receipt> payOrder(String orderId, String transId) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public CompletableFuture<Receipt> getReceipt(String receiptId) {
			// TODO Auto-generated method stub
			return null;
		}
	}
}
