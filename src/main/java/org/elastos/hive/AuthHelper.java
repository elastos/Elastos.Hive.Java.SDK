package org.elastos.hive;

import com.fasterxml.jackson.databind.JsonNode;

import org.elastos.hive.connection.ConnectionManager;
import org.elastos.hive.connection.model.BaseServiceConfig;
import org.elastos.hive.connection.model.HeaderConfig;
import org.elastos.hive.storage.AuthPersistence;
import org.elastos.hive.utils.ResponseUtil;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.function.Function;
import java.util.function.Supplier;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import retrofit2.Response;

public class AuthHelper {
	private String baseUrl;
	private AuthToken token;
	private AppContextProvider contextProvider;
	private ConnectionManager connectionManager;
	private AuthPersistence persistent;

	public AuthHelper(AppContext appContext) {
		this.baseUrl = appContext.getProviderAddress();
		this.contextProvider = appContext.getAppContextProvider();
		BaseServiceConfig config = new BaseServiceConfig.Builder().build();
		this.connectionManager = new ConnectionManager(this.baseUrl, config);
	}

	public CompletableFuture<Void> login() {
		 return CompletableFuture.runAsync(new Runnable() {
			@Override
			public void run() {
				if(null == token) {
					restoreToken();
				}
			}
		}).thenComposeAsync(new Function<Void, CompletionStage<String>>() {
			@Override
			public CompletionStage<String> apply(Void aVoid) {
				if (token == null || token.isExpired()) {
					return challenge();
				}
				return CompletableFuture.completedFuture(null);
			}
		}).thenComposeAsync(new Function<String, CompletionStage<Void>>() {
			 @Override
			 public CompletionStage<Void> apply(String jwt) {
			 	if(null != jwt) {
			 		return authorize();
				}
				 return CompletableFuture.completedFuture(null);
			 }
		 }).thenRun(new Runnable() {
			 @Override
			 public void run() {
				 initConnection();
			 }
		 });
	}

	private CompletableFuture<String> challenge() {
		return CompletableFuture.supplyAsync(new Supplier<String>() {
			@Override
			public String get() {

				Map<String, Object> map = new HashMap<>();
				JSONObject docJsonObject = new JSONObject(contextProvider.getAppInstanceDocument().toString());
				map.put("document", docJsonObject);

				try {
					String json = new JSONObject(map).toString();
					Response response = connectionManager.getAuthApi()
							.signIn(RequestBody.create(MediaType.parse("Content-Type, application/json"), json))
							.execute();
					JsonNode ret = ResponseUtil.getValue(response, JsonNode.class).get("challenge");

					String jwtToken = ret.textValue();
				} catch (IOException e) {
					e.printStackTrace();
				}

				return null;
			}
		});
	}

	private CompletableFuture<Void> authorize() {
		return CompletableFuture.supplyAsync(new Supplier<Void>() {
			@Override
			public Void get() {
				return null;
			}
		});
	}

	private void initConnection() {
		HeaderConfig headerConfig = new HeaderConfig.Builder()
				.authToken(token)
				.build();
		BaseServiceConfig baseServiceConfig = new BaseServiceConfig.Builder()
				.headerConfig(headerConfig)
				.build();
		this.connectionManager.resetVaultApi(this.baseUrl,
				baseServiceConfig);
	}

	private static final String ACCESS_TOKEN_KEY = "access_token";
	private static final String EXPIRES_AT_KEY = "expires_at";
	private static final String TOKEN_TYPE_KEY = "token_type";

	private void restoreToken() {
		try {

			JSONObject json = persistent.restore();

			if(!json.has(ACCESS_TOKEN_KEY)) return;

			this.token = new AuthToken(
					json.getString(ACCESS_TOKEN_KEY),
					json.getLong(EXPIRES_AT_KEY),
					json.getString(TOKEN_TYPE_KEY));
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	private void storeToken() {
		try {
			JSONObject json = new JSONObject();

			json.put(ACCESS_TOKEN_KEY, token.getAccessToken());
			json.put(EXPIRES_AT_KEY, token.getExpiresTime());
			json.put(TOKEN_TYPE_KEY, token.getTokenType());

			persistent.store(json);
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

}
