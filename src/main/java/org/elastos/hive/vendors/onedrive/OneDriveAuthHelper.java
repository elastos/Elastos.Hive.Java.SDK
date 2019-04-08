package org.elastos.hive.vendors.onedrive;

import org.elastos.hive.AuthHelper;
import org.elastos.hive.AuthInfo;
import org.elastos.hive.AuthResult;
import org.elastos.hive.Authenticator;
import org.elastos.hive.exceptions.HiveException;

import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;

final class OneDriveAuthHelper implements AuthHelper {
	private final static String REQUEST_URL_PREFIX = "https://login.microsoftonline.com/common/oauth2/v2.0/";

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
			String requestUrl;
			AuthResult result;

			requestUrl = REQUEST_URL_PREFIX + "authorize?"
					+ "client_id=" + appId
					+ "&scope=" + scopes
					+ "&response_type=code"
					+ "&redirect_url=" + redirectUrl;

			result = authenticator.requestAuthentication(requestUrl);
			if (!result.isAuthorized()) {
				// TODO;
			}

			requestAccessToken(result.getAuthorCode());
			result = null;
		}

		if (isExpired()) {
			refreshAccessToken();
		}

		return true;
	}

	private void requestAccessToken(String authorCode) throws HiveException {
		HttpResponse<JsonNode> response;
		try {
			String body = "client_id=" + appId
					+ "&redirect_url=" + redirectUrl
					+ "&code=" + authorCode
					+ "&grant_type=authorization_code";

			response = Unirest.post(REQUEST_URL_PREFIX + "token")
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
		HttpResponse<JsonNode> response;
		try {
			String body = "client_id=" + appId
					+ "&redirect_url=" + redirectUrl
					+ "&refresh_token=" + authInfo.getRefreshToken()
					+ "&grant_type=refresh_token";

			response = Unirest.post(REQUEST_URL_PREFIX + "token")
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
