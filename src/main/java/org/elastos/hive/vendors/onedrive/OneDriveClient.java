package org.elastos.hive.vendors.onedrive;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import org.elastos.hive.AuthHelper;
import org.elastos.hive.AuthToken;
import org.elastos.hive.Authenticator;
import org.elastos.hive.Callback;
import org.elastos.hive.Client;
import org.elastos.hive.ClientInfo;
import org.elastos.hive.Drive;
import org.elastos.hive.DriveType;
import org.elastos.hive.HiveException;
import org.elastos.hive.NullCallback;
import org.elastos.hive.Result;
import org.elastos.hive.Status;

import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;

public final class OneDriveClient extends Client {
	private static Client clientInstance;

	private final AuthHelper authHelper;
	private ClientInfo clientInfo;
	private String clientId;

	private OneDriveClient(OneDriveParameter parameter) {
		authHelper = new OneDriveAuthHelper(parameter.getAuthEntry());
	}

	public static Client createInstance(OneDriveParameter parameter) {
		if (clientInstance == null) {
			clientInstance = new OneDriveClient(parameter);
		}
		return clientInstance;
	}

	public static Client getInstance() {
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
		Future<Result<AuthToken>> future = authHelper.loginAsync(authenticator);
		Result<AuthToken> result;

		try {
			result = future.get();
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
		Future<Result<Status>> future = authHelper.logoutAsync();
		Result<Status> result;

		try {
			result = future.get();
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
	public CompletableFuture<Result<ClientInfo>> getInfo() {
		return getInfo(new NullCallback<ClientInfo>());
	}

	@Override
	public CompletableFuture<Result<ClientInfo>> getInfo(Callback<ClientInfo> callback) {
		CompletableFuture<Result<ClientInfo>> future = new CompletableFuture<Result<ClientInfo>>();

		Unirest.get(OneDriveURL.API)
			.header("Authorization",  "bearer " + authHelper.getToken().getAccessToken())
			.asJsonAsync(new GetClientInfoCallback(future, callback));

		return future;
	}

	@Override
	public CompletableFuture<Result<Drive>> getDefaultDrive() {
		return getDefaultDrive(new NullCallback<Drive>());
	}

	@Override
	public CompletableFuture<Result<Drive>> getDefaultDrive(Callback<Drive> callback) {
		CompletableFuture<Result<Drive>> future = new CompletableFuture<Result<Drive>>();

		Unirest.get(OneDriveURL.API)
			.header("Authorization",  "bearer " + authHelper.getToken().getAccessToken())
			.asJsonAsync(new GetDriveCallback(future, callback));

		return future;
	}

	private class GetClientInfoCallback implements com.mashape.unirest.http.async.Callback<JsonNode> {
		private final CompletableFuture<Result<ClientInfo>> future;
		private final Callback<ClientInfo> callback;

		GetClientInfoCallback(CompletableFuture<Result<ClientInfo>> future, Callback<ClientInfo> callback) {
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
			Result<ClientInfo> value = new Result<ClientInfo>(e);
			this.callback.onFailed(e);
			future.complete(value);
		}
	}

	private class GetDriveCallback implements com.mashape.unirest.http.async.Callback<JsonNode> {
		private final CompletableFuture<Result<Drive>> future;
		private final Callback<Drive> callback;

		GetDriveCallback(CompletableFuture<Result<Drive>> future, Callback<Drive> callback) {
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
			Result<Drive> value = new Result<Drive>(e);
			this.callback.onFailed(e);
			future.complete(value);
		}
	}
}
