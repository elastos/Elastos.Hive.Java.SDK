package org.elastos.hive.vault;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.concurrent.atomic.AtomicBoolean;

import org.elastos.did.DIDDocument;
import org.elastos.did.jwt.Claims;
import org.elastos.hive.AuthInfoStoreImpl;
import org.elastos.hive.AuthToken;
import org.elastos.hive.AuthenticationHandler;
import org.elastos.hive.ConnectHelper;
import org.elastos.hive.Persistent;
import org.elastos.hive.connection.ConnectionManager;
import org.elastos.hive.connection.model.BaseServiceConfig;
import org.elastos.hive.connection.model.HeaderConfig;
import org.elastos.hive.exception.HiveException;
import org.elastos.hive.utils.JwtUtil;
import org.elastos.hive.vault.network.model.AuthResponse;
import org.elastos.hive.vault.network.model.SignResponse;
import org.json.JSONException;
import org.json.JSONObject;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import retrofit2.Response;

public class AuthHelper implements ConnectHelper {

	private static final String ACCESS_TOKEN_KEY = "access_token";
	private static final String REFRESH_TOKEN_KEY = "refresh_token";
	private static final String EXPIRES_AT_KEY = "expires_at";
	private static final String TOKEN_TYPE_KEY = "token_type";

	private static final String USER_DID_KEY = "user_did";
	private static final String APP_ID_KEY = "app_id";
	private static final String APP_INSTANCE_DID_KEY = "app_instance_did";

	private String ownerDid;
	private String userDid;
	private String appId;
	private String appInstanceDid;

	private String nodeUrl;

	private AuthToken token;
	private AtomicBoolean connectState = new AtomicBoolean(false);
	private Persistent persistent;

	private DIDDocument authenticationDIDDocument;
	private AuthenticationHandler authenticationHandler;
	private ConnectionManager connectionManager;

	public AuthHelper(String ownerDid, String nodeUrl, String storePath, DIDDocument authenticationDIDDocument, AuthenticationHandler handler) {
		this.authenticationDIDDocument = authenticationDIDDocument;
		this.authenticationHandler = handler;
		this.ownerDid = ownerDid;
		this.nodeUrl = nodeUrl;

		this.persistent = new AuthInfoStoreImpl(ownerDid, nodeUrl, storePath);

		try {
			BaseServiceConfig config = new BaseServiceConfig.Builder().build();
			this.connectionManager = new ConnectionManager(this.nodeUrl, config);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public ConnectionManager getConnectionManager() {
		return this.connectionManager;
	}

	@Override
	public CompletableFuture<Void> checkValid() {
		return CompletableFuture.runAsync(() -> {
			try {
				doCheckExpired();
			} catch (Exception e) {
				throw new CompletionException(new HiveException(e.getMessage()));
			}
		});
	}

	private void doCheckExpired() throws HiveException {
		connectState.set(false);
		tryRestoreToken();
		if (token == null || token.isExpired()) {
			signIn();
		}
		initConnection();
		connectState.set(true);
	}

	private void signIn() throws HiveException {
		Map<String, Object> map = new HashMap<>();
		JSONObject docJson;

		docJson = new JSONObject(authenticationDIDDocument.toString());
		map.put("document", docJson);

		try {
			String json = new JSONObject(map).toString();
			Response<SignResponse> response = ConnectionManager.getHiveVaultApi()
					.signIn(getJsonRequestBoy(json))
					.execute();
			SignResponse signResponse = response.body();
			if (null == signResponse)
				throw new HiveException("Sign in challenge failed");


			String jwtToken = signResponse.getChallenge();
			if (null != this.authenticationHandler && verifyToken(jwtToken)) {
				String approveJwtToken = this.authenticationHandler.authenticationChallenge(jwtToken).get();
				nodeAuth(approveJwtToken);
			}
		} catch (Exception e) {
			throw new HiveException(e.getMessage());
		}
	}

	private void nodeAuth(String token) throws HiveException {
		Map<String, Object> map = new HashMap<>();
		map.put("jwt", token);

		try {
			String json = new JSONObject(map).toString();
			Response<AuthResponse> response;

			response = ConnectionManager.getHiveVaultApi()
					.auth(getJsonRequestBoy(json))
					.execute();

			AuthResponse authResponse = response.body();
			if (authResponse == null)
				throw new HiveException("Authorization failed");

			String accessToken = authResponse.getAccess_token();
			if (accessToken == null)
				throw new HiveException("No access token found");

			Claims claims = JwtUtil.getBody(accessToken);
			setUserDid(claims.get("userDid").toString())
			.setAppId(claims.get("appId").toString())
			.setAppInstanceDid(claims.get("appInstanceDid").toString());

			long expireTime = claims.getExpiration().getTime();
			long expireAt = System.currentTimeMillis() / 1000 + expireTime / 1000;

			this.token = new AuthToken(null, accessToken, expireAt, "token");
			writebackToken();
			initConnection();
		} catch (IOException e) {
			e.printStackTrace();
			throw new HiveException(e.getMessage());
		}
	}

	private boolean verifyToken(String token) {
		Claims claims = JwtUtil.getBody(token);
		long expiresAt = claims.getExpiration().getTime();
		String audience = claims.getAudience();

		return (audience != null &&
				authenticationDIDDocument.getSubject().toString().equals(audience) &&
				System.currentTimeMillis() < expiresAt);
	}

	private void tryRestoreToken() {
		if (token != null)
			return;

		try {

			JSONObject json = persistent.parseFrom();

			this.userDid = json.getString(USER_DID_KEY);
			this.appId = json.getString(APP_ID_KEY);
			this.appInstanceDid = json.getString(APP_INSTANCE_DID_KEY);

			this.token = new AuthToken(json.getString(REFRESH_TOKEN_KEY),
					json.getString(ACCESS_TOKEN_KEY),
					json.getLong(EXPIRES_AT_KEY),
					json.getString(TOKEN_TYPE_KEY));
		} catch (JSONException e) {
			e.printStackTrace();
		} catch (HiveException e) {
			e.printStackTrace();
		}
	}

	private void writebackToken() {
		try {
			JSONObject json = new JSONObject();

			json.put(REFRESH_TOKEN_KEY, token.getRefreshToken());
			json.put(ACCESS_TOKEN_KEY, token.getAccessToken());
			json.put(EXPIRES_AT_KEY, token.getExpiredTime());
			json.put(TOKEN_TYPE_KEY, token.getTokenType());
			json.put(USER_DID_KEY, this.userDid);
			json.put(APP_ID_KEY, this.appId);
			json.put(APP_INSTANCE_DID_KEY, this.appInstanceDid);

			persistent.upateContent(json);
		} catch (JSONException e) {
			e.printStackTrace();
		} catch (HiveException e) {
			e.printStackTrace();
		}
	}

	private void initConnection() {
		HeaderConfig headerConfig = new HeaderConfig.Builder()
				.authToken(token)
				.build();
		BaseServiceConfig baseServiceConfig = new BaseServiceConfig.Builder()
				.headerConfig(headerConfig)
				.build();
		this.connectionManager.resetHiveVaultApi(this.nodeUrl,
				baseServiceConfig);
	}

	public String getOwnerDid() {
		return this.ownerDid;
	}

	public String getUserDid() {
		return this.userDid;
	}

	public AuthHelper setUserDid(String userDid) {
		this.userDid = userDid;
		return this;
	}

	public String getAppId() {
		return this.appId;
	}

	private AuthHelper setAppId(String appId) {
		this.appId = appId;
		return this;
	}

	public String getAppInstanceDid() {
		return this.appInstanceDid;
	}

	private AuthHelper setAppInstanceDid(String appInstanceDid) {
		this.appInstanceDid = appInstanceDid;
		return this;
	}

	private RequestBody getJsonRequestBoy(String json) {
		return RequestBody.create(MediaType.parse("Content-Type, application/json"), json);
	}

	public void checkResponseCode(Response response) throws HiveException {
		if (response == null)
			throw new HiveException("response is null");

		int code = response.code();
		if (code >= 300 || code<200) {
			if(code==401) {
				doCheckExpired();
			} else {
				String message  = response.message();
				throw new HiveException(message);
			}
		}
	}
}
