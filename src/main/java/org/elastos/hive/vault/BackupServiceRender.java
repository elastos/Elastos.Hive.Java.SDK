package org.elastos.hive.vault;

import java.io.IOException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;

import org.elastos.hive.Vault;
import org.elastos.hive.auth.BackupRemoteResolver;
import org.elastos.hive.auth.LocalResolver;
import org.elastos.hive.auth.TokenResolver;
import org.elastos.hive.connection.ConnectionManager;
import org.elastos.hive.exception.HiveException;
import org.elastos.hive.network.request.BackupSaveRequestBody;
import org.elastos.hive.network.response.BackupStateResponseBody;
import org.elastos.hive.network.response.HiveResponseBody;
import org.elastos.hive.service.BackupContext;
import org.elastos.hive.service.BackupService;
import retrofit2.Response;

class BackupServiceRender implements BackupService {
	private Vault vault;
	private BackupContext backupContext;
	private ConnectionManager connectionManager;
	private TokenResolver tokenResolver;

	public BackupServiceRender(Vault vault) {
		this.connectionManager = vault.getAppContext().getConnectionManager();
	}

	@Override
	public CompletableFuture<Void> setupContext(BackupContext backupContext) {
		this.backupContext = backupContext;
		this.tokenResolver = new LocalResolver(
				vault.getAppContext().getUserDid(),
				vault.getAppContext().getProviderAddress(),
				"backup_credential",
				vault.getAppContext().getAppContextProvider().getLocalDataDir());
		this.tokenResolver.setNextResolver(new BackupRemoteResolver(
				vault.getAppContext(),
				backupContext.getParameter("targetDid"),
				backupContext.getParameter("targetHost")));
		return null;
	}

	@Override
	public CompletableFuture<Void> startBackup() {
		return CompletableFuture.runAsync(() -> {
			try {
				Response<HiveResponseBody> response = connectionManager.getBackupApi()
						.saveToNode(new BackupSaveRequestBody(tokenResolver.getToken().getAccessToken()))
						.execute();
				HiveResponseBody.validateBody(response);
			} catch (HiveException | IOException e) {
				throw new CompletionException(new HiveException(e.getMessage()));
			}
		});
	}

	@Override
	public CompletableFuture<Void> stopBackup() {
		throw new UnsupportedOperationException();
	}

	@Override
	public CompletableFuture<Void> restoreFrom() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public CompletableFuture<Void> stopRestore() {
		throw new UnsupportedOperationException();
	}

	@Override
	public CompletableFuture<BackupResult> checkResult() {
		return CompletableFuture.supplyAsync(() -> {
			try {
				Response<BackupStateResponseBody> response = connectionManager.getBackupApi()
						.getState()
						.execute();
				return HiveResponseBody.validateBody(response).getStatusResult();
			} catch (HiveException | IOException e) {
				throw new CompletionException(new HiveException(e.getMessage()));
			}
		});
	}
}
