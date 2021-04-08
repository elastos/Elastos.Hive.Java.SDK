package org.elastos.hive;

import org.elastos.hive.connection.ConnectionManager;
import org.elastos.hive.exception.HiveException;
import org.elastos.hive.network.request.EmptyRequestBody;
import org.elastos.hive.network.response.HiveResponseBody;
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

public class BackupSubscription extends ServiceEndpoint implements SubscriptionService<BackupSubscription.BackupInfo>, PaymentService, HttpExceptionHandler {
	private SubscriptionServiceRender subscriptionService;
	private PaymentServiceRender paymentService;

	public BackupSubscription(AppContext context, String userDid, String providerAddress) throws HiveException {
		super(context, userDid, providerAddress);
		this.paymentService = new PaymentServiceRender(context);
		this.subscriptionService = new SubscriptionServiceRender(context);
	}

	@Override
	public CompletableFuture<BackupInfo> subscribe(String pricingPlan) {
		return CompletableFuture.runAsync(() -> {
			try {
				this.subscriptionService.subscribeBackup();
			} catch (Exception e) {
				throw new CompletionException(convertException(e));
			}
		}).thenApplyAsync(result -> {
			try {
				return getBackupInfoByResponseBody(this.subscriptionService.getBackupVaultInfo());
			} catch (Exception e) {
				throw new CompletionException(convertException(e));
			}
		});
	}

	private BackupInfo getBackupInfoByResponseBody(VaultInfoResponseBody body) {
		return new BackupInfo().setDid(body.getDid())
				.setMaxStorage(body.getMaxStorage())
				.setFileUseStorage(body.getFileUseStorage())
				.setDbUseStorage(body.getDbUseStorage())
				.setModifyTime(body.getModifyTimeStr())
				.setStartTime(body.getStartTimeStr())
				.setEndTime(body.getEndTimeStr())
				.setPricingUsing(body.getPricingUsing())
				.setIsExisting(body.isExisting());
	}

	@Override
	public CompletableFuture<Void> unsubscribe() {
		throw new UnsupportedOperationException();
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
		return CompletableFuture.supplyAsync(()-> {
			try {
				return getBackupInfoByResponseBody(this.subscriptionService.getBackupVaultInfo());
			} catch (Exception e) {
				throw new CompletionException(convertException(e));
			}
		});
	}

	public class BackupInfo {
		private String did;
		private long maxStorage;
		private long fileUseStorage;
		private long dbUseStorage;
		private String modifyTime;
		private String startTime;
		private String endTime;
		private String pricingUsing;
		private boolean isExisting;

		public String getDid() {
			return did;
		}

		public BackupInfo setDid(String did) {
			this.did = did;
			return this;
		}

		public long getMaxStorage() {
			return maxStorage;
		}

		public BackupInfo setMaxStorage(long maxStorage) {
			this.maxStorage = maxStorage;
			return this;
		}

		public long getFileUseStorage() {
			return fileUseStorage;
		}

		public BackupInfo setFileUseStorage(long fileUseStorage) {
			this.fileUseStorage = fileUseStorage;
			return this;
		}

		public long getDbUseStorage() {
			return dbUseStorage;
		}

		public BackupInfo setDbUseStorage(long dbUseStorage) {
			this.dbUseStorage = dbUseStorage;
			return this;
		}

		public String getModifyTime() {
			return modifyTime;
		}

		public BackupInfo setModifyTime(String modifyTime) {
			this.modifyTime = modifyTime;
			return this;
		}

		public String getStartTime() {
			return startTime;
		}

		public BackupInfo setStartTime(String startTime) {
			this.startTime = startTime;
			return this;
		}

		public String getEndTime() {
			return endTime;
		}

		public BackupInfo setEndTime(String endTime) {
			this.endTime = endTime;
			return this;
		}

		public String getPricingUsing() {
			return pricingUsing;
		}

		public BackupInfo setPricingUsing(String pricingUsing) {
			this.pricingUsing = pricingUsing;
			return this;
		}

		public boolean getIsExisting() {
			return isExisting;
		}

		public BackupInfo setIsExisting(boolean isExisting) {
			this.isExisting = isExisting;
			return this;
		}
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
	public CompletableFuture<Receipt> payOrder(String orderId, List<String> transIds) {
		return CompletableFuture.supplyAsync(()-> {
			try {
				paymentService.payOrder(orderId, transIds);
				//TODO:
				return null;
			} catch (Exception e) {
				throw new CompletionException(convertException(e));
			}
		});
	}

	@Override
	public CompletableFuture<Receipt> getReceipt(String receiptId) {
		throw new UnsupportedOperationException();
	}

}
