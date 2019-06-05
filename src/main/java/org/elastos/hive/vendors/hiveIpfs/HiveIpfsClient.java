package org.elastos.hive.vendors.hiveIpfs;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.util.concurrent.CompletableFuture;

import org.elastos.hive.Authenticator;
import org.elastos.hive.Callback;
import org.elastos.hive.Client;
import org.elastos.hive.Drive;
import org.elastos.hive.DriveType;
import org.elastos.hive.HiveException;
import org.elastos.hive.NullCallback;
import org.elastos.hive.UnirestAsyncCallback;

import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;

public final class HiveIpfsClient extends Client {
	private static Client clientInstance;
	private Client.Info clientInfo;
	private final HiveIpfsParameter parameter;

	private HiveIpfsClient(HiveIpfsParameter parameter) {
		this.parameter = parameter;
		
		if (parameter.getUid() != null) {
			//Store the uid
			storeUid(parameter.getUid());
		}
		else {
			String uid = getUid();
			if (uid != null) {
				parameter.setUid(uid);
			}
		}
	}

	public static Client createInstance(HiveIpfsParameter parameter) {
		if (clientInstance == null) {
			clientInstance = new HiveIpfsClient(parameter);
		}
		return clientInstance;
	}

	public static Client getInstance() {
		return clientInstance;
	}

	@Override
	public String getId() {
		return parameter.getUid();
	}

	@Override
	public DriveType getDriveType() {
		return DriveType.hiveIpfs;
	}

	@Override
	public synchronized void login(Authenticator authenticator) throws HiveException {
		try {
			String uid = parameter.getUid();
			if (uid == null) {
				uid = HiveIpfsUtils.getNewUid();
				parameter.setUid(uid);
				storeUid(uid);
			}

			String homeHash = HiveIpfsUtils.getHomeHash();
			Unirest.get(HiveIpfsUtils.BASEURL + "uid/login")
				.header(HiveIpfsUtils.CONTENTTYPE, HiveIpfsUtils.TYPE_Json)
				.queryString(HiveIpfsUtils.UID, uid)
				.queryString(HiveIpfsUtils.HASH, homeHash)
				.asJson();
		}
		catch (Exception e) {
			e.printStackTrace();
			throw new HiveException("Login failed");
		}
	}

	@Override
	public synchronized void logout() throws HiveException {
		//TODO
		parameter.setUid(null);
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
		CompletableFuture<Client.Info> future = new CompletableFuture<Client.Info>();

		if (callback == null)
			callback = new NullCallback<Client.Info>();

		String url = String.format("%s%s", HiveIpfsUtils.BASEURL, "files/stat");
		Unirest.get(url)
			.header(HiveIpfsUtils.CONTENTTYPE, HiveIpfsUtils.TYPE_Json)
			.queryString(HiveIpfsUtils.UID, parameter.getUid())
			.queryString(HiveIpfsUtils.PATH, "/")
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

		if (callback == null)
			callback = new NullCallback<Drive>();

		String url = String.format("%s%s", HiveIpfsUtils.BASEURL, "files/stat");
		Unirest.get(url)
			.header(HiveIpfsUtils.CONTENTTYPE, HiveIpfsUtils.TYPE_Json)
			.queryString(HiveIpfsUtils.UID, parameter.getUid())
			.queryString(HiveIpfsUtils.PATH, "/")
			.asJsonAsync(new GetDriveCallback(future, callback));

		return future;
	}

	private void storeUid(final String uid) {
		Properties properties = new Properties(); 
		FileOutputStream out = null;
		try {
			String configPath = HiveIpfsClient.class.getResource("/").getPath() + HiveIpfsUtils.CONFIG;
			//Create the property file.
			File prop = new File(configPath);
			if (!prop.exists()) {
				prop.createNewFile();
			}

			properties.setProperty(HiveIpfsUtils.UID, uid);
			out = new FileOutputStream(prop);
			properties.store(out, "store the user's uid");
		} catch (IOException e) {
			e.printStackTrace();
		}
		finally {
			if (out != null) {
				try {
					out.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}
	
	private String getUid() {
		Properties properties = new Properties(); 
		InputStream inputStream = null;

		try {
			String configPath = HiveIpfsClient.class.getResource("/").getPath() + HiveIpfsUtils.CONFIG;
			//Create the property file.
			File prop = new File(configPath);
			if (!prop.exists()) {
				prop.createNewFile();
			}

			inputStream = new FileInputStream(prop);
			properties.load(inputStream);
			return properties.getProperty(HiveIpfsUtils.UID);
		} catch (IOException e) {
			e.printStackTrace();
		}
		finally {
			if (inputStream != null) {
				try {
					inputStream.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		
		return null;
	}
	
	private class GetClientInfoCallback implements UnirestAsyncCallback<JsonNode> {
		private final CompletableFuture<Client.Info> future;
		private final Callback<Client.Info> callback;

		GetClientInfoCallback(CompletableFuture<Client.Info> future, Callback<Client.Info> callback) {
			this.future = future;
			this.callback = callback;
		}

		@Override
		public void cancelled() {}

		@Override
		public void completed(HttpResponse<JsonNode> response) {
			if (response.getStatus() != 200) {
				HiveException ex = new HiveException("Server Error: " + response.getStatusText());
				this.callback.onError(ex);
				future.completeExceptionally(ex);
				return;
			}

			clientInfo = new Client.Info(parameter.getUid());
			this.callback.onSuccess(clientInfo);
			future.complete(clientInfo);
		}

		@Override
		public void failed(UnirestException exception) {
			HiveException e = new HiveException(exception.getMessage());
			this.callback.onError(e);
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
				this.callback.onError(ex);
				future.completeExceptionally(ex);
				return;
			}

			HiveIpfsDrive drive = new HiveIpfsDrive(parameter.getUid());
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
