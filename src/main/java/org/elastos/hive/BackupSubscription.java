package org.elastos.hive;

import org.elastos.hive.Backup.PropertySet;
import org.elastos.hive.exception.HiveException;
import org.elastos.hive.exception.UnsupportedMethodException;
import org.elastos.hive.network.response.VaultInfoResponseBody;
import org.elastos.hive.payment.Order;
import org.elastos.hive.payment.PricingPlan;
import org.elastos.hive.payment.Receipt;
import org.elastos.hive.service.PaymentService;
import org.elastos.hive.service.SubscriptionService;
import org.elastos.hive.vault.HttpExceptionHandler;
import org.elastos.hive.vault.PaymentServiceRender;
import org.elastos.hive.vault.SubscriptionServiceRender;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;

public class BackupSubscription extends ServiceEndpoint
	implements SubscriptionService<Backup.PropertySet>, PaymentService, HttpExceptionHandler {

	private SubscriptionServiceRender subscriptionService;
	private PaymentServiceRender paymentService;

	public BackupSubscription(AppContext context, String providerAddress) throws HiveException {
		super(context, providerAddress);
		this.paymentService = new PaymentServiceRender(this);
		this.subscriptionService = new SubscriptionServiceRender(this);
	}

	@Override
	public CompletableFuture<List<PricingPlan>> getPricingPlanList() {
		return CompletableFuture.supplyAsync(()-> {
			try {
				return paymentService.getBackupPlanList();
			} catch (Exception e) {
				throw new CompletionException(convertException(e));
			}
		});
	}

	@Override
	public CompletableFuture<PricingPlan> getPricingPlan(String planName) {
		return CompletableFuture.supplyAsync(()-> {
			try {
				return paymentService.getBackupPlan(planName);
			} catch (Exception e) {
				throw new CompletionException(convertException(e));
			}
		});
	}

	public CompletableFuture<Backup.PropertySet> subscribe() {
		return this.subscribe(null);
	}

	@Override
	public CompletableFuture<Backup.PropertySet> subscribe(String reserved) {
		return CompletableFuture.runAsync(() -> {
			try {
				this.subscriptionService.subscribeBackup();
			} catch (Exception e) {
				throw new CompletionException(convertException(e));
			}
		}).thenApplyAsync(success -> {
			try {
				return getBackupInfoByResponseBody(this.subscriptionService.getBackupVaultInfo());
			} catch (Exception e) {
				throw new CompletionException(convertException(e));
			}
		});
	}

	@Override
	public CompletableFuture<Void> unsubscribe() {
		throw new UnsupportedMethodException();
	}

	@Override
	public CompletableFuture<Void> activate() {
		throw new UnsupportedMethodException();
	}

	@Override
	public CompletableFuture<Void> deactivate() {
		throw new UnsupportedMethodException();
	}

	@Override
	public CompletableFuture<Backup.PropertySet> checkSubscription() {
		return CompletableFuture.supplyAsync(()-> {
			try {
				return getBackupInfoByResponseBody(this.subscriptionService.getBackupVaultInfo());
			} catch (Exception e) {
				throw new CompletionException(convertException(e));
			}
		});
	}

	private Backup.PropertySet getBackupInfoByResponseBody(VaultInfoResponseBody body) {
		// TODO:
		return null;
	}

	@Override
	public CompletableFuture<Order> placeOrder(String planName) {
		return CompletableFuture.supplyAsync(()-> {
			try {
				return paymentService.getOrderInfo(paymentService.createBackupOrder(planName));
			} catch (Exception e) {
				throw new CompletionException(convertException(e));
			}
		});
	}

	@Override
	public CompletableFuture<Order> getOrder(String orderId) {
		return CompletableFuture.supplyAsync(()-> {
			try {
				return paymentService.getOrderInfo(orderId);
			} catch (Exception e) {
				throw new CompletionException(convertException(e));
			}
		});
	}

	@Override
	public CompletableFuture<Receipt> payOrder(String orderId, String transactionId) {
		return CompletableFuture.supplyAsync(()-> {
			try {
				//TODO:paymentService.payOrder(orderId, transactionId);
				//TODO:
				return null;
			} catch (Exception e) {
				throw new CompletionException(convertException(e));
			}
		});
	}

	@Override
	public CompletableFuture<Receipt> getReceipt(String receiptId) {
		// TODO:
		throw new UnsupportedMethodException();
	}

	@Override
	public CompletableFuture<List<Order>> getOrderList() {
		// TODO:
		throw new UnsupportedMethodException();
	}

	@Override
	public CompletableFuture<List<Receipt>> getReceiptList() {
		// TODO:
		throw new UnsupportedMethodException();
	}
}
