package org.elastos.hive;

import org.elastos.hive.exception.HiveException;
import org.elastos.hive.subscription.AppInfo;
import org.elastos.hive.subscription.VaultInfo;
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
 * The vault subscription is used for the vault management.
 *
 * <p>Subscribe the vault is the first step to use the service in the vault.</p>
 */
public class VaultSubscription extends ServiceEndpoint
	implements SubscriptionService<VaultInfo>, PaymentService {

	private SubscriptionController subscriptionController;
	private PaymentController paymentController;

	/**
	 * Create by the application context, and the address of the provider.
	 *
	 * @param context The application context.
	 * @param providerAddress The address of the provider.
	 * @throws HiveException See {@link HiveException}
	 */
	public VaultSubscription(AppContext context, String providerAddress) throws HiveException {
		super(context, providerAddress);
		subscriptionController = new SubscriptionController(this);
		paymentController = new PaymentController(this);
	}

	/**
	 * Create by the application context, and the address of the provider.
	 *
	 * @param context The application context.
	 * @throws HiveException See {@link HiveException}
	 */
	public VaultSubscription(AppContext context) throws HiveException {
		this(context, null);
	}

	@Override
	public CompletableFuture<List<PricingPlan>> getPricingPlanList() {
		return CompletableFuture.supplyAsync(()-> {
			try {
				return subscriptionController.getVaultPricingPlanList();
			} catch (HiveException | RuntimeException e) {
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
				return subscriptionController.getVaultPricingPlan(planName);
			} catch (HiveException | RuntimeException e) {
				throw new CompletionException(e);
			}
		});
	}

	@Override
	public CompletableFuture<VaultInfo> subscribe() {
		return CompletableFuture.supplyAsync(()-> {
			try {
				return subscriptionController.subscribeToVault();
			} catch (HiveException | RuntimeException e) {
				throw new CompletionException(e);
			}
		});
	}

	@Override
	public CompletableFuture<Void> activate() {
		return CompletableFuture.runAsync(()-> {
			try {
				subscriptionController.activateVault();
			} catch (HiveException | RuntimeException e) {
				throw new CompletionException(e);
			}
		});
	}

	@Override
	public CompletableFuture<Void> deactivate() {
		return CompletableFuture.runAsync(()-> {
			try {
				subscriptionController.deactivateVault();
			} catch (HiveException | RuntimeException e) {
				throw new CompletionException(e);
			}
		});
	}

	@Override
	public CompletableFuture<Void> unsubscribe() {
		return CompletableFuture.runAsync(()-> {
			try {
				subscriptionController.unsubscribeVault();
			} catch (HiveException | RuntimeException e) {
				throw new CompletionException(e);
			}
		});
	}

	@Override
	public CompletableFuture<VaultInfo> checkSubscription() {
		return CompletableFuture.supplyAsync(()-> {
			try {
				return subscriptionController.getVaultInfo();
			} catch (HiveException | RuntimeException e) {
				throw new CompletionException(e);
			}
		});
	}

	@Override
	public CompletableFuture<List<AppInfo>> getAppStats() {
		return CompletableFuture.supplyAsync(()-> {
			try {
				return subscriptionController.getAppStats();
			} catch (HiveException | RuntimeException e) {
				throw new CompletionException(e);
			}
		});
	}

	@Override
	public CompletableFuture<Order> placeOrder(String planName) {
		return CompletableFuture.supplyAsync(() -> {
			try {
				return paymentController.placeOrder("vault", planName);
			} catch (HiveException | RuntimeException e) {
				throw new CompletionException(e);
			}
		});
	}

	@Override
	public CompletableFuture<Order> getOrder(int orderId) {
		return CompletableFuture.supplyAsync(() -> {
			try {
				return paymentController.getVaultOrder(orderId);
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
				return paymentController.getOrders("vault");
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
