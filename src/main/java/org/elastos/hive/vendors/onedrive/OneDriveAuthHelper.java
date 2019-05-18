package org.elastos.hive.vendors.onedrive;

import java.util.concurrent.CompletableFuture;

import org.elastos.hive.AuthCode;
import org.elastos.hive.AuthHelper;
import org.elastos.hive.AuthToken;
import org.elastos.hive.Authenticator;
import org.elastos.hive.Callback;
import org.elastos.hive.HiveException;
import org.elastos.hive.NullCallback;
import org.elastos.hive.OAuthEntry;
import org.elastos.hive.Status;
import org.json.JSONObject;

import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;

class OneDriveAuthHelper implements AuthHelper {
	private final OAuthEntry authEntry;
	private AuthCode  code;
	private AuthToken token;

	OneDriveAuthHelper(OAuthEntry authEntry) {
		this.authEntry = authEntry;
	}

	@Override
	public AuthToken getToken() {
		return token;
	}

	@Override
	public CompletableFuture<AuthToken> loginAsync(Authenticator authenticator) {
		return loginAsync(authenticator, new NullCallback<AuthToken>());
	}

	@Override
	public CompletableFuture<AuthToken> loginAsync(Authenticator authenticator,
		Callback<AuthToken> callback) {
		// TODO
		return null;
	}

	@Override
	public CompletableFuture<Status> logoutAsync() {
		return logoutAsync(new NullCallback<Status>());
	}

	@Override
	public CompletableFuture<Status> logoutAsync(Callback<Status> callback) {
		CompletableFuture<Status> future = new CompletableFuture<Status>();
		String url = String.format("%s/%s?redirect_url=%s",
								   OneDriveURL.AUTH,
								   OneDriveMethod.LOGOUT,
								   authEntry.getRedirectURL())
							.replace(" ", "%20");
		Unirest.get(url).asJsonAsync(new LogoutCallback(future, callback));
		return future;
	}

	@Override
	public CompletableFuture<AuthToken> checkExpired(Callback<AuthToken> callback) {
		if (token.isExpired())
			return redeemToken(callback);

		CompletableFuture<AuthToken> future = new CompletableFuture<AuthToken>();
		callback.onSuccess(token);
		future.complete(token);
		return future;
	}

	private CompletableFuture<AuthCode> getAuthCode(Authenticator authenticator,
			Callback<AuthCode> callback) {
		// TODO;
		return null;
	}

	private CompletableFuture<AuthToken> getToken(String authCode, Callback<AuthToken> callback) {
		CompletableFuture<AuthToken> future = new CompletableFuture<AuthToken>();
		String url 	= String.format("%s/%s",
									OneDriveURL.AUTH,
									OneDriveMethod.TOKEN);
		String body	= String.format("client_id=%&redirect_url=%s&code=%s&grant_type=authorization_code",
									authEntry.getClientId(),
									authEntry.getRedirectURL(),
									authCode);

		Unirest.post(url)
			.header(OneDriveHttpHeader.ContentType, "application/x-www-form-urlencoded")
			.body(body)
			.asJsonAsync(new GetTokenCallback(future, callback));

		return future;
	}

	private CompletableFuture<AuthToken> redeemToken(Callback<AuthToken> callback) {
		CompletableFuture<AuthToken> future = new CompletableFuture<AuthToken>();
		String url 	= String.format("%s/%s",
									OneDriveURL.AUTH,
									OneDriveMethod.TOKEN);
		String body	= String.format("client_id=%&redirect_url=%s&refresh_token=%s&grant_type=refresh_token",
									authEntry.getClientId(),
									authEntry.getRedirectURL(),
									token.getRefreshToken());

		Unirest.post(url)
			.header(OneDriveHttpHeader.ContentType, "application/x-www-form-urlencoded")
			.body(body)
			.asJsonAsync(new GetTokenCallback(future, callback));

		return future;
	}

	private class GetTokenCallback implements UnirestAsyncCallback<JsonNode> {
		private final CompletableFuture<AuthToken> future;
		private final Callback<AuthToken> callback;

		GetTokenCallback(CompletableFuture<AuthToken> future, Callback<AuthToken> callback) {
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
			AuthToken token = new AuthToken(jsonObject.getString("scope"),
											jsonObject.getString("access_token"),
											jsonObject.getString("refresh_token"),
											jsonObject.getLong("expires_in"));

			OneDriveAuthHelper.this.token = token;
			this.callback.onSuccess(token);
			future.complete(token);
		}

		@Override
		public void failed(UnirestException arg0) {
			HiveException ex = new HiveException(arg0.getMessage());
			this.callback.onError(ex);
			future.completeExceptionally(ex);
		}
	}

	private class LogoutCallback implements UnirestAsyncCallback<JsonNode> {
		private final CompletableFuture<Status> future;
		private final Callback<Status> callback;

		LogoutCallback(CompletableFuture<Status> future, Callback<Status> callback) {
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

			Status status = new Status(1);
			this.callback.onSuccess(status);
			future.complete(status);
		}

		@Override
		public void failed(UnirestException exception) {
			HiveException ex = new HiveException(exception.getMessage());
			this.callback.onError(ex);
			future.completeExceptionally(ex);
		}
	}
}
