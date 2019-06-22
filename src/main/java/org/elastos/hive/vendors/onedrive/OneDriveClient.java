package org.elastos.hive.vendors.onedrive;

import org.elastos.hive.Authenticator;
import org.elastos.hive.Callback;
import org.elastos.hive.Client;
import org.elastos.hive.Drive;
import org.elastos.hive.DriveType;
import org.elastos.hive.HiveException;
import org.elastos.hive.NullCallback;
import org.elastos.hive.Persistent;
import org.elastos.hive.Void;
import org.elastos.hive.vendors.onedrive.Model.DriveResponse;
import org.elastos.hive.vendors.onedrive.network.Api;
import org.elastos.hive.vendors.onedrive.network.BaseServiceUtil;
import org.json.JSONObject;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import retrofit2.Call;
import retrofit2.Response;

public final class OneDriveClient extends Client {
	private static Client clientInstance;

	private final OneDriveAuthHelper authHelper;
	private Client.Info clientInfo;
	private String userId;
	private final Persistent persistent;

	private OneDriveClient(OneDriveParameter parameter) {
		persistent = new KeyStore(parameter.getKeyStorePath());
		this.authHelper = new OneDriveAuthHelper(parameter.getAuthEntry(), persistent);
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

		try {
			Api api = BaseServiceUtil.createService(Api.class, Constance.ONE_DRIVE_API_BASE_URL ,
					true ,true , authHelper.getToken());
			Call call = api.getInfo();
			call.enqueue(new DriveClientCallback(future , callback , Type.GET_INFO));
		} catch (Exception e) {
			e.printStackTrace();
		}

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

		try {
			Api api = BaseServiceUtil.createService(Api.class, Constance.ONE_DRIVE_API_BASE_URL ,
					true ,true , authHelper.getToken());
			Call call = api.getDrive();
			call.enqueue(new DriveClientCallback(future , callback , Type.GET_DEFAULT_DRIVE));
		} catch (Exception e) {
			e.printStackTrace();
		}

		return future;
	}

	private class DriveClientCallback implements retrofit2.Callback{
		CompletableFuture future ;
		Callback callback ;
		Type type ;

		public DriveClientCallback(CompletableFuture future , Callback callback , Type type) {
			this.future = future ;
			this.callback = callback ;
			this.type = type ;
		}

		@Override
		public void onResponse(Call call, Response response) {
			if (response.code() == 401) {
				authHelper.getToken().expired();
				HiveException e = new HiveException("Server Error: " + response.message());
				this.callback.onError(e);
				future.completeExceptionally(e);
				return;
			}
			if (response.code() != 200) {
				HiveException ex = new HiveException("Server Error: " + response.message());
				this.callback.onError(ex);
				future.completeExceptionally(ex);
				return;
			}

			switch (type){
				case GET_INFO:
					//if @call https://graph.microsoft.com/v1.0/me/
//					ClientResponse clientInfoResponse = (ClientResponse) response.body();
//					Client.Info info = new Client.Info(clientInfoResponse.getId());
//					info.setDisplayName(clientInfoResponse.getDisplayName());

					DriveResponse driveResponseForClient= (DriveResponse) response.body();
					HashMap<String, String> attrs = new HashMap<String, String>();
					attrs.put(Client.Info.userId, driveResponseForClient.getOwner().getUser().getId());
					attrs.put(Client.Info.name, driveResponseForClient.getOwner().getUser().getDisplayName());

					Client.Info info = new Client.Info(attrs);

					clientInfo = info;
					this.callback.onSuccess(info);
					future.complete(info);
					break ;
				case GET_DEFAULT_DRIVE:
					DriveResponse driveResponse= (DriveResponse) response.body();

					HashMap<String, String> driveAttrs = new HashMap<String, String>();
					driveAttrs.put(Client.Info.userId, driveResponse.getOwner().getUser().getId());
					driveAttrs.put(Client.Info.name, driveResponse.getOwner().getUser().getDisplayName());

					Drive.Info driveInfo = new Drive.Info(driveAttrs);
					OneDriveDrive drive = new OneDriveDrive(driveInfo , authHelper);

					this.callback.onSuccess(drive);
					future.complete(drive);

					break ;
			}
		}

		@Override
		public void onFailure(Call call, Throwable t) {
			HiveException e = new HiveException(t.getMessage());
			this.callback.onError(e);
			future.completeExceptionally(e);
		}
	}

	class KeyStore implements Persistent {
		private String storePath;

		KeyStore(String storePath) {
			this.storePath = String.format("%s/%s", storePath, OneDriveUtils.CONFIG);
		}

		@Override
		public JSONObject parseFrom() throws HiveException {
			FileReader reader = null;
			try {
				initialize();
				reader = new FileReader(storePath);
				char[] buf = new char[128];
				int len = 0;
				StringBuilder content = new StringBuilder();
				while ((len = reader.read(buf)) != -1) {
					content.append(new String(buf, 0, len));
				}
				
				if (content.length() > 0) {
					return new JSONObject(content.toString());
				}
			} catch (Exception e) {
				throw new HiveException(e.getMessage());
			}
			finally {
				if (reader != null) {
					try {
						reader.close();
					} catch (IOException e) {
						throw new HiveException(e.getMessage());
					}
				}
			}

			return null;
		}

		@Override
		public void upateContent(JSONObject conetnt) throws HiveException {
			FileWriter fileWriter = null;
			try {
				initialize();
				fileWriter = new FileWriter(storePath);
				fileWriter.write(conetnt.toString());
			} catch (Exception e) {
				throw new HiveException(e.getMessage());
			}
			finally {
				if (fileWriter != null) {
					try {
						fileWriter.close();
					} catch (IOException e) {
						throw new HiveException(e.getMessage());
					}
				}
			}
		}

		private void initialize() throws IOException {
			File config = new File(storePath);
			if (!config.exists()) {
				config.createNewFile();
			}
		}
	}

	private enum Type{
		GET_INFO, GET_DEFAULT_DRIVE
	}
}
