package org.elastos.hive;

import com.fasterxml.jackson.databind.JsonNode;

import org.elastos.hive.backup.State;
import org.elastos.hive.connection.ConnectionManager;
import org.elastos.hive.exception.HiveException;
import org.elastos.hive.exception.UnsupportStateTypeException;
import org.elastos.hive.utils.ResponseHelper;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;

import retrofit2.Response;

class BackupImpl implements Backup {

	private AuthHelper authHelper;
	private ConnectionManager connectionManager;

	BackupImpl(AuthHelper authHelper) {
		this.authHelper = authHelper;
		this.connectionManager = authHelper.getConnectionManager();
	}

	@Override
	public CompletableFuture<State> getState() {
		return authHelper.checkValid().thenApplyAsync(aVoid -> {
			try {
				return getStateImp();
			} catch (HiveException e) {
				throw new CompletionException(e);
			}
		});
	}

	private State getStateImp() throws HiveException {
		try {
			Response response = this.connectionManager.getBackApi()
					.getState()
					.execute();

			authHelper.checkResponseWithRetry(response);

			JsonNode ret = ResponseHelper.getValue(response, JsonNode.class);
			String type = ret.get("hive_backup_state").asText();
			switch (type) {
				case "stop":
					return State.STOP;
				case "backup":
					return State.BACKUP;
				case "restore":
					return State.RESTORE;
			}
			throw new UnsupportStateTypeException();
		} catch (Exception e) {
			throw new HiveException(e.getLocalizedMessage());
		}
	}

	@Override
	public CompletableFuture<Boolean> save(BackupAuthenticationHandler handler) {
		if(null == handler) {
			throw new IllegalArgumentException("backup authentication handler can not be null");
		}

//		handler.authorization();

		return null;
	}

	@Override
	public CompletableFuture<Boolean> restore(String target) {
		return null;
	}

	@Override
	public CompletableFuture<Boolean> active() {
		return null;
	}
}
