package org.elastos.hive.vendors.onedrive;

import org.elastos.hive.AuthHelper;
import org.elastos.hive.AuthServer;
import org.elastos.hive.AuthToken;
import org.elastos.hive.Authenticator;
import org.elastos.hive.Callback;
import org.elastos.hive.HiveException;
import org.elastos.hive.NullCallback;
import org.elastos.hive.OAuthEntry;
import org.elastos.hive.Persistent;
import org.elastos.hive.Void;
import org.elastos.hive.utils.UrlUtil;
import org.elastos.hive.vendors.onedrive.Model.BaseServiceConfig;
import org.elastos.hive.vendors.onedrive.Model.TokenResponse;
import org.elastos.hive.vendors.onedrive.network.AuthApi;
import org.elastos.hive.vendors.onedrive.network.BaseServiceUtil;
import org.json.JSONObject;

import java.io.IOException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Semaphore;

import retrofit2.Call;
import retrofit2.Response;

public class OneDriveAuthHelper implements AuthHelper {
	public static final String clientIdKey 		= "client_id";
	public static final String accessTokenKey 	= "access_token";
	public static final String refreshTokenKey 	= "refresh_token";
	public static final String expireAtKey 		= "expires_at";

	private final OAuthEntry authEntry;
	private final Persistent persistent;
	private AuthToken token;
	private AuthApi authApi ;


	protected OneDriveAuthHelper(OAuthEntry authEntry, Persistent persistent) {
		this.authEntry = authEntry;
		this.persistent = persistent;
		try {
			BaseServiceConfig baseServiceConfig = new BaseServiceConfig(true,false,null,false);
			authApi = BaseServiceUtil.createService(AuthApi.class, Constance.ONE_DRIVE_AUTH_BASE_URL ,baseServiceConfig);
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

		AuthApi logoutApi = null;
		try {
			BaseServiceConfig baseServiceConfig = new BaseServiceConfig(false,false,null,false);
			logoutApi = BaseServiceUtil.createService(AuthApi.class, Constance.ONE_DRIVE_AUTH_BASE_URL ,baseServiceConfig);
		} catch (Exception e) {
			e.printStackTrace();
		}
		Call call = logoutApi.logout(authEntry.getRedirectURL());

		call.enqueue(new AuthCallback(future,callback,Type.LOGOUT));

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

	protected CompletableFuture<String> getAuthCode(Authenticator authenticator) {
		CompletableFuture<String> future = CompletableFuture.supplyAsync(() -> {
			Semaphore semph = new Semaphore(1);

			String hostUrl = authEntry.getRedirectURL() ;
			String[] hostAndPort = UrlUtil.decodeHostAndPort(hostUrl , Constance.DEFAULT_REDIRECT_URL , String.valueOf(Constance.DEFAULT_REDIRECT_PORT));

			String host = hostAndPort[0] ;
			int port = Integer.valueOf(hostAndPort[1]) ;

			AuthServer server = new AuthServer(semph, host , port);
			try {
				server.start();
			} catch (IOException e) {
				e.printStackTrace();
			}

			String url = String.format("%s/%s?client_id=%s&scope=%s&response_type=code&redirect_uri=%s",
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
			try{
				server.stop();
			}catch (Exception e){
			}

			semph.release();

			return authCode;
		});

		return future;
	}

	private CompletableFuture<Void> getToken(String authCode, Callback<Void> callback) {
		CompletableFuture<Void> future = new CompletableFuture<Void>();

		Call call = authApi.getToken(authEntry.getClientId(),authCode,
				authEntry.getRedirectURL(),Constance.GRANT_TYPE_GET_TOKEN);

		call.enqueue(new AuthCallback(future,callback,Type.GET_TOKEN));

		return future;
	}

	private CompletableFuture<Void> redeemToken(Callback<Void> callback) {
		CompletableFuture<Void> future = new CompletableFuture<Void>();

		Call call = authApi.refreshToken(authEntry.getClientId(),authEntry.getRedirectURL(),
				token.getRefreshToken(),Constance.GRANT_TYPE_REFRESH_TOKEN);

		call.enqueue(new AuthCallback(future,callback,Type.REDEEM_TOKEN));

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

					break;
				case LOGOUT:
					OneDriveAuthHelper.this.clearToken();
					break;
				default:
					break;
			}


			Void placeHolder = new Void();
		    callback.onSuccess(placeHolder);
			future.complete(placeHolder);
		}

		@Override
		public void onFailure(Call call, Throwable t) {
			HiveException ex = new HiveException(t.getMessage());
			this.callback.onError(ex);
			future.completeExceptionally(ex);
		}
	}

	private enum Type{
		GET_TOKEN, REDEEM_TOKEN, LOGOUT
	}
}
