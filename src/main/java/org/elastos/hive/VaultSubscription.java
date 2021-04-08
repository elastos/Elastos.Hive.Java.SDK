package org.elastos.hive;

import org.elastos.hive.exception.HiveException;
import org.elastos.hive.network.response.VaultInfoResponseBody;
import org.elastos.hive.payment.Order;
import org.elastos.hive.payment.PricingPlan;
import org.elastos.hive.payment.Receipt;
import org.elastos.hive.service.PaymentService;
import org.elastos.hive.service.SubscriptionService;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;

import org.elastos.hive.vault.HttpExceptionHandler;
import org.elastos.hive.vault.PaymentServiceRender;
import org.elastos.hive.vault.SubscriptionServiceRender;

public class VaultSubscription extends ServiceEndpoint implements SubscriptionService<VaultSubscription.VaultInfo>, PaymentService, HttpExceptionHandler {
	private AppContext context;
	private String userDid;
	private SubscriptionServiceRender subscriptionService;
	private PaymentServiceRender paymentService;

	public VaultSubscription(AppContext context, String userDid, String providerAddress) throws HiveException {
		super(context, userDid, providerAddress);
		this.context = context;
		this.userDid = userDid;
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
			} catch (Exception e) {
				throw new CompletionException(convertException(e));
			}
		});
	}

	@Override
	public CompletableFuture<Void> unsubscribe() {
		return CompletableFuture.runAsync(() -> {
			try {
				this.subscriptionService.unsubscribe();
			} catch (Exception e) {
				throw new CompletionException(convertException(e));
			}
		});
	}

	@Override
	public CompletableFuture<Void> activate() {
		return CompletableFuture.runAsync(() -> {
			try {
				this.subscriptionService.activate();
			} catch (Exception e) {
				throw new CompletionException(convertException(e));
			}
		});
	}

	@Override
	public CompletableFuture<Void> deactivate() {
		return CompletableFuture.runAsync(() -> {
			try {
				this.subscriptionService.deactivate();
			} catch (Exception e) {
				throw new CompletionException(convertException(e));
			}
		});
	}

	@Override
	public CompletableFuture<VaultInfo> checkSubscription() {
		return CompletableFuture.supplyAsync(() -> {
			try {
				VaultInfoResponseBody body = this.subscriptionService.getVaultInfo();
				return new VaultInfo(this.userDid, null, body.getDid())
						.setProvider(this.context.getProviderAddress())
						.setCreateTime(body.getStartTimeStr())
						.setModifyTime(body.getModifyTimeStr())
						.setMaxSpace(body.getMaxStorage())
						.setDbSpaceUsed(body.getDbUseStorage())
						.setFileSpaceUsed(body.getFileUseStorage())
						.setExisting(body.isExisting());
			} catch (Exception e) {
				throw new CompletionException(convertException(e));
			}
		});
	}

	@Override
	public CompletableFuture<List<PricingPlan>> getPricingPlanList() {
		return CompletableFuture.supplyAsync(()-> {
			try {
				return paymentService.getPricingPlanList();
			} catch (Exception e) {
				throw new CompletionException(convertException(e));
			}
		});
	}

	@Override
	public CompletableFuture<PricingPlan> getPricingPlan(String planName) {
		return CompletableFuture.supplyAsync(()-> {
			try {
				return paymentService.getPricingPlan(planName);
			} catch (Exception e) {
				throw new CompletionException(convertException(e));
			}
		});
	}

	@Override
	public CompletableFuture<Order> placeOrder(String planName) {
		return CompletableFuture.supplyAsync(()-> {
			try {
				return paymentService.getOrderInfo(paymentService.createPricingOrder(planName));
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
				return new Receipt();
			} catch (Exception e) {
				throw new CompletionException(convertException(e));
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

		public String getMyDid() {
			return myDid;
		}

		public VaultInfo setMyDid(String myDid) {
			this.myDid = myDid;
			return this;
		}

		public String getAppInstanceDid() {
			return appInstanceDid;
		}

		public VaultInfo setAppInstanceDid(String appInstanceDid) {
			this.appInstanceDid = appInstanceDid;
			return this;
		}

		public String getAppId() {
			return appId;
		}

		public VaultInfo setAppId(String appId) {
			this.appId = appId;
			return this;
		}

		public String getProvider() {
			return provider;
		}

		public VaultInfo setProvider(String provider) {
			this.provider = provider;
			return this;
		}

		public String getServiceDid() {
			return serviceDid;
		}

		public VaultInfo setServiceDid(String serviceDid) {
			this.serviceDid = serviceDid;
			return this;
		}

		public String getPricingUsing() {
			return pricingUsing;
		}

		public VaultInfo setPricingUsing(String pricingUsing) {
			this.pricingUsing = pricingUsing;
			return this;
		}

		public String getCreateTime() {
			return createTime;
		}

		public VaultInfo setCreateTime(String createTime) {
			this.createTime = createTime;
			return this;
		}

		public String getModifyTime() {
			return modifyTime;
		}

		public VaultInfo setModifyTime(String modifyTime) {
			this.modifyTime = modifyTime;
			return this;
		}

		public long getMaxSpace() {
			return maxSpace;
		}

		public VaultInfo setMaxSpace(long maxSpace) {
			this.maxSpace = maxSpace;
			return this;
		}

		public long getDbSpaceUsed() {
			return dbSpaceUsed;
		}

		public VaultInfo setDbSpaceUsed(long dbSpaceUsed) {
			this.dbSpaceUsed = dbSpaceUsed;
			return this;
		}

		public long getFileSpaceUsed() {
			return fileSpaceUsed;
		}

		public VaultInfo setFileSpaceUsed(long fileSpaceUsed) {
			this.fileSpaceUsed = fileSpaceUsed;
			return this;
		}

		public boolean isExisting() {
			return existing;
		}

		public VaultInfo setExisting(boolean existing) {
			this.existing = existing;
			return this;
		}
	}
}
