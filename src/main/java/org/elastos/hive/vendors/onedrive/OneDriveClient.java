package org.elastos.hive.vendors.onedrive;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import org.elastos.hive.Authenticator;
import org.elastos.hive.Callback;
import org.elastos.hive.Client;
import org.elastos.hive.Drive;
import org.elastos.hive.DriveType;
import org.elastos.hive.HiveException;
import org.elastos.hive.NullCallback;
import org.elastos.hive.UnirestAsyncCallback;
import org.elastos.hive.Void;
import org.json.JSONObject;

import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;

public final class OneDriveClient extends Client {
	private static Client clientInstance;

	private final OneDriveAuthHelper authHelper;
	private Client.Info clientInfo;
	private String userId;
	private static String keystorePath;

	private OneDriveClient(OneDriveParameter parameter) {
		keystorePath = parameter.getKeyStorePath();
		this.authHelper = new OneDriveAuthHelper(parameter.getAuthEntry(), keystorePath);
	}

	public static Client createInstance(OneDriveParameter parameter) throws HiveException {
		if (clientInstance == null) {
			clientInstance = new OneDriveClient(parameter);

			try {
				if (keystorePath == null)
					throw new HiveException("Please input an invalid path to store the IPFS data.");

				File dataFile = new File(keystorePath);
				if (!dataFile.exists()) {
					dataFile.mkdirs();
				}

				//config file
				File ipfsConfig = new File(dataFile, OneDriveUtils.CONFIG);
				if (!ipfsConfig.exists()) {
					ipfsConfig.createNewFile();
				}

				//tmp folder
				File tmpFolder = new File(dataFile, OneDriveUtils.TMP);
				if (!tmpFolder.exists()) {
					tmpFolder.mkdir();
				}
			} catch (Exception e) {
				throw new HiveException(e.getMessage());
			}
		}

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
		//load the local data, if invalid, invoke the http interfaces to login.
		if (hasValidLocalData()) {
			return;
		}

		CompletableFuture<Void> future = authHelper.loginAsync(authenticator);

		try {
			future.get();
		} catch (InterruptedException e) {
			throw new HiveException(e.getMessage());
		} catch (ExecutionException e) {
			throw new HiveException(e.getMessage());
		}
	}

	private boolean hasValidLocalData() {
		BufferedReader bufferedReader = null;
		try {
			File ipfsConfig = new File(keystorePath, OneDriveUtils.CONFIG);
			InputStreamReader reader = new InputStreamReader(new FileInputStream(ipfsConfig));
			bufferedReader = new BufferedReader(reader);
			String line;
			String content = "";
			while ((line = bufferedReader.readLine()) != null) {
				content += line;
			}

			if (content.isEmpty()) {
				return false;
			}

			//get the access_token, refresh_token and expires_at from the config.
			JSONObject config = new JSONObject(content);
			if (config.has(OneDriveUtils.RefreshToken) && config.has(OneDriveUtils.AccessToken)
					&& config.has(OneDriveUtils.ExpiresAt)) {
				//Check the expire time.
				long current = System.currentTimeMillis() / 1000;
				if (config.getLong(OneDriveUtils.ExpiresAt) > current) {
					authHelper.updateAuthToken(config.getString(OneDriveUtils.RefreshToken), 
											   config.getString(OneDriveUtils.AccessToken), 
											   config.getLong(OneDriveUtils.ExpiresAt));
					return true;
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		finally {
			if (bufferedReader != null) {
				try {
					bufferedReader.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}

		return false;
	}

	@Override
	public synchronized void logout() throws HiveException {
		CompletableFuture<Void> future = authHelper.logoutAsync();

		try {
			//clear the access_token, refresh_token and expires_at in the config.
			clearTokenInfo();

			future.get();
		} catch (InterruptedException e) {
			throw new HiveException(e.getMessage());
		} catch (ExecutionException e) {
			throw new HiveException(e.getMessage());
		}
	}
	
	private void clearTokenInfo() {
		BufferedReader bufferedReader = null;
		BufferedWriter writer = null;
		try {
			File ipfsConfig = new File(keystorePath, OneDriveUtils.CONFIG);
			InputStreamReader reader = new InputStreamReader(new FileInputStream(ipfsConfig));
			bufferedReader = new BufferedReader(reader);
			String line;
			String content = "";
			while ((line = bufferedReader.readLine()) != null) {
				content += line;
			}

			if (content.isEmpty()) {
				return;
			}

			JSONObject config = new JSONObject(content);
			//clear the access_token, refresh_token and expires_at
			config.remove(OneDriveUtils.RefreshToken);
			config.remove(OneDriveUtils.AccessToken);
			config.remove(OneDriveUtils.ExpiresAt);

			writer = new BufferedWriter(new FileWriter(ipfsConfig));
			writer.write(config.toString());
		} catch (IOException e) {
			e.printStackTrace();
		}
		finally {
			if (bufferedReader != null) {
				try {
					bufferedReader.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}

			if (writer != null) {
				try {
					writer.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
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
			try {
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

				JSONObject jsonObject = response.getBody().getObject();
				JSONObject userObject = jsonObject.getJSONObject("owner").getJSONObject("user");
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
			} catch (Exception ex) {
				HiveException e = new HiveException(ex.getMessage());
				this.callback.onError(e);
				future.completeExceptionally(e);
			}
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

			JSONObject jsonObject = response.getBody().getObject();
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
}
