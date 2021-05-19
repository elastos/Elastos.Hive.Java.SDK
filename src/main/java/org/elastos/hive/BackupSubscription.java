package org.elastos.hive;

import org.elastos.hive.exception.HiveException;
import org.elastos.hive.exception.UnsupportedMethodException;
import org.elastos.hive.vault.scripting.VaultInfoResponseBody;
import org.elastos.hive.vault.payment.Order;
import org.elastos.hive.vault.payment.PricingPlan;
import org.elastos.hive.vault.payment.Receipt;
import org.elastos.hive.service.PaymentService;
import org.elastos.hive.service.SubscriptionService;
import org.elastos.hive.vault.ExceptionConvertor;
import org.elastos.hive.vault.PaymentServiceRender;
import org.elastos.hive.vault.SubscriptionServiceRender;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;

public class BackupSubscription extends ServiceEndpoint
	implements SubscriptionService<Backup.PropertySet>, PaymentService, ExceptionConvertor {

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
				return subscriptionService.getBackupPlanList();
			} catch (Exception e) {
				throw new CompletionException(toHiveException(e));
			}
		});
	}

	@Override
	public CompletableFuture<PricingPlan> getPricingPlan(String planName) {
		return CompletableFuture.supplyAsync(()-> {
			try {
				return subscriptionService.getBackupPlan(planName);
			} catch (Exception e) {
				throw new CompletionException(toHiveException(e));
			}
		});
	}

	public CompletableFuture<Backup.PropertySet> subscribe() {
		return this.subscribe(null);
	}

	@Override
	public CompletableFuture<Backup.PropertySet> subscribe(String reserved) {
		return CompletableFuture.supplyAsync(() -> {
			try {
				this.subscriptionService.subscribeBackup();
				return getPropertySet();
			} catch (Exception e) {
				throw new CompletionException(toHiveException(e));
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
				return getPropertySet();
			} catch (Exception e) {
				throw new CompletionException(toHiveException(e));
			}
		});
	}

	private Backup.PropertySet getPropertySet() throws IOException {
		VaultInfoResponseBody body = this.subscriptionService.getBackupVaultInfo();
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
				return paymentService.getOrderInfo(paymentService.createBackupOrder(planName));
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
				paymentService.payOrder(orderId, Collections.singletonList(transactionId));
				//TODO:
				return null;
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
