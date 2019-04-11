package org.elastos.hive.vendors.onedrive;

import java.util.concurrent.Semaphore;

import org.elastos.hive.AuthHelper;
import org.elastos.hive.AuthInfo;
import org.elastos.hive.AuthServer;
import org.elastos.hive.Authenticator;
import org.elastos.hive.exceptions.HiveException;
import org.jetbrains.annotations.NotNull;

import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;

final class OneDriveAuthHelper implements AuthHelper {
	private final static String AUTH_URL_PREFIX = "https://login.microsoftonline.com/common/oauth2/v2.0";

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
			refreshAccessToken();
		}

		return true;
	}

	private @NotNull String getAuthCode(Authenticator authenticator) throws HiveException {
		String url = String
				.format("%s/authorize?client_id=%s&scope=%s&response_type=code&redirect_uri=%s",
						AUTH_URL_PREFIX, appId, scopes, redirectUrl)
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
					.format("client_id=%s&%redirect_url=%s&code=%s&grant_type=authorization_code",
							appId, redirectUrl, authCode);

			HttpResponse<JsonNode> response = Unirest.post(AUTH_URL_PREFIX + "/token")
							.header("Content-Type", "application/x-www-form-urlencoded")
							.body(body)
							.asJson();

			if (response.getStatus() == 200) {
				// TODO;
			} else {
				// TODO;
			}
		} catch (UnirestException e) {
			// TODO
			e.printStackTrace();
		}

		// TODO:
	}

	private void refreshAccessToken() throws HiveException {
		try {
			String body = String
					.format("client_id=%s&%redirect_url=%s&refresh_token=%s&grant_type=refresh_token",
							appId, redirectUrl, authInfo.getRefreshToken());

			HttpResponse<JsonNode> response = Unirest.post(AUTH_URL_PREFIX + "token")
					.header("Content-Type", "application/x-www-form-urlencoded")
					.body(body)
					.asJson();

			if (response.getStatus() == 200) {
				// TODO;
			} else {
				// TODO;
			}
		} catch (UnirestException e) {
			// TODO
			e.printStackTrace();
		}
		// TODO:
	}

	private AuthInfo getAuthInfo() {
		return authInfo;
	}

	private boolean hasLogin() {
		return authInfo != null;
	}

	private boolean isExpired() {
		return authInfo.isExpired();
	}

	@Override
	public void checkExpired() throws HiveException {
		// TODO
	}
}
