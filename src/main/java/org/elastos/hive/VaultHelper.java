package org.elastos.hive;

import org.elastos.hive.connection.ConnectionManager;
import org.elastos.hive.exception.HiveException;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;

import okhttp3.ResponseBody;
import retrofit2.Response;

class VaultHelper {

	private AuthHelper authHelper;
	private ConnectionManager connectionManager;

	public VaultHelper(AuthHelper authHelper) {
		this.authHelper = authHelper;
		this.connectionManager = authHelper.getConnectionManager();
	}

	public CompletableFuture<Boolean> useTrial() {
		return authHelper.checkValid()
				.thenCompose(result -> useTrialImp());
	}

	private CompletableFuture<Boolean> useTrialImp() {
		return CompletableFuture.supplyAsync(() -> {
			try {
				Response<ResponseBody> response = this.connectionManager.getVaultApi()
						.createFreeVault()
						.execute();
				authHelper.checkResponseWithRetry(response);
				return true;
			} catch (Exception e) {
				HiveException exception = new HiveException(e.getLocalizedMessage());
				throw new CompletionException(exception);
			}
		});
	}
}
