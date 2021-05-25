package org.elastos.hive;

import org.elastos.hive.Vault.PropertySet;
import org.elastos.hive.exception.HiveException;
import org.elastos.hive.exception.UnsupportedMethodException;
import org.elastos.hive.subscription.payment.Order;
import org.elastos.hive.subscription.payment.PaymentController;
import org.elastos.hive.subscription.payment.PricingPlan;
import org.elastos.hive.subscription.payment.Receipt;
import org.elastos.hive.service.PaymentService;
import org.elastos.hive.service.SubscriptionService;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;

import org.elastos.hive.vault.ExceptionConvertor;
import org.elastos.hive.subscription.SubscriptionController;
import org.elastos.hive.subscription.VaultInfoResponseBody;

public class VaultSubscription extends ServiceEndpoint
	implements SubscriptionService<Vault.PropertySet>, PaymentService, ExceptionConvertor {

	private SubscriptionController subscriptionController;
	private PaymentController paymentController;

	public VaultSubscription(AppContext context, String providerAddress) throws HiveException {
		super(context, providerAddress);
		subscriptionController = new SubscriptionController(this);
		paymentController = new PaymentController(this);
	}

	@Override
	public CompletableFuture<List<PricingPlan>> getPricingPlanList() {
		return CompletableFuture.supplyAsync(()-> {
			try {
				return paymentController.getPricingPlanList();
			} catch (HiveException e) {
				throw new CompletionException(e);
			} catch (RuntimeException e) {
				throw new CompletionException(e);
			}
		});
	}

	@Override
	public CompletableFuture<PricingPlan> getPricingPlan(String planName) {
		return CompletableFuture.supplyAsync(()-> {
			try {
				return paymentController.getPricingPlan(planName);
			} catch (HiveException e) {
				throw new CompletionException(e);
			} catch (RuntimeException e) {
				throw new CompletionException(e);
			}
		});
	}

	public CompletableFuture<Vault.PropertySet> subscribe() {
		return this.subscribe(null);
	}

	@Override
	public CompletableFuture<Vault.PropertySet> subscribe(String reserved) {
		return CompletableFuture.supplyAsync(()-> {
			try {
				// TODO:
				subscriptionController.subscribe();
				return new PropertySet();
			} catch (HiveException e) {
				throw new CompletionException(e);
			} catch (RuntimeException e) {
				throw new CompletionException(e);
			}
		});
	}

	@Override
	public CompletableFuture<Void> unsubscribe() {
		return CompletableFuture.runAsync(()-> {
			try {
				subscriptionController.unsubscribe();
			} catch (HiveException e) {
				throw new CompletionException(e);
			} catch (RuntimeException e) {
				throw new CompletionException(e);
			}
		});
	}

	@Override
	public CompletableFuture<Void> activate() {
		return CompletableFuture.runAsync(()-> {
			try {
				subscriptionController.activate();
			} catch (HiveException e) {
				throw new CompletionException(e);
			} catch (RuntimeException e) {
				throw new CompletionException(e);
			}
		});
	}

	@Override
	public CompletableFuture<Void> deactivate() {
		return CompletableFuture.runAsync(()-> {
			try {
				subscriptionController.deactivate();
			} catch (HiveException e) {
				throw new CompletionException(e);
			} catch (RuntimeException e) {
				throw new CompletionException(e);
			}
		});
	}

	@Override
	public CompletableFuture<Vault.PropertySet> checkSubscription() {
		return CompletableFuture.supplyAsync(()-> {
			try {
				// TODO:
				return getPropertySet();
			} catch (HiveException e) {
				throw new CompletionException(e);
			} catch (RuntimeException e) {
				throw new CompletionException(e);
			}
		});
	}

	private Vault.PropertySet getPropertySet() throws HiveException {
		VaultInfoResponseBody body = subscriptionController.getVaultInfo();
		// TODO: serviceDid
		return new Vault.PropertySet()
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
				paymentController.payOrder(orderId,
						transactionId == null ? Collections.emptyList() : Collections.singletonList(transactionId));
				//TODO:
				return new Receipt();
			} catch (HiveException e) {
				throw new CompletionException(e);
			} catch (RuntimeException e) {
				throw new CompletionException(e);
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
