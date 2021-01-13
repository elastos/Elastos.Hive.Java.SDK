package org.elastos.hive;

import com.fasterxml.jackson.databind.JsonNode;

import org.elastos.hive.backup.State;
import org.elastos.hive.connection.ConnectionManager;
import org.elastos.hive.exception.HiveException;
import org.elastos.hive.exception.UnsupportStateTypeException;
import org.elastos.hive.utils.JsonUtil;
import org.elastos.hive.utils.ResponseHelper;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.concurrent.CompletionStage;
import java.util.function.Consumer;
import java.util.function.Function;

import okhttp3.MediaType;
import okhttp3.RequestBody;
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
		if (null == handler) {
			throw new IllegalArgumentException("backup authentication handler can not be null");
		}

		return authHelper.checkValid().thenComposeAsync(aVoid ->
				handler.authorization(authHelper.serviceDid(), authHelper.endPoint())
						.thenApplyAsync(credential -> {
							try {
								return saveImpl(credential);
							} catch (HiveException e) {
								e.printStackTrace();
							}
							return false;
						}));
	}

	private boolean saveImpl(String credential) throws HiveException {
		try {
			Map<String, Object> map = new HashMap<>();
			map.put("backup_credential", credential);
			String json = JsonUtil.serialize(map);

			Response response = this.connectionManager.getBackApi()
					.saveToNode(RequestBody.create(MediaType.parse("Content-Type, application/json"), json))
					.execute();

			authHelper.checkResponseWithRetry(response);
			return true;
		} catch (Exception e) {
			throw new HiveException(e.getLocalizedMessage());
		}
	}

	@Override
	public CompletableFuture<Boolean> restore(BackupAuthenticationHandler handler) {
		if (null == handler) {
			throw new IllegalArgumentException("backup authentication handler can not be null");
		}

		return authHelper.checkValid().thenComposeAsync(aVoid ->
				handler.authorization(authHelper.serviceDid(), authHelper.endPoint())
						.thenApplyAsync(credential -> {
							try {
								return restoreImpl(credential);
							} catch (HiveException e) {
								e.printStackTrace();
							}
							return false;
						}));
	}

	private boolean restoreImpl(String credential) throws HiveException {
		try {
			Map<String, Object> map = new HashMap<>();
			map.put("backup_credential", credential);
			String json = JsonUtil.serialize(map);

			Response response = this.connectionManager.getBackApi()
					.restoreFromNode(RequestBody.create(MediaType.parse("Content-Type, application/json"), json))
					.execute();

			authHelper.checkResponseWithRetry(response);
			return true;
		} catch (Exception e) {
			throw new HiveException(e.getLocalizedMessage());
		}
	}

	@Override
	public CompletableFuture<Boolean> active() {
		return null;
	}
}
