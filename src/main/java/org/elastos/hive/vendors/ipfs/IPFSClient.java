/*
 * Copyright (c) 2019 Elastos Foundation
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package org.elastos.hive.vendors.ipfs;

import org.elastos.hive.Authenticator;
import org.elastos.hive.Callback;
import org.elastos.hive.Client;
import org.elastos.hive.Drive;
import org.elastos.hive.DriveType;
import org.elastos.hive.HiveException;
import org.elastos.hive.IPFSEntry;
import org.elastos.hive.NullCallback;
import org.elastos.hive.Void;
import org.elastos.hive.utils.CacheHelper;
import org.elastos.hive.vendors.ipfs.network.model.UIDResponse;
import org.elastos.hive.vendors.ipfs.network.IPFSApi;
import org.elastos.hive.vendors.connection.BaseServiceUtil;
import org.elastos.hive.vendors.connection.Model.BaseServiceConfig;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Iterator;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import retrofit2.Call;
import retrofit2.Response;

public final class IPFSClient extends Client {
	private static Client clientInstance;
	private static IPFSRpcHelper rpcHelper;
	private Client.Info clientInfo;
	private static String keystorePath;

	private IPFSClient(IPFSParameter parameter) {
		rpcHelper = new IPFSRpcHelper(parameter.getAuthEntry());
		keystorePath = parameter.getKeyStorePath();
		CacheHelper.initialize(parameter.getKeyStorePath());
	}

	public static Client createInstance(IPFSParameter parameter) throws HiveException {
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

				File ipfsConfig = new File(dataFile, IPFSRpcHelper.CONFIG);
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
		CompletableFuture<Void> future = rpcHelper.loginAsync(authenticator);

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
		CompletableFuture<Void> future = rpcHelper.logoutAsync();

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
				.thenCompose(padding -> getInfo(padding, callback));
	}

	private CompletableFuture<Client.Info> getInfo(Void padding, Callback<Client.Info> callback) {
		CompletableFuture<Client.Info> future = new CompletableFuture<Client.Info>();

		if (callback == null)
			callback = new NullCallback<Client.Info>();

		getStat(future,callback,rpcHelper.getBaseUrl(),
				getId(),"/" , IPFSConstance.Type.GET_INFO);

		return future;
	}

	@Override
	public CompletableFuture<Drive> getDefaultDrive() {
		return getDefaultDrive(new NullCallback<Drive>());
	}

	@Override
	public CompletableFuture<Drive> getDefaultDrive(Callback<Drive> callback) {
		return rpcHelper.checkExpired()
				.thenCompose(padding -> getDefaultDrive(padding, callback));
	}

	private CompletableFuture<Drive> getDefaultDrive(Void padding, Callback<Drive> callback) {
		CompletableFuture<Drive> future = new CompletableFuture<Drive>();

		if (callback == null)
			callback = new NullCallback<Drive>();

		getStat(future,callback,rpcHelper.getBaseUrl(),
				getId(),"/" , IPFSConstance.Type.GET_DEFAULT_DRIVE);

		return future;
	}

	private static String getNewUid() throws HiveException {
		String[] addrs = rpcHelper.getIpfsEntry().getRcpAddrs();
		if (addrs != null) {
			for (int i = 0; i < addrs.length; i++) {
				try {
					BaseServiceConfig config = new BaseServiceConfig.Builder().build();
					String url = String.format(IPFSConstance.URLFORMAT, addrs[i]);
					IPFSApi ipfsApi = BaseServiceUtil.createService(IPFSApi.class , url, config);
					Call call = ipfsApi.getNewUid();
					Response response = call.execute();
					if (response.code() == 200){
						rpcHelper.setValidAddress(addrs[i]);
						UIDResponse uidResponse = (UIDResponse) response.body();
						return uidResponse.getUID();
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
			File ipfsConfig = new File(keystorePath, IPFSRpcHelper.CONFIG);
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
			config.put(IPFSRpcHelper.LASTUID, uid);

			//and save in the uid array.
			String uids = IPFSRpcHelper.UIDS;
			JSONArray uidArray = null;
			if (!config.has(uids)) {
				uidArray = new JSONArray();
				JSONObject newUid = new JSONObject();
				newUid.put(IPFSConstance.UID, uid);
				uidArray.put(newUid);
				config.put(uids, uidArray);
			}
			else {
				uidArray = config.getJSONArray(uids);
				Iterator<Object> values = uidArray.iterator();
				boolean has = false;
				while (values.hasNext()) {
					JSONObject json = (JSONObject) values.next();
					if (uid.equals(json.getString(IPFSConstance.UID))) {
						has = true;
						break;
					}
				}

				if (!has) {
					JSONObject newUid = new JSONObject();
					newUid.put(IPFSConstance.UID, uid);
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

	private void getStat(CompletableFuture future , Callback callback , String url ,
						 String uid , String path , IPFSConstance.Type type){
		try {
			BaseServiceConfig config = new BaseServiceConfig.Builder().build();
			IPFSApi ipfsApi = BaseServiceUtil.createService(IPFSApi.class , url , config);
			Call call = ipfsApi.getStat(uid , path);
			call.enqueue(new IPFSClientCallback(future , callback , type));
		} catch (Exception ex) {
			HiveException e = new HiveException(ex.getMessage());
			callback.onError(e);
			future.completeExceptionally(e);
		}
	}

	private static String getUid() {
		BufferedReader bufferedReader = null;
		try {
			File ipfsConfig = new File(keystorePath, IPFSRpcHelper.CONFIG);
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
			if (!config.has(IPFSRpcHelper.LASTUID)) {
				return null;
			}
			return config.getString(IPFSRpcHelper.LASTUID);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
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

	private class IPFSClientCallback implements retrofit2.Callback{
		CompletableFuture future;
		Callback callback;
		IPFSConstance.Type type;

		public IPFSClientCallback(CompletableFuture future , Callback callback , IPFSConstance.Type type) {
			this.future = future;
			this.callback = callback;
			this.type = type;
		}

		@Override
		public void onResponse(Call call, Response response) {
			if (response.code() != 200) {
				HiveException ex = new HiveException("Server Error: " + response.message());
				if (callback!=null){
					this.callback.onError(ex);
				}

				if (future!=null){
					future.completeExceptionally(ex);
				}
				return;
			}

			switch (type){
				case GET_INFO:
					HashMap<String, String> attrs = new HashMap<String, String>();
					attrs.put(Client.Info.userId, rpcHelper.getIpfsEntry().getUid());
					Client.Info info = new Client.Info(attrs);
					clientInfo = info;
					this.callback.onSuccess(info);
					future.complete(info);
					break ;
				case GET_DEFAULT_DRIVE:
					HashMap<String, String> driveAttrs = new HashMap<>();
					driveAttrs.put(Drive.Info.driveId, rpcHelper.getIpfsEntry().getUid()); // TODO;
					// TODO;
					Drive.Info driveInfo = new Drive.Info(driveAttrs);
					IPFSDrive drive = new IPFSDrive(driveInfo, rpcHelper);
					this.callback.onSuccess(drive);
					future.complete(drive);
					break;
			}
		}

		@Override
		public void onFailure(Call call, Throwable t) {
			HiveException e = new HiveException(t.getMessage());
			this.callback.onError(e);
			future.completeExceptionally(e);
		}
	}

}
