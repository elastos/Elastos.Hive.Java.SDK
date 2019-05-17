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
import org.elastos.hive.Result;
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
	public CompletableFuture<Result<AuthToken>> loginAsync(Authenticator authenticator) {
		return loginAsync(authenticator, new NullCallback<AuthToken>());
	}

	@Override
	public CompletableFuture<Result<AuthToken>> loginAsync(Authenticator authenticator,
		Callback<AuthToken> callback) {
		// TODO
		return null;
	}

	@Override
	public CompletableFuture<Result<Status>> logoutAsync() {
		return logoutAsync(new NullCallback<Status>());
	}

	@Override
	public CompletableFuture<Result<Status>> logoutAsync(Callback<Status> callback) {
		CompletableFuture<Result<Status>> future = new CompletableFuture<Result<Status>>();
		String url = String.format("%s/%s?redirect_url=%s",
								   OneDriveURL.AUTH,
								   OneDriveMethod.LOGOUT,
								   authEntry.getRedirectURL())
							.replace(" ", "%20");
		Unirest.get(url).asJsonAsync(new LogoutCallback(future, callback));
		return future;
	}

	@Override
	public CompletableFuture<Result<AuthToken>> checkExpired(Callback<AuthToken> callback) {
		if (token.isExpired())
			return redeemToken(callback);

		CompletableFuture<Result<AuthToken>> future = new CompletableFuture<Result<AuthToken>>();
		callback.onSuccess(token);
		future.complete(new Result<AuthToken>(token));
		return future;
	}

	private CompletableFuture<Result<AuthCode>> getAuthCode(Authenticator authenticator,
			Callback<AuthCode> callback) {
		// TODO;
		return null;
	}

	private CompletableFuture<Result<AuthToken>> getToken(String authCode, Callback<AuthToken> callback) {
		CompletableFuture<Result<AuthToken>> future = new CompletableFuture<Result<AuthToken>>();
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

	private CompletableFuture<Result<AuthToken>> redeemToken(Callback<AuthToken> callback) {
		CompletableFuture<Result<AuthToken>> future = new CompletableFuture<Result<AuthToken>>();
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

	private class GetTokenCallback implements com.mashape.unirest.http.async.Callback<JsonNode> {
		private final CompletableFuture<Result<AuthToken>> future;
		private final Callback<AuthToken> callback;

		GetTokenCallback(CompletableFuture<Result<AuthToken>> future, Callback<AuthToken> callback) {
			this.future = future;
			this.callback = callback;
		}
		@Override
		public void cancelled() {}

		@Override
		public void completed(HttpResponse<JsonNode> arg0) {
			if (arg0.getStatus() != 200) {
				HiveException e = new HiveException(arg0.getStatusText());
				this.callback.onError(e);
				future.complete(new Result<AuthToken>(e));
				return;
			}

			JSONObject jsonObject = arg0.getBody().getObject();
			AuthToken token = new AuthToken(jsonObject.getString("scope"),
											jsonObject.getString("access_token"),
											jsonObject.getString("refresh_token"),
											jsonObject.getLong("expires_in"));

			OneDriveAuthHelper.this.token = token;
			this.callback.onSuccess(token);
			future.complete(new Result<AuthToken>(token));
		}

		@Override
		public void failed(UnirestException arg0) {
			HiveException e = new HiveException(arg0.getMessage());
			this.callback.onError(e);
			future.complete(new Result<AuthToken>(e));
		}
	}

	private class LogoutCallback implements com.mashape.unirest.http.async.Callback<JsonNode> {
		private final CompletableFuture<Result<Status>> future;
		private final Callback<Status> callback;

		LogoutCallback(CompletableFuture<Result<Status>> future, Callback<Status> callback) {
			this.future = future;
			this.callback = callback;
		}
		@Override
		public void cancelled() {}

		@Override
		public void completed(HttpResponse<JsonNode> arg0) {
			if (arg0.getStatus() != 200) {
				HiveException e = new HiveException(arg0.getStatusText());
				this.callback.onError(e);
				future.complete(new Result<Status>(e));
				return;
			}

			Status status = new Status(1);
			this.callback.onSuccess(status);
			future.complete(new Result<Status>(status));
		}

		@Override
		public void failed(UnirestException arg0) {
			HiveException e = new HiveException(arg0.getMessage());
			this.callback.onError(e);
			future.complete(new Result<Status>(e));
		}
	}
}
