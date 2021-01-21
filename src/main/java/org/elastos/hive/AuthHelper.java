package org.elastos.hive;

import com.fasterxml.jackson.databind.JsonNode;

import org.elastos.did.jwt.Claims;
import org.elastos.hive.connection.ConnectionManager;
import org.elastos.hive.connection.model.BaseServiceConfig;
import org.elastos.hive.connection.model.HeaderConfig;
import org.elastos.hive.exception.HiveException;
import org.elastos.hive.utils.JsonUtil;
import org.elastos.hive.utils.JwtUtil;
import org.elastos.hive.utils.ResponseHelper;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Response;

class AuthHelper implements ConnectHelper {

	private static final String ACCESS_TOKEN_KEY = "access_token";
	private static final String REFRESH_TOKEN_KEY = "refresh_token";
	private static final String EXPIRES_AT_KEY = "expires_at";
	private static final String TOKEN_TYPE_KEY = "token_type";

	private static final String SERVICE_DID = "service_did";
	private static final String USER_DID_KEY = "user_did";
	private static final String APP_ID_KEY = "app_id";
	private static final String APP_INSTANCE_DID_KEY = "app_instance_did";

	private String ownerDid;
	private String userDid;
	private String appId;
	private String appInstanceDid;
	private String serviceDid;

	private String endPoint;

	private AuthToken token;
	private Persistent persistent;

	private ConnectionManager connectionManager;
	private ApplicationContext context;
	private AuthenticationAdapter authenticationAdapter;

	public AuthHelper(ApplicationContext context, String ownerDid, String endPoint, AuthenticationAdapter adapter) {
		this.ownerDid = ownerDid;
		this.endPoint = endPoint;
		this.context = context;
		this.authenticationAdapter = adapter;
		this.persistent = new PersistentImpl(ownerDid, endPoint, this.context.getLocalDataDir());

		try {
			BaseServiceConfig config = new BaseServiceConfig.Builder().build();
			this.connectionManager = new ConnectionManager(this.endPoint, config);
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

	private synchronized void doCheckExpired() throws HiveException {
		if(null == token) restoreToken();
		if (token == null || token.isExpired()) {
			signIn();
		}
		initConnection();
	}

	private synchronized void retryLogin()  throws HiveException {
		signIn();
		initConnection();
	}

	private void signIn() throws HiveException {
		Map<String, Object> map = new HashMap<>();
		JSONObject docJsonObject = new JSONObject(this.context.getAppInstanceDocument().toString());
		map.put("document", docJsonObject);

		try {
			String json = new JSONObject(map).toString();
			Response response = this.connectionManager.getAuthApi()
					.signIn(getJsonRequestBoy(json))
					.execute();
			checkResponse(response);
			JsonNode ret = ResponseHelper.getValue(response, JsonNode.class).get("challenge");
			if(null == ret)
				throw new HiveException("Sign in failed");

			String jwtToken = ret.textValue();
			if (authenticationAdapter !=null && verifyToken(jwtToken)) {
				String approveJwtToken = authenticationAdapter.getAuthorization(this.context, jwtToken).get();
				nodeAuth(approveJwtToken);
			}
		} catch (Exception e) {
			throw new HiveException(e.getMessage());
		}
	}

	private void nodeAuth(String token) throws Exception {
		if(null == token)
			return;
		Map<String, Object> map = new HashMap<>();
		map.put("jwt", token);
		String json = new JSONObject(map).toString();
		Response response = this.connectionManager.getAuthApi()
				.auth(getJsonRequestBoy(json))
				.execute();
		checkResponse(response);
		handleAuthResponse(response);
	}

	private boolean verifyToken(String jwtToken) {
		try {
			Claims claims = JwtUtil.getBody(jwtToken);
			long exp = claims.getExpiration().getTime();
			String aud = claims.getAudience();

			String did = this.context.getAppInstanceDocument().getSubject().toString();
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
		JsonNode ret = ResponseHelper.getValue(response, JsonNode.class).get("access_token");
		if(null == ret)
			throw new HiveException("Sign in failed");

		String accessToken = ret.textValue();
		if (null == accessToken) return;
		Claims claims = JwtUtil.getBody(accessToken);
		serviceDid(claims.getIssuer());
		long exp = claims.getExpiration().getTime();
		JsonNode props = JsonUtil.deserialize(claims.get("props").toString());
		userDid(props.get("userDid").textValue());
		appId(props.get("appDid").textValue());
		appInstanceDid((String) claims.get("aud"));

		long expiresTime = System.currentTimeMillis() / 1000 + exp / 1000;

		token = new AuthToken("",
				accessToken,
				expiresTime, "token");

		//Store the local data.
		writeToken();

		//init connection
		initConnection();

	}


	private void restoreToken() {
		try {

			JSONObject json = persistent.parseFrom();

			if(!json.has(ACCESS_TOKEN_KEY)) return;

			this.userDid = json.getString(USER_DID_KEY);
			this.appId = json.getString(APP_ID_KEY);
			this.serviceDid = json.getString(SERVICE_DID);
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

	private void writeToken() {
		try {
			JSONObject json = new JSONObject();

			json.put(REFRESH_TOKEN_KEY, token.getRefreshToken());
			json.put(ACCESS_TOKEN_KEY, token.getAccessToken());
			json.put(EXPIRES_AT_KEY, token.getExpiredTime());
			json.put(TOKEN_TYPE_KEY, token.getTokenType());
			json.put(USER_DID_KEY, this.userDid);
			json.put(APP_ID_KEY, this.appId);
			json.put(SERVICE_DID, this.serviceDid);
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
		this.connectionManager.resetVaultApi(this.endPoint,
				baseServiceConfig);
	}

	public String endPoint() {
		return this.endPoint;
	}

	public String serviceDid() {
		return this.serviceDid;
	}

	public void serviceDid(String serviceDid) {
		this.serviceDid = serviceDid;
	}

	public String ownerDid() {
		return this.ownerDid;
	}

	public String userDid() {
		return this.userDid;
	}

	public void userDid(String userDid) {
		this.userDid = userDid;
	}

	public String appId() {
		return this.appId;
	}

	public void appId(String appId) {
		this.appId = appId;
	}

	public String appInstanceDid() {
		return this.appInstanceDid;
	}

	public void appInstanceDid(String appInstanceDid) {
		this.appInstanceDid = appInstanceDid;
	}

	private RequestBody getJsonRequestBoy(String json) {
		return RequestBody.create(MediaType.parse("Content-Type, application/json"), json);
	}

	public void checkResponse(Response response) throws HiveException {
		if (response == null)
			throw new HiveException("response is null");

		int code = response.code();
		if (code >= 300 || code<200) {
			ResponseBody body = (ResponseBody) response.errorBody();
			String message  = null;
			try {
				message = body.string();
			} catch (IOException e) {
				throw new CompletionException(e);
			}
			throw new HiveException(message);
		}
	}

	public void checkResponseWithRetry(Response response) throws HiveException {
		if (response == null)
			throw new HiveException("response is null");

		int code = response.code();
		if(code==401) {
			retryLogin();
		} else {
			checkResponse(response);
		}
	}

	public void removeToken() {
		token = null;
		persistent.deleteContent();

	}
}
