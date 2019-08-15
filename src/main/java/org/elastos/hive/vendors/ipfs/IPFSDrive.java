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

import org.elastos.hive.Callback;
import org.elastos.hive.Directory;
import org.elastos.hive.Drive;
import org.elastos.hive.DriveType;
import org.elastos.hive.File;
import org.elastos.hive.HiveException;
import org.elastos.hive.ItemInfo;
import org.elastos.hive.NullCallback;
import org.elastos.hive.vendors.connection.ConnectionManager;
import org.elastos.hive.vendors.ipfs.network.model.StatResponse;

import java.net.SocketTimeoutException;
import java.util.HashMap;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

import retrofit2.Call;
import retrofit2.Response;

final class IPFSDrive extends Drive{
	private volatile Drive.Info driveInfo;
	private IPFSRpcHelper rpcHelper;

	IPFSDrive(Drive.Info driveInfo, IPFSRpcHelper rpcHelper) {
		this.driveInfo = driveInfo;
		this.rpcHelper = rpcHelper;
	}

	@Override
	public DriveType getType() {
		return DriveType.hiveIpfs;
	}

	@Override
	public String getId() {
		return driveInfo.get(Drive.Info.driveId);
	}

	@Override
	public Info getLastInfo() {
		return driveInfo;
	}

	@Override
	public CompletableFuture<Info> getInfo() {
		return getInfo(new NullCallback<Info>());
	}

	@Override
	public CompletableFuture<Info> getInfo(Callback<Info> callback) {
		return rpcHelper.checkExpiredNew(callback)
				.thenCompose(value -> getInfo(value));
	}

	private CompletableFuture<Info> getInfo(PackValue value) {
		CompletableFuture<Info> future = new CompletableFuture<Info>();

		Callback<Info> callback = (Callback<Info>) value.getCallback();

		if (value.getException() != null) {
			callback.onError(value.getException());
			future.completeExceptionally(value.getException());
			return future;
		}

		createConnection(future , callback , rpcHelper.getBaseUrl(),
				getId(),"/" , null , IPFSConstance.Type.GET_INFO);
		return future;
	}

	@Override
	public CompletableFuture<Directory> getRootDir() {
		return getRootDir(new NullCallback<Directory>());
	}

	@Override
	public CompletableFuture<Directory> getRootDir(Callback<Directory> callback) {
		return getDirectory("/", callback);
	}

	@Override
	public CompletableFuture<Directory> createDirectory(String path) {
		return createDirectory(path, new NullCallback<Directory>());
	}

	@Override
	public CompletableFuture<Directory> createDirectory(String path, Callback<Directory> callback) {
		return rpcHelper.checkExpiredNew(callback)
					.thenCompose(value -> createDirectory(value, path))
					.thenCompose(value -> rpcHelper.getRootHash(value))
					.thenCompose(value -> rpcHelper.publishHash(value))
					.thenCompose(value -> rpcHelper.invokeDirectoryCallback(value));
	}

	private CompletableFuture<PackValue> createDirectory(PackValue value, String path) {
		CompletableFuture<PackValue> future = new CompletableFuture<PackValue>();

		Callback<Directory> callback = (Callback<Directory>) value.getCallback();

		if (value.getException() != null) {
			callback.onError(value.getException());
			future.completeExceptionally(value.getException());
			return future;
		}

		if (path == null || path.isEmpty()) {
			HiveException e = new HiveException("Empty path name");
			callback.onError(e);
			future.completeExceptionally(e);
			return future;
		}

		if (!path.startsWith("/")) {
			HiveException e = new HiveException("Path name must be a abosulte path");
			callback.onError(e);
			future.completeExceptionally(e);
			return future;
		}

		createConnectionForResult(future,value,rpcHelper.getBaseUrl(),
				rpcHelper.getIpfsEntry().getUid(),path,path,IPFSConstance.Type.MKDIR);

		return future;
	}

	@Override
	public CompletableFuture<Directory> getDirectory(String path) {
		return getDirectory(path, new NullCallback<Directory>());
	}

	@Override
	public CompletableFuture<Directory> getDirectory(String path, Callback<Directory> callback) {
		return rpcHelper.checkExpiredNew(callback)
					.thenCompose(value -> getDirectory(value, path));
	}

	private CompletableFuture<Directory> getDirectory(PackValue value, String path) {
		CompletableFuture<Directory> future = new CompletableFuture<Directory>();

		Callback<Directory> callback = (Callback<Directory>) value.getCallback();

		if (value.getException() != null) {
			callback.onError(value.getException());
			future.completeExceptionally(value.getException());
			return future;
		}

		if (path == null || path.isEmpty()) {
			HiveException e = new HiveException("Empty path name");
			callback.onError(e);
			future.completeExceptionally(e);
			return future;
		}

		if (!path.startsWith("/")) {
			HiveException e = new HiveException("Path name must be a abosulte path");
			callback.onError(e);
			future.completeExceptionally(e);
			return future;
		}
		createConnection(future,callback,rpcHelper.getBaseUrl() ,
				getId() , path , path , IPFSConstance.Type.GET_DIR);
		return future;
	}

	@Override
	public CompletableFuture<File> createFile(String path) {
		return createFile(path, new NullCallback<File>());
	}

	@Override
	public CompletableFuture<File> createFile(String path, Callback<File> callback) {
		return rpcHelper.checkExpiredNew(callback)
				.thenCompose(value -> createFile(value, path))
				.thenCompose(value -> rpcHelper.getRootHash(value))
				.thenCompose(value -> rpcHelper.publishHash(value))
				.thenCompose(value -> rpcHelper.invokeFileCallback(value));
	}

	private CompletableFuture<PackValue> createFile(PackValue value, String path) {
		CompletableFuture<PackValue> future = new CompletableFuture<PackValue>();

		Callback<File> callback = (Callback<File>) value.getCallback();

		if (value.getException() != null) {
			callback.onError(value.getException());
			future.completeExceptionally(value.getException());
			return future;
		}

		if (path == null || path.isEmpty()) {
			HiveException e = new HiveException("Empty path name");
			callback.onError(e);
			future.completeExceptionally(e);
			return future;
		}

		if (!path.startsWith("/")) {
			HiveException e = new HiveException("Path name must be a abosulte path");
			callback.onError(e);
			future.completeExceptionally(e);
			return future;
		}
		createConnectionForResult(future,value,rpcHelper.getBaseUrl() ,
				rpcHelper.getIpfsEntry().getUid(),path , path,IPFSConstance.Type.CREATE_FILE);
		return future;
	}

	@Override
	public CompletableFuture<File> getFile(String path) {
		return getFile(path, new NullCallback<File>());
	}

	@Override
	public CompletableFuture<File> getFile(String path, Callback<File> callback) {
		return rpcHelper.checkExpiredNew(callback)
					.thenCompose(value -> getFile(value, path));
	}

	private CompletableFuture<File> getFile(PackValue value, String path) {
		CompletableFuture<File> future = new CompletableFuture<File>();

		Callback<File> callback = (Callback<File>) value.getCallback();

		if (value.getException() != null) {
			callback.onError(value.getException());
			future.completeExceptionally(value.getException());
			return future;
		}

		if (path == null || path.isEmpty()) {
			HiveException e = new HiveException("Empty path name");
			callback.onError(e);
			future.completeExceptionally(e);
			return future;
		}

		if (!path.startsWith("/")) {
			HiveException e = new HiveException("Need a absolute path to get a file.");
			callback.onError(e);
			future.completeExceptionally(e);
			return future;
		}

		createConnection(future,callback,rpcHelper.getBaseUrl() ,
				getId() , path , path , IPFSConstance.Type.GET_FILE);

		return future;
	}

	@Override
	public CompletableFuture<ItemInfo> getItemInfo(String path) {
		return getItemInfo(path, new NullCallback<ItemInfo>());
	}

	@Override
	public CompletableFuture<ItemInfo> getItemInfo(String path, Callback<ItemInfo> callback) {
		return rpcHelper.checkExpiredNew(callback)
				.thenCompose(value -> getItemInfo(value, path));
	}
	
	private CompletableFuture<ItemInfo> getItemInfo(PackValue value, String path) {
		CompletableFuture<ItemInfo> future = new CompletableFuture<ItemInfo>();

		Callback<ItemInfo> callback = (Callback<ItemInfo>) value.getCallback();

		if (value.getException() != null) {
			callback.onError(value.getException());
			future.completeExceptionally(value.getException());
			return future;
		}

		if (path == null || path.isEmpty()) {
			HiveException e = new HiveException("Empty path name");
			callback.onError(e);
			future.completeExceptionally(e);
			return future;
		}

		if (!path.startsWith("/")) {
			HiveException e = new HiveException("Need a absolute path to get a file or directory's item info.");
			callback.onError(e);
			future.completeExceptionally(e);
			return future;
		}

		createConnection(future,callback,rpcHelper.getBaseUrl() ,
				getId() , path , path , IPFSConstance.Type.GET_ITEMINFO);

		return future;
	}

	private void createConnectionForResult(CompletableFuture future , PackValue value , String url ,
										   String uid , String path , String pathName ,IPFSConstance.Type type){
		try {
			Call call = null;
			switch (type) {
				case CREATE_FILE:
					String contentType = String.format("multipart/form-data; boundary=%s", UUID.randomUUID().toString());
					call = ConnectionManager.getIPFSApi()
							.createFile(contentType,uid, path, true);
					break;
				case MKDIR:
					call = ConnectionManager.getIPFSApi()
							.mkdir(uid,path,"false");
					break;
			}

			if (call != null) {
				call.enqueue(new IPFSDriveForResultCallback(future, value, pathName, type));
			}
		} catch (Exception ex) {
				HiveException e = new HiveException(ex.getMessage());
				value.getCallback().onError(e);
				future.completeExceptionally(e);
		}
	}

	private void createConnection(CompletableFuture future , Callback callback ,String url ,
								  String uid , String path , String pathName , IPFSConstance.Type type){
		try {
			Call call = null ;
			switch (type){
				case GET_INFO:
				case GET_DIR:
				case GET_FILE:
				case GET_ITEMINFO:
					call = ConnectionManager.getIPFSApi()
							.getStat(uid , path);
					break;
			}

			if (call != null){
				call.enqueue(new IPFSDriveCallback(future, callback, pathName, type));
			}
		} catch (Exception ex) {
			HiveException e = new HiveException(ex.getMessage());
			callback.onError(e);
			future.completeExceptionally(e);
		}
	}

	private class IPFSDriveForResultCallback implements retrofit2.Callback{
		private final String pathName ;
		private final CompletableFuture future;
		private final PackValue value;
		private final IPFSConstance.Type type ;

		IPFSDriveForResultCallback(CompletableFuture future , PackValue value , String pathName , IPFSConstance.Type type){
			this.future = future ;
			this.value = value ;
			this.pathName = pathName ;
			this.type = type ;
		}

		@Override
		public void onResponse(Call call, Response response) {
			if (response.code() != 200) {
				HiveException e = new HiveException("Server Error: " + response.message());
				value.getCallback().onError(e);
				future.completeExceptionally(e);
				return;
			}

			switch (type){
				case CREATE_FILE: {
					HashMap<String, String> fileAttrs = new HashMap<>();
					fileAttrs.put(File.Info.itemId, getId());
					fileAttrs.put(File.Info.size, "0");

					int LastPos = pathName.lastIndexOf("/");
					String name = pathName.substring(LastPos + 1);
					fileAttrs.put(File.Info.name, name);

					File.Info fileInfo = new File.Info(fileAttrs);
					IPFSFile file = new IPFSFile(pathName, fileInfo, rpcHelper);

					value.setValue(file);
					future.complete(value);
					break;
				}
				case MKDIR: {
					HashMap<String, String> dirAttrs = new HashMap<>();
					dirAttrs.put(Directory.Info.itemId, getId());
					dirAttrs.put(Directory.Info.childCount, "0");

					int LastPos = pathName.lastIndexOf("/");
					String name = pathName.substring(LastPos + 1);
					dirAttrs.put(File.Info.name, name);

					Directory.Info dirInfo = new Directory.Info(dirAttrs);
					IPFSDirectory directory = new IPFSDirectory(pathName, dirInfo, rpcHelper);

					value.setValue(directory);
					future.complete(value);
					break;
				}
			}
		}

		@Override
		public void onFailure(Call call, Throwable t) {
			if (t instanceof SocketTimeoutException) {
				rpcHelper.setStatus(false);
			}

			HiveException e = new HiveException(t.getMessage());
			value.getCallback().onError(e);
			future.completeExceptionally(e);
		}
	}

	private class IPFSDriveCallback implements retrofit2.Callback{
		private final String pathName ;
		private final CompletableFuture future;
		private final Callback callback;
		private final IPFSConstance.Type type;

		IPFSDriveCallback(CompletableFuture future , Callback callback , String pathName , IPFSConstance.Type type) {
			this.future = future;
			this.callback = callback;
			this.pathName = pathName ;
			this.type = type;
		}

		@Override
		public void onResponse(Call call, Response response) {
			if (response.code() != 200) {
				HiveException e = new HiveException("Server Error: " + response.message());
				this.callback.onError(e);
				future.completeExceptionally(e);
				return;
			}

			switch (type) {
				case GET_INFO: {
					HashMap<String, String> attrs = new HashMap<>();
					attrs.put(Drive.Info.driveId, getId());
					driveInfo = new Drive.Info(attrs);

					this.callback.onSuccess(driveInfo);
					future.complete(driveInfo);
					break;
				}
				case GET_FILE: {
					StatResponse fileStatResponse = (StatResponse) response.body();
//					if (!rpcHelper.isFile(fileStatResponse.getType())) {
//						HiveException e = new HiveException("This is not a file");
//						callback.onError(e);
//						future.completeExceptionally(e);
//						return;
//					}

					HashMap<String, String> fileAttrs = new HashMap<>();
					fileAttrs.put(File.Info.itemId, getId());
					fileAttrs.put(File.Info.size, Integer.toString(fileStatResponse.getSize()));

					int LastPos = pathName.lastIndexOf("/");
					String name = pathName.substring(LastPos + 1);
					fileAttrs.put(File.Info.name, name);

					File.Info fileInfo = new File.Info(fileAttrs);
					IPFSFile file = new IPFSFile(pathName, fileInfo, rpcHelper);
					callback.onSuccess(file);
					future.complete(file);
					break;
				}
				case GET_DIR: {
					StatResponse dirStatResponse = (StatResponse) response.body();
					if (!rpcHelper.isFolder(dirStatResponse.getType())) {
						HiveException e = new HiveException("This is not a directory");
						callback.onError(e);
						future.completeExceptionally(e);
						return;
					}

					HashMap<String, String> dirAttrs = new HashMap<>();
					dirAttrs.put(Directory.Info.itemId, getId());

					if (pathName.equals("/")) {
						dirAttrs.put(Directory.Info.name, "/");
					}
					else {
						int LastPos = pathName.lastIndexOf("/");
						String name = pathName.substring(LastPos + 1);
						dirAttrs.put(Directory.Info.name, name);
					}

					dirAttrs.put(Directory.Info.childCount, Integer.toString(dirStatResponse.getBlocks()));

					Directory.Info dirInfo = new Directory.Info(dirAttrs);
					IPFSDirectory directory = new IPFSDirectory(pathName, dirInfo, rpcHelper);
					callback.onSuccess(directory);
					future.complete(directory);
					break;
				}
				case GET_ITEMINFO: {
					StatResponse itemStatResponse = (StatResponse) response.body();
					HashMap<String, String> itemAttrs = new HashMap<>();
					itemAttrs.put(ItemInfo.itemId, getId());

					itemAttrs.put(ItemInfo.type, itemStatResponse.getType());
					if (rpcHelper.isFile(itemStatResponse.getType())) {
						itemAttrs.put(ItemInfo.size, Integer.toString(itemStatResponse.getSize()));
					}
					else {
						itemAttrs.put(ItemInfo.size, "0");
					}

					int LastPos = this.pathName.lastIndexOf("/");
					String name = this.pathName.substring(LastPos + 1);
					itemAttrs.put(ItemInfo.name, name);

					ItemInfo info = new ItemInfo(itemAttrs);
					callback.onSuccess(info);
					future.complete(info);
					break;
				}

			}
		}

		@Override
		public void onFailure(Call call, Throwable t) {
			if (t instanceof SocketTimeoutException) {
				rpcHelper.setStatus(false);
			}
			
			HiveException e = new HiveException(t.getMessage());
			this.callback.onError(e);
			future.completeExceptionally(e);
		}
	}
}
