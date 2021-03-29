package org.elastos.hive;

import org.elastos.hive.exception.HiveException;
import org.elastos.hive.payment.Order;
import org.elastos.hive.payment.PricingPlan;
import org.elastos.hive.payment.Receipt;
import org.elastos.hive.service.PaymentService;
import org.elastos.hive.service.SubscriptionService;
import org.elastos.hive.vault.PaymentServiceRender;
import org.elastos.hive.vault.SubscriptionServiceRender;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;

public class BackupSubscription extends ServiceEndpoint implements SubscriptionService<BackupSubscription.BackupInfo>, PaymentService {
	private SubscriptionServiceRender subscriptionService;
	private PaymentServiceRender paymentService;

	public BackupSubscription(AppContext context, String userDid, String providerAddress) throws HiveException {
		super(context, providerAddress, userDid);
		this.paymentService = new PaymentServiceRender(context);
		this.subscriptionService = new SubscriptionServiceRender(context);
	}

	@Override
	public CompletableFuture<BackupInfo> subscribe(String pricingPlan) {
		return CompletableFuture.supplyAsync(() -> {
			try {
				this.subscriptionService.subscribeBackup();
				//TODO:
				return new BackupSubscription.BackupInfo();
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
		throw new UnsupportedOperationException();
	}

	@Override
	public CompletableFuture<Void> deactivate() {
		throw new UnsupportedOperationException();
	}

	@Override
	public CompletableFuture<BackupInfo> checkSubscription() {
		throw new UnsupportedOperationException();
	}

	public class BackupInfo {
		// TODO;
	}

	@Override
	public CompletableFuture<List<PricingPlan>> getPricingPlanList() {
		return CompletableFuture.supplyAsync(()-> {
			try {
				return paymentService.getBackupPlanList();
			} catch (HiveException e) {
				throw new CompletionException(e);
			}
		});
	}

	@Override
	public CompletableFuture<PricingPlan> getPricingPlan(String planName) {
		return CompletableFuture.supplyAsync(()-> {
			try {
				return paymentService.getBackupPlan(planName);
			} catch (HiveException e) {
				throw new CompletionException(e);
			}
		});
	}

	@Override
	public CompletableFuture<Order> placeOrder(String planName) {
		return CompletableFuture.supplyAsync(()-> {
			try {
				return paymentService.getOrderInfo(paymentService.createBackupOrder(planName));
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

}
