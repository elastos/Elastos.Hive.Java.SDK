package org.elastos.hive;

import com.fasterxml.jackson.databind.JsonNode;

import org.elastos.did.VerifiableCredential;
import org.elastos.did.exception.DIDBackendException;
import org.elastos.did.exception.MalformedCredentialException;
import org.elastos.did.jwt.Claims;
import org.elastos.hive.backup.State;
import org.elastos.hive.connection.ConnectionManager;
import org.elastos.hive.exception.HiveException;
import org.elastos.hive.exception.UnsupportStateTypeException;
import org.elastos.hive.utils.JsonUtil;
import org.elastos.hive.utils.JwtUtil;
import org.elastos.hive.utils.ResponseHelper;
import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.concurrent.CompletionStage;
import java.util.function.Function;
import java.util.function.Supplier;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import retrofit2.Response;

public class BackupImpl implements Backup{

	private AuthHelper authHelper;
	private ConnectionManager connectionManager;
	private String targetDid;
	private String targetHost;
	private String type;

	BackupImpl(AuthHelper authHelper,String targetHost) {
		this.authHelper = authHelper;
		this.connectionManager = authHelper.getConnectionManager();
		this.targetHost = targetHost;
	}

	private CompletableFuture<String> getServiceDid() {
		if(null != targetDid) {
			return CompletableFuture.supplyAsync(() -> targetDid);
		}
		return CompletableFuture.supplyAsync(() -> {
			ApplicationContext context = authHelper.getContext();
			Map<String, Object> map = new HashMap<>();
			JSONObject docJsonObject = new JSONObject(context.getAppInstanceDocument().toString());
			map.put("document", docJsonObject);

			String json = new JSONObject(map).toString();
			Response response = null;
			try {
				response = connectionManager.getAuthApi()
						.signIn(RequestBody.create(MediaType.parse("Content-Type, application/json"), json))
						.execute();
				JsonNode ret = ResponseHelper.getValue(response, JsonNode.class).get("challenge");
				Claims claims = JwtUtil.getBody(ret.textValue());
				return claims.getIssuer();
			} catch (IOException e) {
				e.printStackTrace();
			}

			return null;
		});
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
		return getServiceDid().thenComposeAsync(targetDid -> {
			setTargetDid(targetDid);
			setType(type);
			String cacheCredential = restoreCredential();
			if (null != cacheCredential && !checkExpired(cacheCredential)) {
				return CompletableFuture.supplyAsync(() -> cacheCredential);
			}
			return handler.getAuthorization(authHelper.serviceDid(), targetDid, targetHost);
		});
	}

	@Override
	public CompletableFuture<Boolean> store(BackupAuthenticationHandler handler) {
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

	@Override
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

	@Override
	public CompletableFuture<Boolean> activate() {
		return authHelper.checkValid().thenApplyAsync(aVoid -> {
			try {
				return activateImpl();
			} catch (HiveException e) {
				throw new CompletionException(e);
			}
		});
	}

	private boolean activateImpl() throws HiveException {
		try {
			Response response = this.connectionManager.getBackApi()
					.activeToVault(RequestBody.create(MediaType.parse("Content-Type, application/json"), "{}"))
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
