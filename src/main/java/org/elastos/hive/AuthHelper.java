package org.elastos.hive;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.concurrent.atomic.AtomicBoolean;

import org.elastos.did.DIDDocument;
import org.elastos.did.jwt.Claims;
import org.elastos.hive.connection.ConnectionManager;
import org.elastos.hive.connection.model.BaseServiceConfig;
import org.elastos.hive.connection.model.HeaderConfig;
import org.elastos.hive.exception.HiveException;
import org.elastos.hive.network.model.AuthResponse;
import org.elastos.hive.network.model.SignResponse;
import org.elastos.hive.utils.JwtUtil;
import org.json.JSONException;
import org.json.JSONObject;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import retrofit2.Response;

class AuthHelper implements ConnectHelper {

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
		if(null == token) tryRestoreToken();
		if (token == null || token.isExpired()) {
			signIn();
		}
		initConnection();
		connectState.set(true);
	}

	private void retryLogin()  throws HiveException {
		connectState.set(false);
		signIn();
		initConnection();
		connectState.set(true);
	}

	private void signIn() throws HiveException {
		Map<String, Object> map = new HashMap<>();
		JSONObject docJsonObject = new JSONObject(authenticationDIDDocument.toString());
		map.put("document", docJsonObject);

		try {
			String json = new JSONObject(map).toString();
			Response<SignResponse> response = this.connectionManager.getVaultApi()
					.signIn(getJsonRequestBoy(json))
					.execute();
			SignResponse signResponse = response.body();
			if (null == signResponse) {
				throw new HiveException("Sign in challenge failed");
			}
			String jwtToken = signResponse.getChallenge();
			if (null != this.authenticationHandler && verifyToken(jwtToken)) {
				String approveJwtToken = this.authenticationHandler.authenticationChallenge(jwtToken).get();
				nodeAuth(approveJwtToken);
			}
		} catch (Exception e) {
			throw new HiveException(e.getMessage());
		}
	}

	private void nodeAuth(String token) throws Exception {
		Map<String, Object> map = new HashMap<>();
		map.put("jwt", token);
		String json = new JSONObject(map).toString();
		Response<AuthResponse> response = this.connectionManager.getVaultApi()
				.auth(getJsonRequestBoy(json))
				.execute();
		handleAuthResponse(response);
	}

	private boolean verifyToken(String jwtToken) {
		try {
			Claims claims = JwtUtil.getBody(jwtToken);
			long exp = claims.getExpiration().getTime();
			String aud = claims.getAudience();

			String did = authenticationDIDDocument.getSubject().toString();
			if (null == did
					|| null == aud
					|| !did.equals(aud))
				return false;

			long currentTime = System.currentTimeMillis();
			if (currentTime > exp) return false;

		} catch (Exception e) {
			e.printStackTrace();
		}

		return true;
	}

	private void handleAuthResponse(Response response) throws Exception {
		AuthResponse authResponse = (AuthResponse) response.body();
		if (null == authResponse) {
			throw new HiveException("Authorize failed");
		}

		String access_token = authResponse.getAccess_token();
		if (null == access_token) return;
		Claims claims = JwtUtil.getBody(access_token);
		long exp = claims.getExpiration().getTime();
		setUserDid((String) claims.get("userDid"));
		setAppId((String) claims.get("appId"));
		setAppInstanceDid((String) claims.get("appInstanceDid"));

		long expiresTime = System.currentTimeMillis() / 1000 + exp / 1000;

		token = new AuthToken("",
				access_token,
				expiresTime, "token");

		//Store the local data.
		writebackToken();

		//init connection
		initConnection();

	}


	private void tryRestoreToken() {
		try {

			JSONObject json = persistent.parseFrom();

			if(!json.has(ACCESS_TOKEN_KEY)) return;

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
		this.connectionManager.resetVaultApi(this.nodeUrl,
				baseServiceConfig);
	}

	public String getOwnerDid() {
		return this.ownerDid;
	}

	public String getUserDid() {
		return this.userDid;
	}

	public void setUserDid(String userDid) {
		this.userDid = userDid;
	}

	public String getAppId() {
		return this.appId;
	}

	public void setAppId(String appId) {
		this.appId = appId;
	}

	public String getAppInstanceDid() {
		return this.appInstanceDid;
	}

	public void setAppInstanceDid(String appInstanceDid) {
		this.appInstanceDid = appInstanceDid;
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
				retryLogin();
			} else {
				String message  = response.message();
				throw new HiveException(message);
			}
		}
	}
}