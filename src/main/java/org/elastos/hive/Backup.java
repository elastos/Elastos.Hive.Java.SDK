package org.elastos.hive;

import com.fasterxml.jackson.databind.JsonNode;

import org.elastos.did.VerifiableCredential;
import org.elastos.did.exception.DIDBackendException;
import org.elastos.did.exception.MalformedCredentialException;
import org.elastos.hive.backup.State;
import org.elastos.hive.connection.ConnectionManager;
import org.elastos.hive.exception.HiveException;
import org.elastos.hive.exception.UnsupportStateTypeException;
import org.elastos.hive.utils.JsonUtil;
import org.elastos.hive.utils.ResponseHelper;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import retrofit2.Response;

public class Backup {

	private AuthHelper authHelper;
	private ConnectionManager connectionManager;
	private String targetDid;
	private String targetHost;
	private String type;

	Backup(AuthHelper authHelper) {
		this.authHelper = authHelper;
		this.connectionManager = authHelper.getConnectionManager();
	}

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

	private static final String CREDENTIAL_KEY = "credential_key";

	private String restoreCredential() {
		Persistent persistent = new BackupPersistentImpl(this.targetHost, this.targetDid, this.type, this.authHelper.storePath());
		try {
			JSONObject jsonObject = persistent.parseFrom();
			if (!jsonObject.has(CREDENTIAL_KEY)) return null;

			return jsonObject.getString(CREDENTIAL_KEY);
		} catch (HiveException e) {
			e.printStackTrace();
		}
		return null;
	}

	private void storeCredential(String credential) {
		Persistent persistent = new BackupPersistentImpl(this.targetHost, this.targetDid, this.type, this.authHelper.storePath());
		JSONObject jsonObject = new JSONObject();
		jsonObject.put(CREDENTIAL_KEY, credential);
		try {
			persistent.upateContent(jsonObject);
		} catch (HiveException e) {
			e.printStackTrace();
		}
	}

	private boolean checkExpired(String cacheCredential) {
		VerifiableCredential vc = null;
		try {
			vc = VerifiableCredential.fromJson(cacheCredential);
		} catch (MalformedCredentialException e) {
			e.printStackTrace();
		}

		if(null != vc) {
			try {
				return vc.isExpired();
			} catch (DIDBackendException e) {
				e.printStackTrace();
			}
		}
		return true;
	}

	private CompletableFuture<String> getCredential(BackupAuthenticationHandler handler, String type) {
		String exTargetDid = handler.getTargetDid();
		String exTargetHost = handler.getTargetHost();

		if (null == exTargetDid) {
			throw new IllegalArgumentException("target did can not be null");
		}

		if (null == exTargetHost) {
			throw new IllegalArgumentException("target host can not be null");
		}

		if (null == type) {
			new HiveException("Hive internal exception: backup cache type is null").printStackTrace();
		}

		setTargetDid(exTargetDid);
		setTargetHost(exTargetHost);
		setType(type);
		String cacheCredential = restoreCredential();
		if (null != cacheCredential && !checkExpired(cacheCredential)) {
			return CompletableFuture.supplyAsync(() -> cacheCredential);
		}
		return handler.getAuthorization(authHelper.serviceDid());
	}

	public CompletableFuture<Boolean> save(BackupAuthenticationHandler handler) {
		if (null == handler) {
			throw new IllegalArgumentException("backup authentication handler can not be null");
		}

		return authHelper.checkValid().thenComposeAsync(aVoid ->
				getCredential(handler, "store")).thenApplyAsync(credential -> {
			try {
				saveImpl(credential);
				return credential;
			} catch (HiveException e) {
				e.printStackTrace();
			}
			return null;
		}).handleAsync((credential, throwable) -> {
			if (null != credential) storeCredential(credential);
			return (null != credential && null == throwable);
		});
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


	public CompletableFuture<Boolean> restore(BackupAuthenticationHandler handler) {
		if (null == handler) {
			throw new IllegalArgumentException("backup authentication handler can not be null");
		}

		if (null == handler) {
			throw new IllegalArgumentException("backup authentication handler can not be null");
		}

		return authHelper.checkValid().thenComposeAsync(aVoid ->
				getCredential(handler, "restore")).thenApplyAsync(credential -> {
			try {
				restoreImpl(credential);
				return credential;
			} catch (HiveException e) {
				e.printStackTrace();
			}
			return null;
		}).handleAsync((credential, throwable) -> {
			if (null != credential) storeCredential(credential);
			return (null != credential && null == throwable);
		});
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

	public CompletableFuture<Boolean> active() {
		return authHelper.checkValid().thenApplyAsync(aVoid -> {
			try {
				return activeImpl();
			} catch (HiveException e) {
				throw new CompletionException(e);
			}
		});
	}

	private boolean activeImpl() throws HiveException {
		try {
			Response response = this.connectionManager.getBackApi()
					.activeToVault()
					.execute();

			authHelper.checkResponseWithRetry(response);
			return true;
		} catch (Exception e) {
			throw new HiveException(e.getLocalizedMessage());
		}
	}

	private void setTargetDid(String targetDid) {
		this.targetDid = targetDid;
	}

	private void setTargetHost(String targetHost) {
		this.targetHost = targetHost;
	}

	private void setType(String type) {
		this.type = type;
	}
}
