package org.elastos.hive.vendors.onedrive;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import org.elastos.hive.AuthHelper;
import org.elastos.hive.AuthToken;
import org.elastos.hive.Authenticator;
import org.elastos.hive.ClientInfo;
import org.elastos.hive.DriveType;
import org.elastos.hive.HiveCallback;
import org.elastos.hive.HiveClient;
import org.elastos.hive.HiveDrive;
import org.elastos.hive.HiveException;
import org.elastos.hive.HiveResult;
import org.elastos.hive.Status;

import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.async.Callback;
import com.mashape.unirest.http.exceptions.UnirestException;

public final class OneDriveClient extends HiveClient {
	private static HiveClient clientInstance;

	private final AuthHelper authHelper;
	private ClientInfo clientInfo;
	private AuthToken authToken;
	private String clientId;

	private OneDriveClient(OneDriveParameter parameter) {
		authHelper = new OneDriveAuthHelper(parameter.getAuthEntry());
	}

	public static HiveClient createInstance(OneDriveParameter parameter) {
		if (clientInstance == null) {
			clientInstance = new OneDriveClient(parameter);
		}
		return clientInstance;
	}

	public static HiveClient getInstance() {
		return clientInstance;
	}

	@Override
	public String getId() {
		return clientId;
	}

	@Override
	public DriveType getDriveType() {
		return DriveType.oneDrive;
	}

	@Override
	public synchronized void login(Authenticator authenticator) throws HiveException {
		Future<HiveResult<AuthToken>> future = authHelper.loginAsync(authenticator);
		HiveResult<AuthToken> result;

		try {
			result = future.get();
			authToken = result.getObject();
		} catch (InterruptedException e) {
			throw new HiveException(e.getMessage());
		} catch (ExecutionException e) {
			throw new HiveException(e.getMessage());
		}

		if (result.isFailed())
			throw result.getException();
	}

	@Override
	public synchronized void logout() throws HiveException {
		Future<HiveResult<Status>> future = authHelper.logoutAsync();
		HiveResult<Status> result;

		try {
			result = future.get();
			authToken = null;
		} catch (InterruptedException e) {
			throw new HiveException(e.getMessage());
		} catch (ExecutionException e) {
			throw new HiveException(e.getMessage());
		}

		if (result.isFailed())
			throw result.getException();
	}

	@Override
	public ClientInfo getLastInfo() {
		return clientInfo;
	}

	@Override
	public CompletableFuture<HiveResult<ClientInfo>> getInfo() {
		return getInfo(null);
	}

	@Override
	public CompletableFuture<HiveResult<ClientInfo>> getInfo(HiveCallback<ClientInfo, HiveException> callback) {
		CompletableFuture<HiveResult<ClientInfo>> future = new CompletableFuture<HiveResult<ClientInfo>>();

		Unirest.get(OneDriveURL.API)
			.header("Authorization",  "bearer " + authToken.getAccessToken())
			.asJsonAsync(new GetClientInfoCallback(future, callback));

		return future;
	}

	@Override
	public CompletableFuture<HiveResult<HiveDrive>> getDefaultDrive() {
		return getDefaultDrive(null);
	}

	@Override
	public CompletableFuture<HiveResult<HiveDrive>> getDefaultDrive(HiveCallback<HiveDrive, HiveException> callback) {
		CompletableFuture<HiveResult<HiveDrive>> future = new CompletableFuture<HiveResult<HiveDrive>>();

		Unirest.get(OneDriveURL.API)
			.header("Authorization",  "bearer " + authToken.getAccessToken())
			.asJsonAsync(new GetDriveCallback(future, callback));

		return future;
	}

	private class GetClientInfoCallback implements Callback<JsonNode> {
		private final CompletableFuture<HiveResult<ClientInfo>> future;
		private final HiveCallback<ClientInfo, HiveException> callback;

		GetClientInfoCallback(CompletableFuture<HiveResult<ClientInfo>> future, HiveCallback<ClientInfo, HiveException> callback) {
			this.future = future;
			this.callback = callback;
		}
		@Override
		public void cancelled() {
		}

		@Override
		public void completed(HttpResponse<JsonNode> arg0) {
		}

		@Override
		public void failed(UnirestException arg0) {
			HiveException e = new HiveException(arg0.getMessage());
			HiveResult<ClientInfo> value = new HiveResult<ClientInfo>(e);
			this.callback.onFailed(e);
			future.complete(value);
		}
	}

	private class GetDriveCallback implements Callback<JsonNode> {
		private final CompletableFuture<HiveResult<HiveDrive>> future;
		private final HiveCallback<HiveDrive, HiveException> callback;

		GetDriveCallback(CompletableFuture<HiveResult<HiveDrive>> future, HiveCallback<HiveDrive, HiveException> callback) {
			this.future = future;
			this.callback = callback;
		}
		@Override
		public void cancelled() {
		}

		@Override
		public void completed(HttpResponse<JsonNode> arg0) {
		}

		@Override
		public void failed(UnirestException arg0) {
			HiveException e = new HiveException(arg0.getMessage());
			HiveResult<HiveDrive> value = new HiveResult<HiveDrive>(e);
			this.callback.onFailed(e);
			future.complete(value);
		}
	}
}
