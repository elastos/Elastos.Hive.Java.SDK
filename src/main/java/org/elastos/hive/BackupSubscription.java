package org.elastos.hive;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import org.elastos.hive.connection.ConnectionManager;
import org.elastos.hive.exception.BackupAlreadyExistException;
import org.elastos.hive.exception.HiveException;
import org.elastos.hive.payment.Order;
import org.elastos.hive.payment.PricingPlan;
import org.elastos.hive.payment.Receipt;
import org.elastos.hive.service.PaymentService;
import org.elastos.hive.service.SubscriptionService;
import org.elastos.hive.subscribe.CreateServiceResult;

import retrofit2.Response;

public class BackupSubscription {
	private SubscriptionRender render;

	public BackupSubscription(AppContext context, String userDid, String providerAddress) throws HiveException {
		render = new SubscriptionRender(context, userDid, providerAddress);
	}

	public CompletableFuture<BackupInfo> subscribe() {
		return render.subscribe(null, BackupInfo.class);
	}

	public CompletableFuture<BackupInfo> subscribe(String pricingPlan) {
		return render.subscribe(pricingPlan, BackupInfo.class);
	}

	public CompletableFuture<Void> unsubscribe() {
		return render.unsubscribe();
	}

	public CompletableFuture<Void> activate() {
		return render.activate();
	}

	public CompletableFuture<Void> deactivate() {
		return render.deactivate();
	}

	public CompletableFuture<BackupInfo> checkSubscription() {
		return render.checkSubscription();
	}

	public class BackupInfo {
		// TODO;
	}

	class SubscriptionRender extends ServiceEndpoint implements SubscriptionService, PaymentService {
		private AppContext appContext;
		private ConnectionManager connectionManager;

		protected SubscriptionRender(AppContext context, String userDid, String providerAddress)
				throws HiveException {
			super(context, providerAddress, userDid);
			this.appContext = context;
			this.connectionManager = appContext.getConnectionManager();
		}

		@Override
		public <T> CompletableFuture<T> subscribe(String pricingPlan, Class<T> type) {
			return CompletableFuture.supplyAsync(this::subscribeImpl);
		}

		@Override
		public CompletableFuture<Void> unsubscribe() {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public CompletableFuture<Void> activate() {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public CompletableFuture<Void> deactivate() {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public <T> CompletableFuture<T> checkSubscription() {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public CompletableFuture<List<PricingPlan>> getPricingPlanList() {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public CompletableFuture<PricingPlan> getPricingPlan(String planName) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public CompletableFuture<Order> placeOrder(String planName) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public CompletableFuture<Order> getOrder(String orderId) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public CompletableFuture<Receipt> payOrder(String orderId, String transId) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public CompletableFuture<Receipt> getReceipt(String receiptId) {
			// TODO Auto-generated method stub
			return null;
		}

		private <T> T subscribeImpl() {
			BackupInfo backupInfo = new BackupInfo();
			Response<CreateServiceResult> response;
			try {
				response = connectionManager.getSubscriptionApi().createBackupVault().execute();
				if (response.body().existing()) {
					throw new BackupAlreadyExistException("The backup service already exists");
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
			return (T) backupInfo;
		}
	}
}
