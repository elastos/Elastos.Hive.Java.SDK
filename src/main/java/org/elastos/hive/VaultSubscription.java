package org.elastos.hive;

import org.elastos.hive.exception.HiveException;
import org.elastos.hive.payment.Order;
import org.elastos.hive.payment.PricingPlan;
import org.elastos.hive.payment.Receipt;
import org.elastos.hive.service.PaymentService;
import org.elastos.hive.service.SubscriptionService;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;

import org.elastos.hive.vault.PaymentServiceRender;
import org.elastos.hive.vault.SubscriptionServiceRender;

public class VaultSubscription extends ServiceEndpoint implements SubscriptionService<VaultSubscription.VaultInfo>, PaymentService  {
	private SubscriptionServiceRender subscriptionService;
	private PaymentServiceRender paymentService;

	public VaultSubscription(AppContext context, String userDid, String providerAddress) throws HiveException {
		super(context, providerAddress, userDid);
		this.paymentService = new PaymentServiceRender(context);
		this.subscriptionService = new SubscriptionServiceRender(context);
	}

	@Override
	public CompletableFuture<VaultInfo> subscribe(String pricingPlan) {
		return CompletableFuture.supplyAsync(() -> {
			try {
				this.subscriptionService.subscribe();
				//TODO:
				return new VaultInfo(null, getAppContext().getUserDid(), null);
			} catch (HiveException e) {
				throw new CompletionException(e);
			}
		});
	}

	@Override
	public CompletableFuture<Void> unsubscribe() {
		return CompletableFuture.runAsync(() -> {
			try {
				this.subscriptionService.unsubscribe();
			} catch (HiveException e) {
				throw new CompletionException(e);
			}
		});
	}

	@Override
	public CompletableFuture<Void> activate() {
		return CompletableFuture.runAsync(() -> {
			try {
				this.subscriptionService.activate();
			} catch (HiveException e) {
				throw new CompletionException(e);
			}
		});
	}

	@Override
	public CompletableFuture<Void> deactivate() {
		return CompletableFuture.runAsync(() -> {
			try {
				this.subscriptionService.deactivate();
			} catch (HiveException e) {
				throw new CompletionException(e);
			}
		});
	}

	@Override
	public CompletableFuture<VaultInfo> checkSubscription() {
		//TODO:
		throw new UnsupportedOperationException();
	}

	@Override
	public CompletableFuture<List<PricingPlan>> getPricingPlanList() {
		return CompletableFuture.supplyAsync(()-> {
			try {
				return paymentService.getPricingPlanList();
			} catch (HiveException e) {
				throw new CompletionException(e);
			}
		});
	}

	@Override
	public CompletableFuture<PricingPlan> getPricingPlan(String planName) {
		return CompletableFuture.supplyAsync(()-> {
			try {
				return paymentService.getPricingPlan(planName);
			} catch (HiveException e) {
				throw new CompletionException(e);
			}
		});
	}

	@Override
	public CompletableFuture<Order> placeOrder(String planName) {
		return CompletableFuture.supplyAsync(()-> {
			try {
				return paymentService.getOrderInfo(paymentService.createPricingOrder(planName));
			} catch (HiveException e) {
				throw new CompletionException(e);
			}
		});
	}

	@Override
	public CompletableFuture<Order> getOrder(String orderId) {
		return CompletableFuture.supplyAsync(()-> {
			try {
				return paymentService.getOrderInfo(orderId);
			} catch (HiveException e) {
				throw new CompletionException(e);
			}
		});
	}

	@Override
	public CompletableFuture<Receipt> payOrder(String orderId, List<String> transIds) {
		return CompletableFuture.supplyAsync(()-> {
			try {
				paymentService.payOrder(orderId, transIds);
				//TODO:
				return null;
			} catch (HiveException e) {
				throw new CompletionException(e);
			}
		});
	}

	@Override
	public CompletableFuture<Receipt> getReceipt(String receiptId) {
		throw new UnsupportedOperationException();
	}

	public class VaultInfo {
		private String myDid;
		private String appInstanceDid;
		private String appId;
		private String provider;

		private String serviceDid;
		private String pricingUsing;
		private String createTime;
		private String modifyTime;
		private long maxSpace;
		private long dbSpaceUsed;
		private long fileSpaceUsed;
		private boolean existing;

		public VaultInfo(String appInstanceDid, String myDid, String serviceDid) {
			this.appInstanceDid = appInstanceDid;
			this.myDid = myDid;
			this.serviceDid = serviceDid;
		}

		public String getAppInstanceDid() {
			return appInstanceDid;
		}

		public String getMyDid() {
			return myDid;
		}

		public String getServiceDid() {
			return serviceDid;
		}
	}
}
