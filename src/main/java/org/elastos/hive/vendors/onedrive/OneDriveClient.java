package org.elastos.hive.vendors.onedrive;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import org.elastos.hive.Authenticator;
import org.elastos.hive.Callback;
import org.elastos.hive.Client;
import org.elastos.hive.Drive;
import org.elastos.hive.DriveType;
import org.elastos.hive.HiveException;
import org.elastos.hive.JsonPersistent;
import org.elastos.hive.NullCallback;
import org.elastos.hive.UnirestAsyncCallback;
import org.elastos.hive.Void;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;

public final class OneDriveClient extends Client {
	private static Client clientInstance;

	private final OneDriveAuthHelper authHelper;
	private Client.Info clientInfo;
	private String userId;
	private KeyStore keyStore;
	private static String keystorePath;

	private OneDriveClient(OneDriveParameter parameter) {
		keystorePath = parameter.getKeyStorePath();
		keyStore = new KeyStore(keystorePath);
		this.authHelper = new OneDriveAuthHelper(parameter.getAuthEntry(), keyStore);
	}

	public static Client createInstance(OneDriveParameter parameter) {
		if (clientInstance == null)
			clientInstance = new OneDriveClient(parameter);

		return clientInstance;
	}

	public static Client getInstance() {
		return clientInstance;
	}

	@Override
	public String getId() {
		return userId;
	}

	@Override
	public DriveType getDriveType() {
		return DriveType.oneDrive;
	}

	@Override
	public synchronized void login(Authenticator authenticator) throws HiveException {
		CompletableFuture<Void> future = authHelper.loginAsync(authenticator);

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
		CompletableFuture<Void> future = authHelper.logoutAsync();

		try {
			future.get();
		} catch (InterruptedException e) {
			throw new HiveException(e.getMessage());
		} catch (ExecutionException e) {
			throw new HiveException(e.getMessage());
		}
	}

	@Override
	public Client.Info getLastInfo() {
		return clientInfo;
	}

	@Override
	public CompletableFuture<Client.Info> getInfo() {
		return getInfo(new NullCallback<Client.Info>());
	}

	@Override
	public CompletableFuture<Client.Info> getInfo(Callback<Client.Info> callback) {
		return authHelper.checkExpired()
				.thenCompose(status -> getInfo(status, callback));
	}

	private CompletableFuture<Client.Info> getInfo(Void status, Callback<Client.Info> callback) {
		CompletableFuture<Client.Info> future = new CompletableFuture<Client.Info>();

		if (callback == null)
			callback = new NullCallback<Client.Info>();

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
		return authHelper.checkExpired()
				.thenCompose(status -> getDefaultDrive(status, callback));
	}

	private CompletableFuture<Drive> getDefaultDrive(Void status, Callback<Drive> callback) {
		CompletableFuture<Drive> future = new CompletableFuture<Drive>();

		if (callback == null)
			callback = new NullCallback<Drive>();

		Unirest.get(OneDriveURL.API)
			.header(OneDriveHttpHeader.Authorization,
					OneDriveHttpHeader.bearerValue(authHelper))
			.asJsonAsync(new GetDefaultDriveCallback(future, callback));

		return future;
	}

	private class GetClientInfoCallback implements UnirestAsyncCallback<JsonNode> {
		private final CompletableFuture<Client.Info> future;
		private final Callback<Client.Info> callback;

		GetClientInfoCallback(CompletableFuture<Client.Info> future,
			Callback<Client.Info> callback) {
			this.future = future;
			this.callback = callback;
		}
		@Override
		public void cancelled() {}

		@Override
		public void completed(HttpResponse<JsonNode> response) {
			if (response.getStatus() == 401) {
				authHelper.getToken().expired();
				HiveException e = new HiveException("Server Error: " + response.getStatusText());
				this.callback.onError(e);
				future.completeExceptionally(e);
				return;
			}

			if (response.getStatus() != 200) {
				HiveException ex = new HiveException("Server Error: " + response.getStatusText());
				this.callback.onError(ex);
				future.completeExceptionally(ex);
				return;
			}

			org.json.JSONObject jsonObject = response.getBody().getObject();
			org.json.JSONObject userObject = jsonObject.getJSONObject("owner").getJSONObject("user");
			Client.Info info;

			HashMap<String, String> attrs = new HashMap<String, String>();
			attrs.put(Client.Info.userId, userObject.getString("id"));
			attrs.put(Client.Info.name, userObject.getString("displayName"));
			// TODO;

			info = new Client.Info(attrs);

			clientInfo = info;
			userId = clientInfo.get(Client.Info.userId);

			this.callback.onSuccess(info);
			future.complete(info);
		}

		@Override
		public void failed(UnirestException exception) {
			HiveException e = new HiveException(exception.getMessage());
			this.callback.onError(e);
			future.completeExceptionally(e);
		}
	}

	private class GetDefaultDriveCallback implements UnirestAsyncCallback<JsonNode> {
		private final CompletableFuture<Drive> future;
		private final Callback<Drive> callback;

		GetDefaultDriveCallback(CompletableFuture<Drive> future, Callback<Drive> callback) {
			this.future = future;
			this.callback = callback;
		}
		@Override
		public void cancelled() {}

		@Override
		public void completed(HttpResponse<JsonNode> response) {
			if (response.getStatus() == 401) {
				authHelper.getToken().expired();
				HiveException e = new HiveException("Server Error: " + response.getStatusText());
				this.callback.onError(e);
				future.completeExceptionally(e);
				return;
			}

			if (response.getStatus() != 200) {
				HiveException ex = new HiveException("Server Error: " + response.getStatusText());
				this.callback.onError(ex);
				future.completeExceptionally(ex);
				return;
			}

			org.json.JSONObject jsonObject = response.getBody().getObject();
			HashMap<String, String> attrs = new HashMap<>();
			attrs.put(Drive.Info.driveId, jsonObject.getString("id"));
			// TODO;

			Drive.Info info = new Drive.Info(attrs);
			OneDriveDrive drive = new OneDriveDrive(info, authHelper);
			this.callback.onSuccess(drive);
			future.complete(drive);
		}

		@Override
		public void failed(UnirestException exception) {
			HiveException e = new HiveException(exception.getMessage());
			this.callback.onError(e);
			future.completeExceptionally(e);
		}
	}

	class KeyStore implements JsonPersistent {
		private String storePath;

		KeyStore(String storePath) {
			this.storePath = String.format("%s/%s", storePath, OneDriveUtils.CONFIG);
		}

		@Override
		public JSONObject parseFrom() throws HiveException {
			JSONParser parser = new JSONParser();
	        try {
				return (JSONObject) parser.parse(new FileReader(storePath));
			} catch(Exception e) {
				throw new HiveException(e.getMessage());
	        }
		}

		@Override
		public void upateContent(JSONObject conetnt) throws HiveException {
			FileWriter fileWriter = null;
			try {
				fileWriter = new FileWriter(storePath);

				// Writting the jsonObject into sample.json
				fileWriter.write(conetnt.toJSONString());
			} catch (Exception e) {
				throw new HiveException(e.getMessage());
			}
			finally {
				if (fileWriter != null) {
					try {
						fileWriter.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
		}
	}
}
