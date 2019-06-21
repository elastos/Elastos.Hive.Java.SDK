package org.elastos.hive.vendors.onedrive;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
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
import org.elastos.hive.Status;
import org.elastos.hive.UnirestAsyncCallback;
import org.json.JSONObject;

import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;

class OneDriveAuthHelper implements AuthHelper {
	private final OAuthEntry authEntry;
	private AuthToken token;
	private final String keystorePath;

	OneDriveAuthHelper(OAuthEntry authEntry, String keystorePath) {
		this.authEntry = authEntry;
		this.keystorePath = keystorePath;
	}

	@Override
	public AuthToken getToken() {
		return token;
	}

	OAuthEntry getAuthEntry() {
		return authEntry;
	}

	@Override
	public CompletableFuture<Status> loginAsync(Authenticator authenticator) {
		return loginAsync(authenticator, new NullCallback<Status>());
	}

	@Override
	public CompletableFuture<Status> loginAsync(Authenticator authenticator,
		Callback<Status> callback) {

		return getAuthCode(authenticator)
				.thenCompose(code -> getToken(code, callback));
	}

	@Override
	public CompletableFuture<Status> logoutAsync() {
		return logoutAsync(new NullCallback<Status>());
	}

	@Override
	public CompletableFuture<Status> logoutAsync(Callback<Status> callback) {
		CompletableFuture<Status> future = new CompletableFuture<Status>();
		String url = String.format("%s/%s?post_logout_redirect_uri=%s",
								   OneDriveURL.AUTH,
								   OneDriveMethod.LOGOUT,
								   authEntry.getRedirectURL())
							.replace(" ", "%20");
		Unirest.get(url).asStringAsync(new LogoutCallback(future, callback));
		return future;
	}

	@Override
	public CompletableFuture<Status> checkExpired() {
		return checkExpired(new NullCallback<Status>());
	}

	@Override
	public CompletableFuture<Status> checkExpired(Callback<Status> callback) {
		if (token.isExpired())
			return redeemToken(callback);

		CompletableFuture<Status> future = new CompletableFuture<Status>();
		Status status = new Status(1);
	    callback.onSuccess(status);
		future.complete(status);
		return future;
	}

	void updateAuthToken(String refreshToken, String accessToken, long experitime) {
		this.token = new AuthToken(refreshToken, accessToken, experitime);
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
			}

			String authCode = server.getAuthCode();
			server.close();
			semph.release();

			return authCode;
		});

		return future;
	}

	private CompletableFuture<Status> getToken(String authCode, Callback<Status> callback) {
		CompletableFuture<Status> future = new CompletableFuture<Status>();
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

	private CompletableFuture<Status> redeemToken(Callback<Status> callback) {
		CompletableFuture<Status> future = new CompletableFuture<Status>();
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

	private void storeLocalData() {
		BufferedReader bufferedReader = null;
		BufferedWriter writer = null;
		try {
			File ipfsConfig = new File(keystorePath, OneDriveUtils.CONFIG);
			InputStreamReader reader = new InputStreamReader(new FileInputStream(ipfsConfig));
			bufferedReader = new BufferedReader(reader);
			String line;
			String content = "";
			while ((line = bufferedReader.readLine()) != null) {
				content += line;
			}

			JSONObject config;
			if (!content.isEmpty()) {
				config = new JSONObject(content);
			}
			else {
				config = new JSONObject();
			}

			//store the info.
			config.put(OneDriveUtils.ClientId, getAuthEntry().getClientId());
			config.put(OneDriveUtils.Scope, getAuthEntry().getScope());
			config.put(OneDriveUtils.RedirectUrl, getAuthEntry().getRedirectURL());
			config.put(OneDriveUtils.RefreshToken, getToken().getRefreshToken());
			config.put(OneDriveUtils.AccessToken, getToken().getAccessToken());
			config.put(OneDriveUtils.ExpiresAt, getToken().getExpiredTime());

			writer = new BufferedWriter(new FileWriter(ipfsConfig));
			writer.write(config.toString());
		} catch (IOException e) {
			e.printStackTrace();
		}
		finally {
			if (bufferedReader != null) {
				try {
					bufferedReader.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}

			if (writer != null) {
				try {
					writer.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}
	
	private class GetTokenCallback implements UnirestAsyncCallback<JsonNode> {
		private final CompletableFuture<Status> future;
		private final Callback<Status> callback;

		GetTokenCallback(CompletableFuture<Status> future, Callback<Status> callback) {
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

			JSONObject jsonObject = response.getBody().getObject();
			long experitime = System.currentTimeMillis() / 1000 + jsonObject.getLong("expires_in");
			AuthToken token = new AuthToken(jsonObject.getString("refresh_token"),
											jsonObject.getString("access_token"),
											experitime);

			OneDriveAuthHelper.this.token = token;

			//Store the local data.
			storeLocalData();

			Status status = new Status(1);
			this.callback.onSuccess(status);
			this.future.complete(status);
		}

		@Override
		public void failed(UnirestException arg0) {
			HiveException ex = new HiveException(arg0.getMessage());
			this.callback.onError(ex);
			future.completeExceptionally(ex);
		}
	}

	private class LogoutCallback implements com.mashape.unirest.http.async.Callback<String> {
		private final CompletableFuture<Status> future;
		private final Callback<Status> callback;

		LogoutCallback(CompletableFuture<Status> future, Callback<Status> callback) {
			this.future = future;
			this.callback = callback;
		}
		@Override
		public void cancelled() {}

		@Override
		public void completed(HttpResponse<String> response) {
			if (response.getStatus() != 200) {
				HiveException ex = new HiveException(response.getStatusText());
				this.callback.onError(ex);
				future.completeExceptionally(ex);
				return;
			}

			Status status = new Status(1);
			this.callback.onSuccess(status);
			future.complete(status);
			OneDriveAuthHelper.this.token = null;
		}

		@Override
		public void failed(UnirestException exception) {
			HiveException ex = new HiveException(exception.getMessage());
			this.callback.onError(ex);
			future.completeExceptionally(ex);
		}
	}
}
