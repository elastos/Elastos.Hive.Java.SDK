package org.elastos.hive.vendors.onedrive;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Semaphore;

import org.elastos.hive.AuthHelper;
import org.elastos.hive.AuthServer;
import org.elastos.hive.AuthToken;
import org.elastos.hive.Authenticator;
import org.elastos.hive.Callback;
import org.elastos.hive.HiveException;
import org.elastos.hive.NullCallback;
import org.elastos.hive.OAuthEntry;
import org.elastos.hive.Persistent;
import org.elastos.hive.UnirestAsyncCallback;
import org.elastos.hive.Void;
import org.json.JSONObject;

import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;

class OneDriveAuthHelper implements AuthHelper {
	public static final String clientIdKey 		= "client_id";
	public static final String accessTokenKey 	= "access_token";
	public static final String refreshTokenKey 	= "refresh_token";
	public static final String expireAtKey 		= "expires_at";

	private final OAuthEntry authEntry;
	private AuthToken token;
	private final Persistent persistent;

	OneDriveAuthHelper(OAuthEntry authEntry, Persistent persistent) {
		this.authEntry = authEntry;
		this.persistent = persistent;
	}

	@Override
	public AuthToken getToken() {
		return token;
	}

	@Override
	public CompletableFuture<Void> loginAsync(Authenticator authenticator) {
		return loginAsync(authenticator, new NullCallback<Void>());
	}

	@Override
	public CompletableFuture<Void> loginAsync(Authenticator authenticator,
		Callback<Void> callback) {

		tryRestoreToken();

		if (token != null) {
			long current = System.currentTimeMillis() / 1000;
			//Check the expire time
			if (token.getExpiredTime() > current) {
				CompletableFuture<Void> future = new CompletableFuture<Void>();
				Void placeHolder = new Void();
			    callback.onSuccess(placeHolder);
				future.complete(placeHolder);
				return future;
			}

			return redeemToken(callback);
		}

		return getAuthCode(authenticator)
				.thenCompose(code -> getToken(code, callback));
	}

	@Override
	public CompletableFuture<Void> logoutAsync() {
		return logoutAsync(new NullCallback<Void>());
	}

	@Override
	public CompletableFuture<Void> logoutAsync(Callback<Void> callback) {
		CompletableFuture<Void> future = new CompletableFuture<Void>();
		String url = String.format("%s/%s?post_logout_redirect_uri=%s",
								   OneDriveURL.AUTH,
								   OneDriveMethod.LOGOUT,
								   authEntry.getRedirectURL())
							.replace(" ", "%20");
		Unirest.get(url).asStringAsync(new LogoutCallback(future, callback));
		return future;
	}

	@Override
	public CompletableFuture<Void> checkExpired() {
		return checkExpired(new NullCallback<Void>());
	}

	@Override
	public CompletableFuture<Void> checkExpired(Callback<Void> callback) {
		if (token.isExpired())
			return redeemToken(callback);

		CompletableFuture<Void> future = new CompletableFuture<Void>();
		Void placeHolder = new Void();
	    callback.onSuccess(placeHolder);
		future.complete(placeHolder);
		return future;
	}

	private CompletableFuture<String> getAuthCode(Authenticator authenticator) {
		CompletableFuture<String> future = CompletableFuture.supplyAsync(() -> {
			Semaphore semph = new Semaphore(1);
			AuthServer server = null;
			String url;

			try {
				server = new AuthServer(semph);
			} catch (HiveException ex) {
				ex.printStackTrace();
				// TODO: error
			}
			server.start();

			url = String.format("%s/%s?client_id=%s&scope=%s&response_type=code&redirect_uri=%s",
								OneDriveURL.AUTH,
								OneDriveMethod.AUTHORIZE,
								authEntry.getClientId(),
								authEntry.getScope(),
								authEntry.getRedirectURL())
						.replace(" ", "%20");

			authenticator.requestAuthentication(url);
			try {
				semph.acquire();
			}catch (InterruptedException e) {
				e.printStackTrace();
				// TODO: error
			}

			String authCode = server.getAuthCode();
			server.close();
			semph.release();

			return authCode;
		});

		return future;
	}

	private CompletableFuture<Void> getToken(String authCode, Callback<Void> callback) {
		CompletableFuture<Void> future = new CompletableFuture<Void>();
		String url 	= String.format("%s/%s",
									OneDriveURL.AUTH,
									OneDriveMethod.TOKEN);
		String body	= String.format("client_id=%s&redirect_url=%s&code=%s&grant_type=authorization_code",
									authEntry.getClientId(),
									authEntry.getRedirectURL(),
									authCode);

		Unirest.post(url)
			.header(OneDriveHttpHeader.ContentType, OneDriveHttpHeader.Urlencoded)
			.body(body)
			.asJsonAsync(new GetTokenCallback(future, callback));

		return future;
	}

	private CompletableFuture<Void> redeemToken(Callback<Void> callback) {
		CompletableFuture<Void> future = new CompletableFuture<Void>();
		String url 	= String.format("%s/%s",
									OneDriveURL.AUTH,
									OneDriveMethod.TOKEN);
		String body	= String.format("client_id=%s&redirect_url=%s&refresh_token=%s&grant_type=refresh_token",
									authEntry.getClientId(),
									authEntry.getRedirectURL(),
									token.getRefreshToken())
							.replace(" ", "%20");

		Unirest.post(url)
			.header(OneDriveHttpHeader.ContentType, OneDriveHttpHeader.Urlencoded)
			.body(body)
			.asJsonAsync(new GetTokenCallback(future, callback));

		return future;
	}

	private void tryRestoreToken() {
		try {
			JSONObject json = persistent.parseFrom();
			String refreshToken = null;
			String accessToken = null;
			long expiresAt = -1;

			if (json.has(refreshTokenKey))
				refreshToken = json.getString(refreshTokenKey);
			if (json.has(accessTokenKey))
				accessToken = json.getString(accessTokenKey);
			if (json.has(expireAtKey))
				expiresAt = json.getLong(expireAtKey);

			if (refreshToken != null && accessToken != null && expiresAt > 0)
				this.token = new AuthToken(refreshToken, accessToken, expiresAt);
		} catch (HiveException e) {
			// TODO: Log output.
		}
	}

	private void clearToken() {
		try {
			JSONObject json = new JSONObject();
			json.put(clientIdKey, authEntry.getClientId());
			persistent.upateContent(json);
		} catch (Exception e) {
			e.printStackTrace();
		}

		token = null;
	}

	private void writebackToken() {
		if (token == null)
			return;

		try {
			JSONObject json = new JSONObject();
			json.put(clientIdKey, authEntry.getClientId());
			json.put(refreshTokenKey, token.getRefreshToken());
			json.put(refreshTokenKey, token.getAccessToken());
			json.put(expireAtKey, token.getExpiredTime());

			persistent.upateContent(json);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private class GetTokenCallback implements UnirestAsyncCallback<JsonNode> {
		private final CompletableFuture<Void> future;
		private final Callback<Void> callback;

		GetTokenCallback(CompletableFuture<Void> future, Callback<Void> callback) {
			this.future = future;
			this.callback = callback;
		}
		@Override
		public void cancelled() {}

		@Override
		public void completed(HttpResponse<JsonNode> response) {
			if (response.getStatus() != 200) {
				HiveException ex = new HiveException(response.getStatusText());
				this.callback.onError(ex);
				future.completeExceptionally(ex);
				return;
			}

			org.json.JSONObject jsonObject = response.getBody().getObject();
			long experitime = System.currentTimeMillis() / 1000 + jsonObject.getLong("expires_in");
			AuthToken token = new AuthToken(jsonObject.getString("refresh_token"),
											jsonObject.getString("access_token"),
											experitime);

			OneDriveAuthHelper.this.token = token;

			//Store the local data.
			writebackToken();

			Void placeHolder = new Void();
		    callback.onSuccess(placeHolder);
			future.complete(placeHolder);
		}

		@Override
		public void failed(UnirestException arg0) {
			HiveException ex = new HiveException(arg0.getMessage());
			this.callback.onError(ex);
			future.completeExceptionally(ex);
		}
	}

	private class LogoutCallback implements com.mashape.unirest.http.async.Callback<String> {
		private final CompletableFuture<Void> future;
		private final Callback<Void> callback;

		LogoutCallback(CompletableFuture<Void> future, Callback<Void> callback) {
			this.future = future;
			this.callback = callback;
		}
		@Override
		public void cancelled() {}

		@Override
		public void completed(HttpResponse<String> response) {
			if (response.getStatus() != 200) {
				HiveException e = new HiveException(response.getStatusText());
				this.callback.onError(e);
				future.completeExceptionally(e);
				return;
			}

			Void placeHolder = new Void();
		    callback.onSuccess(placeHolder);
			future.complete(placeHolder);

			OneDriveAuthHelper.this.clearToken();
		}

		@Override
		public void failed(UnirestException exception) {
			HiveException e = new HiveException(exception.getMessage());
			this.callback.onError(e);
			future.completeExceptionally(e);
		}
	}
}
