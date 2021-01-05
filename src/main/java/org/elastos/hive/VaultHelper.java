package org.elastos.hive;

import com.fasterxml.jackson.databind.JsonNode;

import org.elastos.hive.connection.ConnectionManager;
import org.elastos.hive.exception.HiveException;
import org.elastos.hive.utils.ResponseHelper;

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

	public CompletableFuture<Boolean> requestToCreateVault() {
		return authHelper.checkValid().thenApplyAsync(aVoid -> {
			try {
				return requestToCreateVaultImpl();
			} catch (HiveException e) {
				throw new CompletionException(e);
			}
		});

	}

	private boolean requestToCreateVaultImpl() throws HiveException {
		try {
			Response<ResponseBody> response = this.connectionManager.getVaultApi()
					.createFreeVault()
					.execute();
			authHelper.checkResponseWithRetry(response);
			return true;
		} catch (Exception e) {
			throw new HiveException(e.getLocalizedMessage());
		}
	}

	public CompletableFuture<Boolean> vaultExist() {
		return authHelper.checkValid().thenApplyAsync(aVoid -> {
			try {
				return vaultExistImpl();
			} catch (HiveException e) {
				throw new CompletionException(e);
			}
		});
	}

	private boolean vaultExistImpl() throws HiveException {
		try {
			Response response = this.connectionManager.getPaymentApi()
					.getServiceInfo()
					.execute();
			int code = response.code();
			code = 404;
			if(404 == code) {
				return true;
			}
			authHelper.checkResponseWithRetry(response);
			JsonNode value = ResponseHelper.getValue(response, JsonNode.class);
			if(null == value) return false;
			JsonNode ret = value.get("vault_service_info");
			return (null!=ret);
		} catch (Exception e) {
			e.printStackTrace();
			throw new HiveException(e.getLocalizedMessage());
		}
	}

}
