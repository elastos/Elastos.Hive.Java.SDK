package org.elastos.hive;

import org.elastos.hive.auth.LocalResolver;
import org.elastos.hive.auth.RemoteResolver;
import org.elastos.hive.auth.TokenResolver;
import org.elastos.hive.connection.ConnectionManager;
import org.elastos.hive.connection.model.BaseServiceConfig;
import org.elastos.hive.storage.FileStorage;
import org.json.JSONException;
import org.json.JSONObject;

import static org.junit.Assert.assertNotNull;

import java.util.concurrent.CompletableFuture;
import java.util.function.Supplier;

public class AuthHelper {
	private AuthToken token;
	private AppContextProvider contextProvider;
	private ConnectionManager connectionManager;
	private FileStorage persistent;

	private TokenResolver tokenResolver;

	public AuthHelper() {
		tokenResolver = new LocalResolver();
		tokenResolver.setNextResolver(new RemoteResolver());
		//
	}

	public AuthHelper(AppContext appContext) {
		this.contextProvider = appContext.getAppContextProvider();
		BaseServiceConfig config = new BaseServiceConfig.Builder().build();
		this.connectionManager = new ConnectionManager(appContext.getProviderAddress(), config);
	}

	public CompletableFuture<Void> login() {
		return CompletableFuture.runAsync(new Runnable() {
			@Override
			public void run() {
				if(null == token) {

				}
			}
		});
	}

	private CompletableFuture<String> challenge() {
		return CompletableFuture.supplyAsync(new Supplier<String>() {
			@Override
			public String get() {
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


	public AuthToken getAuthToken() {
		return tokenResolver.getToken();
	}
}
