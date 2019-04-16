package org.elastos.hive.vendors.onedrive;

import java.util.concurrent.Semaphore;

import org.elastos.hive.AuthHelper;
import org.elastos.hive.AuthInfo;
import org.elastos.hive.AuthServer;
import org.elastos.hive.Authenticator;
import org.elastos.hive.exceptions.HiveException;
import org.jetbrains.annotations.NotNull;
import org.json.JSONObject;

import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;

final class OneDriveAuthHelper implements AuthHelper {
	private final static String AUTH_URL = "https://login.microsoftonline.com/common/oauth2/v2.0";

	private final String appId;
	private final String scopes;
	private final String redirectUrl;

	private AuthInfo authInfo;

	OneDriveAuthHelper(String appId, String scopes, String redirectUrl) {
		this.appId = appId;
		this.scopes = scopes;
		this.redirectUrl = redirectUrl;
	}

	@Override
	public synchronized boolean login(Authenticator authenticator) throws HiveException {
		if (!hasLogin()) {
			String authCode = getAuthCode(authenticator);
			requestAccessToken(authCode);
			authCode = null;
		}

		if (isExpired()) {
			redeemAccessToken();
		}

		return true;
	}

	@Override
	public void logout() throws HiveException {
		try {
			String requestUrl = AUTH_URL + "/logout?post_logout_redirect_uri=" + redirectUrl;
			HttpResponse<String> response = Unirest.get(requestUrl)
					.asString();
			if (response.getStatus() == 200) {
				System.out.println("logout response.getBody(): " + response.getBody());
				authInfo = null;
			} 
			else {
				throw new HiveException("logout has error");
			}
		} 
		catch (UnirestException e) {
			e.printStackTrace();
		}
	}
	
	private @NotNull String getAuthCode(Authenticator authenticator) throws HiveException {
		String url = String
				.format("%s/authorize?client_id=%s&scope=%s&response_type=code&redirect_uri=%s",
						AUTH_URL, appId, scopes, redirectUrl)
				.replace(" ", "%20");

		Semaphore semph = new Semaphore(1);
		AuthServer server = new AuthServer(semph);
		server.start();

		authenticator.requestAuthentication(url);

		try {
			semph.acquire();
		}catch (InterruptedException e) {
			e.printStackTrace();
			//TODO
		}

		String authCode = server.getAuthCode();
		System.out.println("authCode:" + authCode);
		server.close();
		semph.release();

		return authCode;
	}

	private void requestAccessToken(String authCode) throws HiveException {
		try {
			String body = String
					.format("client_id=%s&redirect_url=%s&code=%s&grant_type=authorization_code",
							appId, redirectUrl, authCode);

			HttpResponse<JsonNode> response = Unirest.post(AUTH_URL + "/token")
					.header("Content-Type", "application/x-www-form-urlencoded")
					.body(body)
					.asJson();

			if (response.getStatus() == 200) {
				JSONObject jsonObj = response.getBody().getObject();
				authInfo = new AuthInfo();
				authInfo.withScopes(jsonObj.getString("scope"))
						.withAccessToken(jsonObj.getString("access_token"))
						.withRefreshToken(jsonObj.getString("refresh_token"))
						.withExpiredIn(jsonObj.getLong("expires_in"));

				System.out.println("accessToken: " + authInfo.getAccessToken());
				System.out.println("refreshToken: " + authInfo.getRefreshToken());

			} else {
				// TODO;
			}
		} catch (UnirestException e) {
			// TODO
			e.printStackTrace();
		}
	}

	private void redeemAccessToken() throws HiveException {
		try {
			System.out.println("refreshToken: " + authInfo.getAccessToken());

			String body = String
					.format("client_id=%s&redirect_url=%s&refresh_token=%s&grant_type=refresh_token",
							appId, redirectUrl, authInfo.getRefreshToken());

			HttpResponse<JsonNode> response = Unirest.post(AUTH_URL + "token")
					.header("Content-Type", "application/x-www-form-urlencoded")
					.body(body)
					.asJson();

			if (response.getStatus() == 200) {
				JSONObject jsonObj = response.getBody().getObject();

				authInfo.resetAccessToken(jsonObj.getString("access_token"));

				System.out.println("accessToken: " + authInfo.getAccessToken());

			} else {
				// TODO;
			}
		} catch (UnirestException e) {
			// TODO
			e.printStackTrace();
		}
	}

	@Override
	public AuthInfo getAuthInfo() {
		return authInfo;
	}

	private boolean hasLogin() {
		return authInfo != null;
	}

	private boolean isExpired() {
		return true;
	}

	@Override
	public void checkExpired() throws HiveException {
		// TODO
	}
}
