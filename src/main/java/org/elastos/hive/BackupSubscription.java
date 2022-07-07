package org.elastos.hive;

import org.elastos.hive.exception.HiveException;
import org.elastos.hive.exception.NotImplementedException;
import org.elastos.hive.subscription.AppInfo;
import org.elastos.hive.subscription.BackupInfo;
import org.elastos.hive.subscription.PricingPlan;
import org.elastos.hive.subscription.SubscriptionController;
import org.elastos.hive.subscription.payment.Order;
import org.elastos.hive.subscription.payment.PaymentController;
import org.elastos.hive.subscription.payment.Receipt;

import org.elastos.hive.service.PaymentService;
import org.elastos.hive.service.SubscriptionService;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;

/**
 * The backup subscription is for subscribe or unsubscribe the backup service.
 *
 * <p>With the backup service, the vault data can be backup for security purpose.</p>
 */
public class BackupSubscription extends ServiceEndpoint
				implements SubscriptionService<BackupInfo>, PaymentService {

	private SubscriptionController subscriptionController;
	private PaymentController paymentController;

	/**
	 * Create by the application context and the address of the provider which can save the vault data.
	 *
	 * @param context The application context.
	 * @param providerAddress The address of the provider.
	 * @throws HiveException See {@link HiveException}
	 */
	public BackupSubscription(AppContext context, String providerAddress) throws HiveException {
		super(context, providerAddress);
		subscriptionController = new SubscriptionController(this);
		paymentController = new PaymentController(this);
	}

	/**
	 * Create by the application context and the address of the provider which can save the vault data.
	 *
	 * @param context The application context.
	 * @throws HiveException See {@link HiveException}
	 */
	public BackupSubscription(AppContext context) throws HiveException {
		this(context, null);
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
			if (planName == null)
				throw new IllegalArgumentException("Empty plan name");

			try {
				return subscriptionController.getBackupPricingPlan(planName);
			} catch (RuntimeException | HiveException e) {
				throw new CompletionException(e);
			}
		});
	}

	@Override
	public CompletableFuture<BackupInfo> subscribe() {
		return CompletableFuture.supplyAsync(()-> {
			try {
				return subscriptionController.subscribeToBackup();
			} catch (HiveException | RuntimeException e) {
				throw new CompletionException(e);
			}
		});
	}

	@Override
	public CompletableFuture<Void> activate() {
		throw new NotImplementedException();
	}

	@Override
	public CompletableFuture<Void> deactivate() {
		throw new NotImplementedException();
	}

	@Override
	public CompletableFuture<Void> unsubscribe() {
		return CompletableFuture.runAsync(()-> {
			try {
				subscriptionController.unsubscribeBackup();
			} catch (HiveException | RuntimeException e) {
				throw new CompletionException(e);
			}
		});
	}

	@Override
	public CompletableFuture<BackupInfo> checkSubscription() {
		return CompletableFuture.supplyAsync(()-> {
			try {
				return subscriptionController.getBackupInfo();
			} catch (HiveException | RuntimeException e) {
				throw new CompletionException(e);
			}
		});
	}

	@Override
	public CompletableFuture<List<AppInfo>> getAppStats() {
		throw new NotImplementedException();
	}

	@Override
	public CompletableFuture<Order> placeOrder(String planName) {
		return CompletableFuture.supplyAsync(() -> {
			try {
				return paymentController.placeOrder("backup", planName);
			} catch (HiveException | RuntimeException e) {
				throw new CompletionException(e);
			}
		});
	}

	@Override
	public CompletableFuture<Order> getOrder(int orderId) {
		return CompletableFuture.supplyAsync(() -> {
			try {
				return paymentController.getBackupOrder(orderId);
			} catch (HiveException | RuntimeException e) {
				throw new CompletionException(e);
			}
		});
	}

	@Override
	public CompletableFuture<Receipt> settleOrder(int orderId) {
		return CompletableFuture.supplyAsync(() -> {
			try {
				return paymentController.settleOrder(orderId);
			} catch (HiveException | RuntimeException e) {
				throw new CompletionException(e);
			}
		});
	}

	@Override
	public CompletableFuture<List<Order>> getOrderList() {
		return CompletableFuture.supplyAsync(() -> {
			try {
				return paymentController.getOrders("backup");
			} catch (HiveException | RuntimeException e) {
				throw new CompletionException(e);
			}
		});
	}

	@Override
	public CompletableFuture<Receipt> getReceipt(int orderId) {
		return CompletableFuture.supplyAsync(() -> {
			try {
				return paymentController.getReceipt(orderId);
			} catch (HiveException | RuntimeException e) {
				throw new CompletionException(e);
			}
		});
	}

	@Override
	public CompletableFuture<List<Receipt>> getReceipts() {
		return CompletableFuture.supplyAsync(() -> {
			try {
				return paymentController.getReceipts();
			} catch (HiveException | RuntimeException e) {
				throw new CompletionException(e);
			}
		});
	}

	@Override
	public CompletableFuture<String> getVersion() {
		return CompletableFuture.supplyAsync(() -> {
			try {
				return paymentController.getVersion();
			} catch (HiveException | RuntimeException e) {
				throw new CompletionException(e);
			}
		});
	}
}
