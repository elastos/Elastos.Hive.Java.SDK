/*
 * Copyright (c) 2019 Elastos Foundation
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package org.elastos.hive.vendor.onedrive;

/*
import org.elastos.hive.AuthHelper;
import org.elastos.hive.AuthServer;
import org.elastos.hive.AuthToken;
import org.elastos.hive.Authenticator;
import org.elastos.hive.Callback;
import org.elastos.hive.HiveException;
import org.elastos.hive.NullCallback;
import org.elastos.hive.Persistent;
import org.elastos.hive.result.Void;
import org.elastos.hive.utils.UrlUtil;
import org.elastos.hive.vendors.connection.ConnectionManager;
import org.elastos.hive.vendors.connection.model.BaseServiceConfig;
import org.elastos.hive.vendors.connection.model.HeaderConfig;
import org.elastos.hive.vendors.onedrive.network.model.TokenResponse;
import org.json.JSONObject;

import java.io.IOException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Semaphore;

import retrofit2.Call;
import retrofit2.Response;

public class OneDriveAuthHelper implements AuthHelper {
	private static final String clientIdKey 		= "client_id";
	private static final String accessTokenKey 		= "access_token";
	private static final String refreshTokenKey 	= "refresh_token";
	private static final String expireAtKey 		= "expires_at";

	private final String clientId;
	private final String scope;
	private final String redirectUrl;

	private final Persistent persistent;
	private AuthToken token;

	OneDriveAuthHelper(String clientId , String scope , String redirectUrl , String persistentStorePath) {
		this.clientId = clientId ;
		this.scope = scope ;
		this.redirectUrl = redirectUrl ;
		this.persistent = new AuthInfoStoreImpl(persistentStorePath);

		try {
			BaseServiceConfig config = new BaseServiceConfig.Builder().build();
			ConnectionManager.resetAuthApi(OneDriveConstance.ONE_DRIVE_AUTH_BASE_URL, config);
		} catch (Exception e) {
			e.printStackTrace();
		}
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

		return accessAuthCode(authenticator)
				.thenCompose(code -> accessToken(code, callback));
	}

	@Override
	public CompletableFuture<Void> logoutAsync() {
		return logoutAsync(new NullCallback<Void>());
	}

	@Override
	public CompletableFuture<Void> logoutAsync(Callback<Void> callback) {
		CompletableFuture<Void> future = new CompletableFuture<Void>();

		try {
			ConnectionManager.getAuthApi()
                    .logout(redirectUrl)
                    .enqueue(new AuthCallback(future,callback,Type.LOGOUT));
		} catch (Exception e) {
			e.printStackTrace();
		}

		return future;
	}

	@Override
	public CompletableFuture<Void> checkExpired() {
		return checkExpired(new NullCallback<Void>());
	}

	@Override
	public CompletableFuture<Void> checkExpired(Callback<Void> callback) {
		CompletableFuture<Void> future = new CompletableFuture<Void>();
		Void padding = new Void();

		if (token == null) {
			HiveException e = new HiveException("Please login first");
			callback.onError(e);
			future.completeExceptionally(e);
			return future;
		}

		if (token.isExpired())
			return redeemToken(callback);

	    callback.onSuccess(padding);
		future.complete(padding);
		return future;
	}

	private CompletableFuture<String> accessAuthCode(Authenticator authenticator) {
		CompletableFuture<String> future = CompletableFuture.supplyAsync(() -> {
			Semaphore semph = new Semaphore(1);

			String hostUrl = redirectUrl ;
			String[] hostAndPort = UrlUtil.decodeHostAndPort(hostUrl , OneDriveConstance.DEFAULT_REDIRECT_URL , String.valueOf(OneDriveConstance.DEFAULT_REDIRECT_PORT));

			String host = hostAndPort[0] ;
			int port = Integer.valueOf(hostAndPort[1]) ;

			AuthServer server = new AuthServer(semph, host , port);
			try {
				server.start();
			} catch (IOException e) {
				e.printStackTrace();
			}

			String url = String.format("%s/%s?client_id=%s&scope=%s&response_type=code&redirect_uri=%s",
								OneDriveConstance.ONE_DRIVE_AUTH_URL,
								OneDriveConstance.AUTHORIZE,
								clientId,
								scope,
								redirectUrl)
						.replace(" ", "%20");

			authenticator.requestAuthentication(url);

			try {
				semph.acquire();
			}catch (InterruptedException e) {
				e.printStackTrace();
				// TODO: error
			}

			String authCode = server.getAuthCode();
			try{
				server.stop();
			}catch (Exception e){
				// TODO;
			}

			semph.release();

			return authCode;
		});

		return future;
	}

	private CompletableFuture<Void> accessToken(String authCode, Callback<Void> callback) {
		CompletableFuture<Void> future = new CompletableFuture<Void>();

		try {
			ConnectionManager.getAuthApi()
					.getToken(clientId,authCode,
                    redirectUrl, OneDriveConstance.GRANT_TYPE_GET_TOKEN)
                    .enqueue(new AuthCallback(future,callback,Type.GET_TOKEN));
		} catch (Exception e) {
			e.printStackTrace();
		}
		return future;
	}

	private CompletableFuture<Void> redeemToken(Callback<Void> callback) {
		CompletableFuture<Void> future = new CompletableFuture<Void>();

		try {
			ConnectionManager.getAuthApi()
                    .refreshToken(clientId,redirectUrl,
                    token.getRefreshToken(), OneDriveConstance.GRANT_TYPE_REFRESH_TOKEN)
                    .enqueue(new AuthCallback(future,callback,Type.REDEEM_TOKEN));
		} catch (Exception e) {
			e.printStackTrace();
		}
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
		} catch (Exception e) {
			// TODO: Log output.
		}
	}

	private void clearToken() {
		try {
			JSONObject json = new JSONObject();
			json.put(clientIdKey, clientId);
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
			json.put(clientIdKey, clientId);
			json.put(refreshTokenKey, token.getRefreshToken());
			json.put(refreshTokenKey, token.getAccessToken());
			json.put(expireAtKey, token.getExpiredTime());

			persistent.upateContent(json);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private class AuthCallback implements retrofit2.Callback{

		private final CompletableFuture<Void> future;
		private final Callback<Void> callback;
		private Type type ;

		AuthCallback(CompletableFuture<Void> future, Callback<Void> callback , Type type){
			this.future = future;
			this.callback = callback;
			this.type = type ;
		}

		@Override
		public void onResponse(Call call, Response response) {
			if (response.code() != 200){
				HiveException ex = new HiveException(response.message());
				this.callback.onError(ex);
				future.completeExceptionally(ex);
				return ;
			}

			AuthToken token = null ;

			switch (type){
				case GET_TOKEN:
				case REDEEM_TOKEN:
					TokenResponse tokenResponse = (TokenResponse) response.body();
					long experitime = System.currentTimeMillis() / 1000 + tokenResponse.getExpires_in();

					token = new AuthToken(tokenResponse.getRefresh_token(),
							tokenResponse.getAccess_token(),
							experitime);

					OneDriveAuthHelper.this.token = token;

					//Store the local data.
					writebackToken();

					try {
						HeaderConfig headerConfig =
								new HeaderConfig.Builder()
										.authToken(token)
										.build();
						BaseServiceConfig baseServiceConfig =
								new BaseServiceConfig.Builder()
										.headerConfig(headerConfig)
										.build();
						ConnectionManager.resetOneDriveApi(
								OneDriveConstance.ONE_DRIVE_API_BASE_URL,
								baseServiceConfig);
					} catch (Exception e) {
						HiveException ex = new HiveException(e.getMessage());
						callback.onError(ex);
						future.completeExceptionally(ex);
						return;
					}

					break;

				case LOGOUT:
					OneDriveAuthHelper.this.clearToken();
					break;

				default:
					break;
			}

			Void padding = new Void();
		    callback.onSuccess(padding);
			future.complete(padding);
		}

		@Override
		public void onFailure(Call call, Throwable t) {
			HiveException e = new HiveException(t.getMessage());
			this.callback.onError(e);
			future.completeExceptionally(e);
		}
	}

	private enum Type{
		GET_TOKEN, REDEEM_TOKEN, LOGOUT
	}
}
*/