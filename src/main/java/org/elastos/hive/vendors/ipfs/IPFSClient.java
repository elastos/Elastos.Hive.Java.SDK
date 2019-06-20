package org.elastos.hive.vendors.ipfs;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Iterator;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import org.elastos.hive.Authenticator;
import org.elastos.hive.Callback;
import org.elastos.hive.Client;
import org.elastos.hive.Drive;
import org.elastos.hive.DriveType;
import org.elastos.hive.HiveException;
import org.elastos.hive.IPFSEntry;
import org.elastos.hive.NullCallback;
import org.elastos.hive.Status;
import org.elastos.hive.UnirestAsyncCallback;
import org.json.JSONArray;
import org.json.JSONObject;

import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;

public final class IPFSClient extends Client {
	private static Client clientInstance;
	private static IPFSRpcHelper rpcHelper;
	private Client.Info clientInfo;
	private static String keystorePath;

	private IPFSClient(IPFSParameter parameter) {
		rpcHelper = new IPFSRpcHelper(parameter.getAuthEntry());
		keystorePath = parameter.getKeyStorePath();
	}

	public static Client createInstance(IPFSParameter parameter) throws HiveException {
		try {
			if (clientInstance == null) {
				clientInstance = new IPFSClient(parameter);

				try {
					IPFSEntry entry = parameter.getAuthEntry();
					if (keystorePath == null)
						throw new HiveException("Please input an invalid path to store the IPFS data.");

					File dataFile = new File(keystorePath);
					if (!dataFile.exists()) {
						dataFile.mkdirs();
					}

					File ipfsConfig = new File(dataFile, IPFSUtils.CONFIG);
					if (!ipfsConfig.exists()) {
						ipfsConfig.createNewFile();
					}

					if (entry.getUid() != null) {
						//Store the uid
						storeUid(entry.getUid());
					}
					else {
						String uid = getUid();
						if (uid == null) {
							//uid/new a new uid.
							uid = getNewUid();
						}

						if (uid != null) {
							entry.setUid(uid);
							//Store the new uid
							storeUid(entry.getUid());
						}
					}
				} catch (Exception e) {
					throw new HiveException(e.getMessage());
				}
			}
		} catch (Exception e) {
			throw new HiveException(e.getMessage());
		}

		return clientInstance;
	}

	public static Client getInstance() {
		return clientInstance;
	}

	@Override
	public String getId() {
		return rpcHelper.getIpfsEntry().getUid();
	}

	@Override
	public DriveType getDriveType() {
		return DriveType.hiveIpfs;
	}

	@Override
	public synchronized void login(Authenticator authenticator) throws HiveException {
		CompletableFuture<Status> future = rpcHelper.loginAsync(authenticator);

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
		CompletableFuture<Status> future = rpcHelper.logoutAsync();

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
		return rpcHelper.checkExpired()
				.thenCompose(status -> getInfo(status, callback));
	}

	private CompletableFuture<Client.Info> getInfo(Status status, Callback<Client.Info> callback) {
		CompletableFuture<Client.Info> future = new CompletableFuture<Client.Info>();

		if (callback == null)
			callback = new NullCallback<Client.Info>();

		String url = String.format("%s%s", rpcHelper.getBaseUrl(), IPFSMethod.STAT);

		Unirest.get(url)
			.header(IPFSHttpHeader.ContentType, "application/json")
			.queryString("uid", getId())
			.queryString("path", "/")
			.asJsonAsync(new GetInfoCallback(future, callback));

		return future;
	}

	@Override
	public CompletableFuture<Drive> getDefaultDrive() {
		return getDefaultDrive(new NullCallback<Drive>());
	}

	@Override
	public CompletableFuture<Drive> getDefaultDrive(Callback<Drive> callback) {
		return rpcHelper.checkExpired()
				.thenCompose(status -> getDefaultDrive(status, callback));
	}

	private CompletableFuture<Drive> getDefaultDrive(Status status, Callback<Drive> callback) {
		CompletableFuture<Drive> future = new CompletableFuture<Drive>();

		if (callback == null)
			callback = new NullCallback<Drive>();

		String url = String.format("%s%s", rpcHelper.getBaseUrl(), IPFSMethod.STAT);

		Unirest.get(url)
			.header(IPFSHttpHeader.ContentType, "application/json")
			.queryString("uid", getId())
			.queryString("path", "/")
			.asJsonAsync(new GetDriveCallback(future, callback));

		return future;
	}

	private static String getNewUid() throws HiveException {
		String[] addrs = rpcHelper.getIpfsEntry().getRcpAddrs();
		if (addrs != null) {
			for (int i = 0; i < addrs.length; i++) {
				String url = String.format(IPFSURL.URLFORMAT, addrs[i]) + IPFSMethod.NEW;
				try {
					HttpResponse<JsonNode> json = Unirest.get(url)
							.header(IPFSURL.ContentType, IPFSURL.Json)
							.asJson();
					if (json.getStatus() == 200) {
						rpcHelper.setValidAddress(addrs[i]);
						return json.getBody().getObject().getString("UID");
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}

		throw new HiveException("The input addresses are all invalid.");
	}

	private static void storeUid(final String uid) {
		BufferedReader bufferedReader = null;
		BufferedWriter writer = null;
		try {
			File ipfsConfig = new File(keystorePath, IPFSUtils.CONFIG);
			InputStreamReader reader = new InputStreamReader(new FileInputStream(ipfsConfig));
			bufferedReader = new BufferedReader(reader);
			String line;
			String content = "";
			while ((line = bufferedReader.readLine()) != null) {
				content += line;
			}

			JSONObject config;
			if (!content.isEmpty()) {
				config = new JSONObject(content);
			}
			else {
				config = new JSONObject();
			}

			//save at "last_uid"
			config.put(IPFSUtils.LASTUID, uid);

			//and save in the uid array.
			String uids = IPFSUtils.UIDS;
			JSONArray uidArray = null;
			if (!config.has(uids)) {
				uidArray = new JSONArray();
				JSONObject newUid = new JSONObject();
				newUid.put(IPFSURL.UID, uid);
				uidArray.put(newUid);
				config.put(uids, uidArray);
			}
			else {
				uidArray = config.getJSONArray(uids);
				Iterator<Object> values = uidArray.iterator();
				boolean has = false;
				while (values.hasNext()) {
					JSONObject json = (JSONObject) values.next();
					if (uid.equals(json.getString(IPFSURL.UID))) {
						has = true;
						break;
					}
				}

				if (!has) {
					JSONObject newUid = new JSONObject();
					newUid.put(IPFSURL.UID, uid);
					uidArray.put(newUid);
					config.put(uids, uidArray);
				}
			}

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

	private static String getUid() {
		BufferedReader bufferedReader = null;
		try {
			File ipfsConfig = new File(keystorePath, IPFSUtils.CONFIG);
			InputStreamReader reader = new InputStreamReader(new FileInputStream(ipfsConfig));
			bufferedReader = new BufferedReader(reader);
			String line;
			String content = "";
			while ((line = bufferedReader.readLine()) != null) {
				content += line;
			}

			if (content.isEmpty()) {
				return null;
			}

			JSONObject config = new JSONObject(content);
			if (!config.has(IPFSUtils.LASTUID)) {
				return null;
			}
			return config.getString(IPFSUtils.LASTUID);
		} catch (Exception e) {
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

		return null;
	}

	private class GetInfoCallback implements UnirestAsyncCallback<JsonNode> {
		private final CompletableFuture<Client.Info> future;
		private final Callback<Client.Info> callback;

		GetInfoCallback(CompletableFuture<Client.Info> future, Callback<Client.Info> callback) {
			this.future = future;
			this.callback = callback;
		}

		@Override
		public void cancelled() {}

		@Override
		public void completed(HttpResponse<JsonNode> response) {
			if (response.getStatus() != 200) {
				HiveException e = new HiveException("Server Error: " + response.getStatusText());
				this.callback.onError(e);
				future.completeExceptionally(e);
				return;
			}

			clientInfo = new Client.Info(rpcHelper.getIpfsEntry().getUid());
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
				HiveException e = new HiveException("Server Error: " + response.getStatusText());
				this.callback.onError(e);
				future.completeExceptionally(e);
				return;
			}

			Drive.Info info = new Drive.Info(rpcHelper.getIpfsEntry().getUid());
			IPFSDrive drive = new IPFSDrive(info, rpcHelper);
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
