package org.elastos.hive;

import org.elastos.hive.exception.HiveException;
import org.elastos.hive.exception.NotImplementedException;
import org.elastos.hive.subscription.BackupInfo;
import org.elastos.hive.subscription.PricingPlan;
import org.elastos.hive.subscription.payment.Order;
import org.elastos.hive.subscription.payment.PaymentController;
import org.elastos.hive.subscription.payment.Receipt;
import org.elastos.hive.service.PaymentService;
import org.elastos.hive.service.SubscriptionService;
import org.elastos.hive.subscription.SubscriptionController;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;

public class BackupSubscription extends ServiceEndpoint
	implements SubscriptionService<Backup.PropertySet>, PaymentService {

	private SubscriptionController subscriptionController;
	private PaymentController paymentController;

	public BackupSubscription(AppContext context, String providerAddress) throws HiveException {
		super(context, providerAddress);
		subscriptionController = new SubscriptionController(this.getConnectionManager());
		paymentController = new PaymentController(this);
	}

	@Override
	public CompletableFuture<List<PricingPlan>> getPricingPlanList() {
		return CompletableFuture.supplyAsync(()-> {
			try {
				return subscriptionController.getBackupPricingPlanList();
			} catch (RuntimeException | HiveException e) {
				throw new CompletionException(e);
			}
		});
	}

	@Override
	public CompletableFuture<PricingPlan> getPricingPlan(String planName) {
		return CompletableFuture.supplyAsync(()-> {
			try {
				return subscriptionController.getBackupPricingPlan(planName);
			} catch (RuntimeException | HiveException e) {
				throw new CompletionException(e);
			}
		});
	}

	public CompletableFuture<Backup.PropertySet> subscribe() {
		return this.subscribe(null);
	}

	@Override
	public CompletableFuture<Backup.PropertySet> subscribe(String reserved) {
		return CompletableFuture.supplyAsync(()-> {
			try {
				subscriptionController.subscribeToBackup(null);
				return getPropertySet();
			} catch (HiveException e) {
				throw new CompletionException(e);
			} catch (RuntimeException e) {
				throw new CompletionException(e);
			}
		});
	}

	@Override
	public CompletableFuture<Void> unsubscribe() {
		throw new NotImplementedException();
	}

	@Override
	public CompletableFuture<Backup.PropertySet> checkSubscription() {
		return CompletableFuture.supplyAsync(()-> {
			try {
				return getPropertySet();
			} catch (Exception e) {
				throw new CompletionException(e);
			}
		});
	}

	private Backup.PropertySet getPropertySet() throws HiveException {
		BackupInfo body = subscriptionController.getBackupInfo();
		// TODO: serviceDid
		return new Backup.PropertySet()
				.setPricingPlan(body.getPricingUsing())
				.setCreated(body.getStartTime())
				.setUpdated(body.getModifyTime())
				.setQuota(body.getMaxStorage())
				.setUsedSpace(body.getFileUseStorage() + body.getDbUseStorage());
	}

	@Override
	public CompletableFuture<Order> placeOrder(String planName) {
		return CompletableFuture.supplyAsync(()-> {
			try {
				return paymentController.getOrderInfo(paymentController.createOrder(null, planName));
			} catch (HiveException e) {
				throw new CompletionException(e);
			} catch (RuntimeException e) {
				throw new CompletionException(e);
			}
		});
	}

	@Override
	public CompletableFuture<Order> getOrder(String orderId) {
		return CompletableFuture.supplyAsync(()-> {
			try {
				return paymentController.getOrderInfo(orderId);
			} catch (HiveException e) {
				throw new CompletionException(e);
			} catch (RuntimeException e) {
				throw new CompletionException(e);
			}
		});
	}

	@Override
	public CompletableFuture<Receipt> payOrder(String orderId, String transactionId) {
		return CompletableFuture.supplyAsync(()-> {
			try {
				paymentController.payOrder(orderId, Collections.singletonList(transactionId));
				return null;
			} catch (HiveException e) {
				throw new CompletionException(e);
			} catch (RuntimeException e) {
				throw new CompletionException(e);
			}
		});
	}

	@Override
	public CompletableFuture<Receipt> getReceipt(String receiptId) {
		throw new NotImplementedException();
	}

	@Override
	public CompletableFuture<List<Order>> getOrderList() {
		throw new NotImplementedException();
	}

	@Override
	public CompletableFuture<List<Receipt>> getReceiptList() {
		throw new NotImplementedException();
	}
}
