package org.elastos.hive.vendors.onedrive;

import java.util.concurrent.CompletableFuture;

import org.elastos.hive.AuthHelper;
import org.elastos.hive.AuthToken;
import org.elastos.hive.Authenticator;
import org.elastos.hive.Callback;
import org.elastos.hive.OAuthEntry;
import org.elastos.hive.Result;
import org.elastos.hive.Status;

class OneDriveAuthHelper implements AuthHelper {
	private final OAuthEntry authEntry;

	OneDriveAuthHelper(OAuthEntry authEntry) {
		this.authEntry = authEntry;
	}

	@Override
	public AuthToken getToken() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public CompletableFuture<Result<AuthToken>> loginAsync(Authenticator authenticator) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public CompletableFuture<Result<AuthToken>> loginAsync(Authenticator authenticator, Callback<AuthToken> callback) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public CompletableFuture<Result<Status>> logoutAsync(Callback<Status> callback) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public CompletableFuture<Result<Status>> logoutAsync() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public CompletableFuture<Result<AuthToken>> checkExpired(Callback<AuthToken> callback) {
		// TODO Auto-generated method stub
		return null;
	}
}

/*
final class OneDriveAuthHelper implements AuthHelper {
	private final OAuthEntry authEntry;
	private AuthToken authToken;

	OneDriveAuthHelper(OAuthEntry authEntry) {
		this.authEntry = authEntry;
	}

	@Override
	public synchronized void login(Authenticator authenticator) throws HiveException {
		if (!hasLogin())
			requestAccessToken(getAuthCode(authenticator));

		if (isExpired())
			redeemAccessToken();
	}

	@Override
	public synchronized void logout() throws HiveException {
		try {
			String url = String
					.format("%s/%s?%s=%",
							OneDriveURL.AUTH,
							OneDriveMethod.LOGOUT,
							authEntry.getRedirectURL())
					.replace(" ", "%20");

			HttpResponse<String> response = Unirest.get(url).asString();
			if (response.getStatus() == 200) {
				authToken = null;
			} else {
				throw new HiveException("logout has error");
			}
		} catch (UnirestException e) {
			throw new HiveException(e.getMessage());
		}
	}

	private String getAuthCode(Authenticator authenticator) throws HiveException {
		Semaphore semph = new Semaphore(1);
		AuthServer server = new AuthServer(semph);
		server.start();

		try {
			String url1 = String
					.format("%s/%?client_id=%s&scope=%s&response_type=code&redirect_uri=%s",
							OneDriveURL.AUTH,
							OneDriveMethod.AUTHORIZE,
							authEntry.getClientId(),
							authEntry.getScope(),
							authEntry.getRedirectURL())
					.replace(" ", "%20");

			authenticator.requestAuthentication(url1);

			semph.acquire();
			String authCode = server.getAuthCode();
			server.close();
			semph.release();

			return authCode;
		}catch (InterruptedException e) {
			throw new HiveException(e.getMessage());
		}
	}

	private void requestAccessToken(String authCode) throws HiveException {
		try {
			String url  = String
					.format("%s/%s",
							OneDriveURL.AUTH,
							OneDriveMethod.TOKEN)
					.replace(" ", "%20");

			String body = String
					.format("client_id=%&redirect_url=%s&code=%s&grant_type=authorization_code",
							authEntry.getClientId(),
							authEntry.getRedirectURL(), authCode);

			HttpResponse<JsonNode> response = Unirest.post(url)
					.header("Content-Type", "application/x-www-form-urlencoded")
					.body(body)
					.asJson();

			if (response.getStatus() == 200) {
				JSONObject jsonObject = response.getBody().getObject();
				authToken = new AuthToken(jsonObject.getString("scope"),
										  jsonObject.getString("access_token"),
										  jsonObject.getString("refresh_token"),
										  jsonObject.getLong("expires_in"));
				jsonObject = null;
			} else {
				throw new HiveException(response.getStatusText());
			}
		} catch (UnirestException e) {
			throw new HiveException(e.getMessage());
		}
	}

	private void redeemAccessToken() throws HiveException {
		try {
			String url 	= String
					.format("%s/%s",
							OneDriveURL.AUTH,
							OneDriveMethod.TOKEN)
					.replace(" ", "%20");

			String body = String
					.format("client_id=%s&redirect_url=%s&refresh_token=%s&grant_type=refresh_token",
							authEntry.getClientId(),
							authEntry.getRedirectURL(),
							authToken.getRefreshToken());

			HttpResponse<JsonNode> response = Unirest.post(url)
					.header("Content-Type", "application/x-www-form-urlencoded")
					.body(body)
					.asJson();

			if (response.getStatus() == 200) {
				JSONObject jsonObject = response.getBody().getObject();
				authToken = new AuthToken(jsonObject.getString("scope"),
						  jsonObject.getString("access_token"),
						  jsonObject.getString("refresh_token"),
						  jsonObject.getLong("expires_in"));
				jsonObject = null;
			} else {
				throw new HiveException(response.getStatusText());
			}
		} catch (UnirestException e) {
			throw new HiveException(e.getMessage());
		}
	}

	@Override
	public AuthToken getAuthToken() {
		return authToken;
	}

	private boolean hasLogin() {
		return authToken != null;
	}

	private boolean isExpired() {
		return authToken.isExpired();
	}

	@Override
	public void checkExpired() throws HiveException {
		// TODO
	}
}
*/
