package org.elastos.hive;

import org.elastos.hive.exception.HiveException;
import org.elastos.hive.exception.UnsupportedMethodException;
import org.elastos.hive.network.response.VaultInfoResponseBody;
import org.elastos.hive.vault.payment.Order;
import org.elastos.hive.vault.payment.PricingPlan;
import org.elastos.hive.vault.payment.Receipt;
import org.elastos.hive.service.PaymentService;
import org.elastos.hive.service.SubscriptionService;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;

import org.elastos.hive.vault.ExceptionConvertor;
import org.elastos.hive.vault.PaymentServiceRender;
import org.elastos.hive.vault.SubscriptionServiceRender;

public class VaultSubscription extends ServiceEndpoint
	implements SubscriptionService<Vault.PropertySet>, PaymentService, ExceptionConvertor {

	private SubscriptionServiceRender subscriptionService;
	private PaymentServiceRender paymentService;

	public VaultSubscription(AppContext context, String providerAddress) throws HiveException {
		super(context, providerAddress);
		this.paymentService = new PaymentServiceRender(this);
		this.subscriptionService = new SubscriptionServiceRender(this);
	}

	@Override
	public CompletableFuture<List<PricingPlan>> getPricingPlanList() {
		return CompletableFuture.supplyAsync(()-> {
			try {
				return subscriptionService.getPricingPlanList();
			} catch (Exception e) {
				throw new CompletionException(toHiveException(e));
			}
		});
	}

	@Override
	public CompletableFuture<PricingPlan> getPricingPlan(String planName) {
		return CompletableFuture.supplyAsync(()-> {
			try {
				return subscriptionService.getPricingPlan(planName);
			} catch (Exception e) {
				throw new CompletionException(toHiveException(e));
			}
		});
	}

	public CompletableFuture<Vault.PropertySet> subscribe() {
		return this.subscribe(null);
	}

	@Override
	public CompletableFuture<Vault.PropertySet> subscribe(String reserved) {
		return CompletableFuture.supplyAsync(() -> {
			try {
				this.subscriptionService.subscribe();
				return getPropertySet();
			} catch (Exception e) {
				throw new CompletionException(toHiveException(e));
			}
		});
	}

	@Override
	public CompletableFuture<Void> unsubscribe() {
		return CompletableFuture.runAsync(() -> {
			try {
				this.subscriptionService.unsubscribe();
			} catch (Exception e) {
				throw new CompletionException(toHiveException(e));
			}
		});
	}

	@Override
	public CompletableFuture<Void> activate() {
		return CompletableFuture.runAsync(() -> {
			try {
				this.subscriptionService.activate();
			} catch (Exception e) {
				throw new CompletionException(toHiveException(e));
			}
		});
	}

	@Override
	public CompletableFuture<Void> deactivate() {
		return CompletableFuture.runAsync(() -> {
			try {
				this.subscriptionService.deactivate();
			} catch (Exception e) {
				throw new CompletionException(toHiveException(e));
			}
		});
	}

	@Override
	public CompletableFuture<Vault.PropertySet> checkSubscription() {
		return CompletableFuture.supplyAsync(() -> {
			try {
				return getPropertySet();
			} catch (Exception e) {
				throw new CompletionException(toHiveException(e));
			}
		});
	}

	private Vault.PropertySet getPropertySet() throws IOException {
		VaultInfoResponseBody body = this.subscriptionService.getVaultInfo();
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
				return paymentService.getOrderInfo(paymentService.createPricingOrder(planName));
			} catch (Exception e) {
				throw new CompletionException(toHiveException(e));
			}
		});
	}

	@Override
	public CompletableFuture<Order> getOrder(String orderId) {
		return CompletableFuture.supplyAsync(()-> {
			try {
				return paymentService.getOrderInfo(orderId);
			} catch (Exception e) {
				throw new CompletionException(toHiveException(e));
			}
		});
	}

	@Override
	public CompletableFuture<Receipt> payOrder(String orderId, String transactionId) {
		return CompletableFuture.supplyAsync(()-> {
			try {
				paymentService.payOrder(orderId, transactionId == null ? Collections.emptyList() : Collections.singletonList(transactionId));
				//TODO:
				return new Receipt();
			} catch (Exception e) {
				throw new CompletionException(toHiveException(e));
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
