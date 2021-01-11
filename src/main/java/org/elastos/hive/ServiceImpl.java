package org.elastos.hive;

import com.fasterxml.jackson.databind.JsonNode;

import org.elastos.hive.connection.ConnectionManager;
import org.elastos.hive.exception.HiveException;
import org.elastos.hive.payment.UsingPlan;
import org.elastos.hive.service.BackupUsingPlan;
import org.elastos.hive.utils.ResponseHelper;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;

import retrofit2.Response;

class ServiceImpl implements Service {


	private AuthHelper authHelper;
	private ConnectionManager connectionManager;

	ServiceImpl(AuthHelper authHelper) {
		this.authHelper = authHelper;
		this.connectionManager = authHelper.getConnectionManager();
	}

	@Override
	public CompletableFuture<Boolean> createVault() {
		return authHelper.checkValid().thenApplyAsync(aVoid -> {
			try {
				return createVaultImp();
			} catch (HiveException e) {
				throw new CompletionException(e);
			}
		});
	}

	private boolean createVaultImp() throws HiveException {
		try {
			Response response = this.connectionManager.getServiceApi()
					.createVault()
					.execute();

			authHelper.checkResponseWithRetry(response);
			return true;
		} catch (Exception e) {
			throw new HiveException(e.getLocalizedMessage());
		}
	}

	@Override
	public CompletableFuture<Boolean> removeVault() {
		return authHelper.checkValid().thenApplyAsync(aVoid -> {
			try {
				return removeVaultImp();
			} catch (HiveException e) {
				throw new CompletionException(e);
			}
		});
	}

	private boolean removeVaultImp() throws HiveException {
		try {
			Response response = this.connectionManager.getServiceApi()
					.removeVault()
					.execute();

			authHelper.checkResponseWithRetry(response);
			return true;
		} catch (Exception e) {
			throw new HiveException(e.getLocalizedMessage());
		}
	}

	@Override
	public CompletableFuture<Boolean> freezeVault() {
		return authHelper.checkValid().thenApplyAsync(aVoid -> {
			try {
				return freezeVaultImp();
			} catch (HiveException e) {
				throw new CompletionException(e);
			}
		});
	}

	private boolean freezeVaultImp() throws HiveException {
		try {
			Response response = this.connectionManager.getServiceApi()
					.freezeVault()
					.execute();

			authHelper.checkResponseWithRetry(response);
			return true;
		} catch (Exception e) {
			throw new HiveException(e.getLocalizedMessage());
		}
	}

	@Override
	public CompletableFuture<Boolean> unfreezeVault() {
		return authHelper.checkValid().thenApplyAsync(aVoid -> {
			try {
				return unfreezeVaultImp();
			} catch (HiveException e) {
				throw new CompletionException(e);
			}
		});
	}

	private boolean unfreezeVaultImp() throws HiveException {
		try {
			Response response = this.connectionManager.getServiceApi()
					.unfreezeVault()
					.execute();

			authHelper.checkResponseWithRetry(response);
			return true;
		} catch (Exception e) {
			throw new HiveException(e.getLocalizedMessage());
		}
	}

	@Override
	public CompletableFuture<UsingPlan> getVaultServiceInfo() {
		return authHelper.checkValid().thenApplyAsync(aVoid -> {
			try {
				return getVaultServiceInfoImpl();
			} catch (HiveException e) {
				throw new CompletionException(e);
			}
		});
	}

	private UsingPlan getVaultServiceInfoImpl() throws HiveException {
		try {
			Response response = this.connectionManager.getServiceApi()
					.getVaultServiceInfo()
					.execute();
			authHelper.checkResponseWithRetry(response);
			JsonNode value = ResponseHelper.getValue(response, JsonNode.class);
			if(null == value) return null;
			JsonNode ret = value.get("vault_service_info");
			if(null == ret) return null;
			return UsingPlan.deserialize(ret.toString());
		} catch (Exception e) {
			throw new HiveException(e.getLocalizedMessage());
		}
	}

	@Override
	public CompletableFuture<Boolean> createBackupVault() {
		return authHelper.checkValid().thenApplyAsync(aVoid -> {
			try {
				return createBackupVaultImp();
			} catch (HiveException e) {
				throw new CompletionException(e);
			}
		});
	}

	private boolean createBackupVaultImp() throws HiveException {
		try {
			Response response = this.connectionManager.getServiceApi()
					.createBackupVault()
					.execute();

			authHelper.checkResponseWithRetry(response);
			return true;
		} catch (Exception e) {
			throw new HiveException(e.getLocalizedMessage());
		}
	}

	@Override
	public CompletableFuture<BackupUsingPlan> getBackupServiceInfo() {
		return authHelper.checkValid().thenApplyAsync(aVoid -> {
			try {
				return getBackupServiceInfoImpl();
			} catch (HiveException e) {
				throw new CompletionException(e);
			}
		});
	}

	private BackupUsingPlan getBackupServiceInfoImpl() throws HiveException {
		try {
			Response response = this.connectionManager.getServiceApi()
					.getBackupVaultInfo()
					.execute();
			authHelper.checkResponseWithRetry(response);
			JsonNode value = ResponseHelper.getValue(response, JsonNode.class);
			if(null == value) return null;
			JsonNode ret = value.get("vault_service_info");
			if(null == ret) return null;
			return BackupUsingPlan.deserialize(ret.toString());
		} catch (Exception e) {
			throw new HiveException(e.getLocalizedMessage());
		}
	}
}
