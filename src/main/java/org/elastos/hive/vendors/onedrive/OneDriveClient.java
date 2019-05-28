package org.elastos.hive.vendors.onedrive;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import org.elastos.hive.AuthHelper;
import org.elastos.hive.AuthToken;
import org.elastos.hive.Authenticator;
import org.elastos.hive.Callback;
import org.elastos.hive.Client;
import org.elastos.hive.ClientInfo;
import org.elastos.hive.Drive;
import org.elastos.hive.DriveInfo;
import org.elastos.hive.DriveType;
import org.elastos.hive.HiveException;
import org.elastos.hive.NullCallback;
import org.elastos.hive.Status;
import org.elastos.hive.UnirestAsyncCallback;
import org.json.JSONObject;

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
		this.authHelper = new OneDriveAuthHelper(parameter.getAuthEntry());
	}

	public static Client createInstance(OneDriveParameter parameter) {
		if (clientInstance == null && parameter != null) {
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
		CompletableFuture<AuthToken> future = authHelper.loginAsync(authenticator);

		try {
			future.get();
		} catch (InterruptedException e) {
			throw new HiveException(e.getMessage());
		} catch (ExecutionException e) {
			throw new HiveException(e.getMessage());
		}
	}

	@Override
	public synchronized void logout() throws HiveException {
		CompletableFuture<Status> future = authHelper.logoutAsync();

		try {
			future.get();
		} catch (InterruptedException e) {
			throw new HiveException(e.getMessage());
		} catch (ExecutionException e) {
			throw new HiveException(e.getMessage());
		}
	}

	@Override
	public ClientInfo getLastInfo() {
		return clientInfo;
	}

	@Override
	public CompletableFuture<ClientInfo> getInfo() {
		return getInfo(new NullCallback<ClientInfo>());
	}

	@Override
	public CompletableFuture<ClientInfo> getInfo(Callback<ClientInfo> callback) {
		CompletableFuture<ClientInfo> future = new CompletableFuture<ClientInfo>();

		//TODO
		Unirest.get(OneDriveURL.API)
			.header(OneDriveHttpHeader.Authorization,
					OneDriveHttpHeader.bearerValue(authHelper))
			.asJsonAsync(new GetClientInfoCallback(future, callback));

		return future;
	}

	@Override
	public CompletableFuture<Drive> getDefaultDrive() {
		return getDefaultDrive(new NullCallback<Drive>());
	}

	@Override
	public CompletableFuture<Drive> getDefaultDrive(Callback<Drive> callback) {
		CompletableFuture<Drive> future = new CompletableFuture<Drive>();

		Unirest.get(OneDriveURL.API)
			.header(OneDriveHttpHeader.Authorization,
					OneDriveHttpHeader.bearerValue(authHelper))
			.asJsonAsync(new GetDriveCallback(future, callback));

		return future;
	}

	private class GetClientInfoCallback implements UnirestAsyncCallback<JsonNode> {
		private final CompletableFuture<ClientInfo> future;
		private final Callback<ClientInfo> callback;

		GetClientInfoCallback(CompletableFuture<ClientInfo> future,
			Callback<ClientInfo> callback) {
			this.future = future;
			this.callback = callback;
		}
		@Override
		public void cancelled() {}

		@Override
		public void completed(HttpResponse<JsonNode> response) {
			if (response.getStatus() != 200) {
				HiveException ex = new HiveException("Server Error: " + response.getStatusText());
				if (this.callback != null) {
					this.callback.onError(ex);	
				}

				future.completeExceptionally(ex);
				return;
			}

			//TODO
			ClientInfo info = new ClientInfo(null);
			JSONObject jsonObject = response.getBody().getObject();
			JSONObject ownerObject = (JSONObject) jsonObject.get("owner");
			JSONObject userObject = (JSONObject) ownerObject.get("user");
			
			info.setDisplayName(userObject.getString("displayName"));
			System.out.println("displayName="+info.getDisplayName());
			System.out.println("jsonObject="+jsonObject.toString());
			if (this.callback != null) {
				this.callback.onSuccess(info);
			}
			future.complete(info);
		}

		@Override
		public void failed(UnirestException exception) {
			HiveException e = new HiveException(exception.getMessage());
			if (this.callback != null) {
				this.callback.onError(e);				
			}
			
			future.completeExceptionally(e);
		}
	}

	private class GetDriveCallback implements UnirestAsyncCallback<JsonNode> {
		private final CompletableFuture<Drive> future;
		private final Callback<Drive> callback;

		GetDriveCallback(CompletableFuture<Drive> future, Callback<Drive> callback) {
			this.future = future;
			this.callback = callback;
		}
		@Override
		public void cancelled() {}

		@Override
		public void completed(HttpResponse<JsonNode> response) {
			if (response.getStatus() != 200) {
				HiveException ex = new HiveException("Server Error: " + response.getStatusText());
				if (this.callback != null) {
					this.callback.onError(ex);	
				}

				future.completeExceptionally(ex);
				return;
			}
			
			JSONObject jsonObject = response.getBody().getObject();
			DriveInfo info = new DriveInfo(jsonObject.getString("id"));
			OneDriveDrive drive = new OneDriveDrive(info, authHelper);
			if (this.callback != null) {
				this.callback.onSuccess(drive);
			}
			future.complete(drive);
		}

		@Override
		public void failed(UnirestException exception) {
			HiveException e = new HiveException(exception.getMessage());
			if (this.callback != null) {
				this.callback.onError(e);				
			}
			
			future.completeExceptionally(e);
		}
	}
}
