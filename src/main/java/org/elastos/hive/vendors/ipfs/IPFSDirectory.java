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
import org.elastos.hive.Children;
import org.elastos.hive.Directory;
import org.elastos.hive.File;
import org.elastos.hive.HiveException;
import org.elastos.hive.ItemInfo;
import org.elastos.hive.NullCallback;
import org.elastos.hive.Void;
import org.elastos.hive.vendors.connection.BaseServiceUtil;
import org.elastos.hive.vendors.connection.Model.BaseServiceConfig;
import org.elastos.hive.vendors.connection.Model.HeaderConfig;
import org.elastos.hive.vendors.ipfs.network.IPFSApi;
import org.elastos.hive.vendors.ipfs.network.model.ListChildResponse;
import org.elastos.hive.vendors.ipfs.network.model.StatResponse;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

import retrofit2.Call;
import retrofit2.Response;

class IPFSDirectory extends Directory  {
	private String pathName;
	private volatile Directory.Info dirInfo;
	private IPFSRpcHelper rpcHelper;

	IPFSDirectory(String pathName, Directory.Info dirInfo, IPFSRpcHelper rpcHelper) {
		this.pathName = pathName;
		this.dirInfo = dirInfo;
		this.rpcHelper = rpcHelper;
	}

	@Override
	public String getId() {
		return dirInfo.get(Directory.Info.itemId);
	}

	@Override
	public Info getLastInfo() {
		return dirInfo;
	}

	@Override
	public CompletableFuture<Info> getInfo() {
		return getInfo(new NullCallback<Directory.Info>());
	}

	@Override
	public CompletableFuture<Info> getInfo(Callback<Info> callback) {
		return rpcHelper.checkExpiredNew()
				.thenCompose(value -> getInfo(value, callback));
	}

	private CompletableFuture<Info> getInfo(PackValue value, Callback<Info> callback) {
		CompletableFuture<Directory.Info> future = new CompletableFuture<Directory.Info>();

		if (callback == null)
			callback = new NullCallback<Info>();

		if (value.getException() != null) {
			callback.onError(value.getException());
			future.completeExceptionally(value.getException());
			return future;
		}

		createConnection(future,callback,rpcHelper.getBaseUrl(),
				getId(),pathName,null,IPFSConstance.Type.GET_INFO);

		return future;
	}

	@Override
	public CompletableFuture<Directory> createDirectory(String path) {
		return  createDirectory(path, new NullCallback<Directory>());
	}

	@Override
	public CompletableFuture<Directory> createDirectory(String path, Callback<Directory> callback) {
		return rpcHelper.checkExpiredNew()
				.thenCompose(value -> createDirectory(value, path, callback))
				.thenCompose(value -> rpcHelper.getRootHash(value))
				.thenCompose(value -> rpcHelper.publishHash(value))
				.thenCompose(value -> rpcHelper.invokeDirectoryCallback(value));
	}

	private CompletableFuture<PackValue> createDirectory(PackValue value, String path, Callback<Directory> callback) {
		CompletableFuture<PackValue> future = new CompletableFuture<PackValue>();

		if (callback == null)
			callback = new NullCallback<Directory>();

		value.setCallback(callback);

		if (value.getException() != null) {
			future.completeExceptionally(value.getException());
			return future;
		}

		if (path == null || path.isEmpty()) {
			HiveException e = new HiveException("The path is invalid");
			value.setException(e);
			future.completeExceptionally(e);
			return future;
		}

		if (path.contains("/")) {
			HiveException e = new HiveException("Only need the path name");
			value.setException(e);
			future.completeExceptionally(e);
			return future;
		}

		String newPath = String.format("%s/%s", this.pathName, path);

		createConnectionForResult(future,value,rpcHelper.getBaseUrl(),
				rpcHelper.getIpfsEntry().getUid(),newPath,newPath,IPFSConstance.Type.MKDIR);
		return future;
	}

	@Override
	public CompletableFuture<Directory> getDirectory(String path) {
		return getDirectory(path, new NullCallback<Directory>());
	}

	@Override
	public CompletableFuture<Directory> getDirectory(String path, Callback<Directory> callback) {
		return rpcHelper.checkExpiredNew()
				.thenCompose(value -> getDirectory(value, path, callback));
	}

	private CompletableFuture<Directory> getDirectory(PackValue value, String path, Callback<Directory> callback) {
		CompletableFuture<Directory> future = new CompletableFuture<Directory>();

		if (callback == null)
			callback = new NullCallback<Directory>();

		if (value.getException() != null) {
			callback.onError(value.getException());
			future.completeExceptionally(value.getException());
			return future;
		}

		if (path == null || path.isEmpty()) {
			HiveException e = new HiveException("The path is invalid");
			callback.onError(e);
			future.completeExceptionally(e);
			return future;
		}

		if (path.contains("/")) {
			HiveException e = new HiveException("Only need the name of a directory.");
			callback.onError(e);
			future.completeExceptionally(e);
			return future;
		}

		String pathName = String.format("%s/%s", this.pathName, path);

		createConnection(future,callback,rpcHelper.getBaseUrl(),
				getId(),pathName,pathName,IPFSConstance.Type.GET_DIR);
		return future;
	}

	@Override
	public CompletableFuture<File> createFile(String path) {
		return createFile(path, new NullCallback<File>());
	}

	@Override
	public CompletableFuture<File> createFile(String path, Callback<File> callback) {
		return rpcHelper.checkExpiredNew()
				.thenCompose(value -> createFile(value, path, callback))
				.thenCompose(value -> rpcHelper.getRootHash(value))
				.thenCompose(value -> rpcHelper.publishHash(value))
				.thenCompose(value -> rpcHelper.invokeFileCallback(value));
	}

	private CompletableFuture<PackValue> createFile(PackValue value, String path, Callback<File> callback) {
		CompletableFuture<PackValue> future = new CompletableFuture<PackValue>();

		if (callback == null)
			callback = new NullCallback<File>();

		value.setCallback(callback);

		if (value.getException() != null) {
			future.completeExceptionally(value.getException());
			return future;
		}

		if (path == null || path.isEmpty()) {
			HiveException e = new HiveException("The path is invalid");
			value.setException(e);
			future.completeExceptionally(e);
			return future;
		}

		if (path.contains("/")) {
			HiveException e = new HiveException("Only need the name of a file.");
			value.setException(e);
			future.completeExceptionally(e);
			return future;
		}

		String pathName = String.format("%s/%s", this.pathName, path);

		createConnectionForResult(future, value,rpcHelper.getBaseUrl(),
				rpcHelper.getIpfsEntry().getUid(), pathName, pathName, IPFSConstance.Type.CREATE_FILE);

		return future;
	}

	@Override
	public CompletableFuture<File> getFile(String path) {
		return getFile(path, new NullCallback<File>());
	}

	@Override
	public CompletableFuture<File> getFile(String path, Callback<File> callback) {
		return rpcHelper.checkExpiredNew()
				.thenCompose(value -> getFile(value, path, callback));
	}

	private CompletableFuture<File> getFile(PackValue value, String path, Callback<File> callback) {
		CompletableFuture<File> future = new CompletableFuture<File>();

		if (callback == null)
			callback = new NullCallback<File>();

		if (value.getException() != null) {
			callback.onError(value.getException());
			future.completeExceptionally(value.getException());
			return future;
		}

		if (path == null || path.isEmpty()) {
			HiveException e = new HiveException("The path is invalid");
			callback.onError(e);
			future.completeExceptionally(e);
			return future;
		}

		if (path.contains("/")) {
			HiveException e = new HiveException("Only need the name of a file.");
			callback.onError(e);
			future.completeExceptionally(e);
			return future;
		}

		String pathName = String.format("%s/%s", this.pathName, path);

		createConnection(future,callback,rpcHelper.getBaseUrl(),
				getId(),pathName,pathName,IPFSConstance.Type.GET_FILE);

		return future;
	}

	@Override
	public String getPath() {
		return pathName;
	}

	@Override
	public String getParentPath() {
		if (pathName.equals("/"))
			return pathName;

		return pathName.substring(0, pathName.lastIndexOf("/") + 1);
	}

	@Override
	public CompletableFuture<Void> moveTo(String path) {
		return moveTo(path, new NullCallback<Void>());
	}

	@Override
	public CompletableFuture<Void> moveTo(String path, Callback<Void> callback) {
		return rpcHelper.checkExpiredNew()
				.thenCompose(value -> moveTo(value, path, callback))
				.thenCompose(value -> rpcHelper.getRootHash(value))
				.thenCompose(value -> rpcHelper.publishHash(value))
				.thenCompose(value -> rpcHelper.invokeVoidCallback(value));
	}

	private CompletableFuture<PackValue> moveTo(PackValue value, String path, Callback<Void> callback) {
		CompletableFuture<PackValue> future = new CompletableFuture<PackValue>();

		if (callback == null)
			callback = new NullCallback<Void>();

		value.setCallback(callback);

		if (value.getException() != null) {
			future.completeExceptionally(value.getException());
			return future;
		}

		if (path == null || path.isEmpty()) {
			HiveException e = new HiveException("The path is invalid");
			value.setException(e);
			future.completeExceptionally(e);
			return future;
		}

		if (!path.startsWith("/")) {
			HiveException e = new HiveException("Path name must be a abosulte path");
			value.setException(e);
			future.completeExceptionally(e);
			return future;
		}

		if (path.equals(this.pathName)) {
			HiveException e = new HiveException("Can't move to the oneself directory");
			value.setException(e);
			future.completeExceptionally(e);
			return future;
		}

		int LastPos = this.pathName.lastIndexOf("/");
		String name = this.pathName.substring(LastPos + 1);
		final String newPath = String.format("%s/%s", path, name);

		try {
			BaseServiceConfig config = new BaseServiceConfig.Builder().ignoreReturnBody(true).build();
			IPFSApi ipfsApi = BaseServiceUtil.createService(IPFSApi.class, rpcHelper.getBaseUrl(), config);
			Call call = ipfsApi.moveTo(rpcHelper.getIpfsEntry().getUid(), pathName, newPath);
			call.enqueue(new IPFSDirForResultCallback(future,value,newPath, IPFSConstance.Type.MOVE_TO));
		} catch (Exception e) {
			e.printStackTrace();
		}

		return future;
	}

	@Override
	public CompletableFuture<Void> copyTo(String path) {
		return copyTo(path, new NullCallback<Void>());
	}

	@Override
	public CompletableFuture<Void> copyTo(String path, Callback<Void> callback) {
		return rpcHelper.checkExpiredNew()
				.thenCompose(value -> rpcHelper.getPathHash(value, this.pathName))
				.thenCompose(value -> copyTo(value, path, callback))
				.thenCompose(value -> rpcHelper.getRootHash(value))
				.thenCompose(value -> rpcHelper.publishHash(value))
				.thenCompose(value -> rpcHelper.invokeVoidCallback(value));
	}

	private CompletableFuture<PackValue> copyTo(PackValue value, String path, Callback<Void> callback) {
		CompletableFuture<PackValue> future = new CompletableFuture<PackValue>();

		if (callback == null)
			callback = new NullCallback<Void>();

		value.setCallback(callback);

		if (value.getException() != null) {
			future.completeExceptionally(value.getException());
			return future;
		}

		if (path == null || path.isEmpty()) {
			HiveException e = new HiveException("The path is invalid");
			value.setException(e);
			future.completeExceptionally(e);
			return future;
		}

		if (!path.startsWith("/")) {
			HiveException e = new HiveException("Path name must be a abosulte path");
			value.setException(e);
			future.completeExceptionally(e);
			return future;
		}

		if (path.equals(this.pathName)) {
			HiveException e = new HiveException("Can't copy to the oneself directory");
			value.setException(e);
			future.completeExceptionally(e);
			return future;
		}

		String hash = value.getHash().getValue();
		if (hash == null || hash.isEmpty()) {
			HiveException e = new HiveException("The hash is invalid");
			future.completeExceptionally(e);
			return future;
		}

		int LastPos = this.pathName.lastIndexOf("/");
		String name = this.pathName.substring(LastPos + 1);
		final String newPath = String.format("%s/%s", path, name);

		try {
			BaseServiceConfig config = new BaseServiceConfig.Builder().ignoreReturnBody(true).build();
			IPFSApi ipfsApi = BaseServiceUtil.createService(IPFSApi.class, rpcHelper.getBaseUrl(), config);
			Call call = ipfsApi.copyTo(rpcHelper.getIpfsEntry().getUid(), IPFSConstance.PREFIX + hash, newPath);
			call.enqueue(new IPFSDirForResultCallback(future,value,null, IPFSConstance.Type.COPY_TO));
		} catch (Exception e) {
			e.printStackTrace();
		}

		return future;
	}

	@Override
	public CompletableFuture<Void> deleteItem() {
		return deleteItem(new NullCallback<Void>());
	}

	@Override
	public CompletableFuture<Void> deleteItem(Callback<Void> callback) {
		return rpcHelper.checkExpiredNew()
				.thenCompose(value -> deleteItem(value, callback))
				.thenCompose(value -> rpcHelper.getRootHash(value))
				.thenCompose(value -> rpcHelper.invokeVoidCallback(value));
	}

	private CompletableFuture<PackValue> deleteItem(PackValue value, Callback<Void> callback) {
		CompletableFuture<PackValue> future = new CompletableFuture<PackValue>();

		if (callback == null)
			callback = new NullCallback<Void>();

		value.setCallback(callback);

		if (value.getException() != null) {
			future.completeExceptionally(value.getException());
			return future;
		}

		if (pathName.equals("/")) {
			HiveException e = new HiveException("Can't delete the root.");
			value.setException(e);
			future.completeExceptionally(e);
			return future;
		}

		try {
			BaseServiceConfig config = new BaseServiceConfig.Builder().ignoreReturnBody(true).build();
			IPFSApi ipfsApi = BaseServiceUtil.createService(IPFSApi.class, rpcHelper.getBaseUrl(), config);
			Call call = ipfsApi.deleteItem(rpcHelper.getIpfsEntry().getUid(), pathName, "true");
			call.enqueue(new IPFSDirForResultCallback(future, value, null, IPFSConstance.Type.DELETE_ITEM));
		} catch (Exception e) {
			e.printStackTrace();
		}

		return future;
	}

	@Override
	public CompletableFuture<Children> getChildren() {
		return getChildren(new NullCallback<Children>());
	}

	@Override
	public CompletableFuture<Children> getChildren(Callback<Children> callback) {
		return rpcHelper.checkExpiredNew()
				.thenCompose(value -> getChildren(value, callback));
	}

	private CompletableFuture<Children> getChildren(PackValue value, Callback<Children> callback) {
		CompletableFuture<Children> future = new CompletableFuture<Children>();

		if (callback == null)
			callback = new NullCallback<Children>();

		if (value.getException() != null) {
			callback.onError(value.getException());
			future.completeExceptionally(value.getException());
			return future;
		}

		try {
			BaseServiceConfig config = new BaseServiceConfig.Builder().build();
			IPFSApi ipfsApi = BaseServiceUtil.createService(IPFSApi.class, rpcHelper.getBaseUrl(), config);
			Call call = ipfsApi.list(getId(), pathName);
			call.enqueue(new IPFSDirForResultCallback(future, value, null, IPFSConstance.Type.GET_CHILDREN));
		} catch (Exception ex) {
			HiveException e = new HiveException(ex.getMessage());
			callback.onError(e);
			future.completeExceptionally(e);
		}
		return future;
	}

	private void createConnectionForResult(CompletableFuture future , PackValue value , String url ,
										   String uid , String path , String pathName ,IPFSConstance.Type type){
		try {
			Call call = null;
			switch (type) {
				case CREATE_FILE:
					String contentType = String.format("multipart/form-data; boundary=%s", UUID.randomUUID().toString());
					HeaderConfig headerConfig = new HeaderConfig.Builder()
							.contentType(contentType)
							.build();
					BaseServiceConfig config = new BaseServiceConfig.Builder()
							.headerConfig(headerConfig)
							.ignoreReturnBody(true)
							.build();
					IPFSApi ipfsApi = BaseServiceUtil.createService(IPFSApi.class, url, config);
					call = ipfsApi.createFile(uid, path, true);
					break;
				case MKDIR:
					BaseServiceConfig mkdirConfig = new BaseServiceConfig.Builder()
							.ignoreReturnBody(true)
							.build();
					IPFSApi ipfsMkdirApi = BaseServiceUtil.createService(IPFSApi.class, url, mkdirConfig);
					call = ipfsMkdirApi.mkdir(uid,path,"false");
					break;
				case MOVE_TO:
					break;
				case COPY_TO:
					break;
				case DELETE_ITEM:
					break;
			}

			if (call != null) {
				call.enqueue(new IPFSDirForResultCallback(future, value, pathName, type));
			}
		} catch (Exception ex) {
			HiveException e = new HiveException(ex.getMessage());
			value.setException(e);
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
					BaseServiceConfig ipfsConfig = new BaseServiceConfig.Builder().build();
					IPFSApi ipfsStatApi = BaseServiceUtil.createService(IPFSApi.class , url , ipfsConfig);
					call = ipfsStatApi.getStat(uid , path);
					break;
				case LIST:
					break;
			}

			if (call!=null){
				call.enqueue(new IPFSDirCallback(future , callback , pathName , type));
			}
		} catch (Exception ex) {
			HiveException e = new HiveException(ex.getMessage());
			callback.onError(e);
			future.completeExceptionally(e);
		}
	}

	private class IPFSDirForResultCallback implements retrofit2.Callback{
		private final String pathName ;
		private final CompletableFuture future;
		private final PackValue value;
		private final IPFSConstance.Type type ;

		IPFSDirForResultCallback(CompletableFuture future , PackValue value , String pathName , IPFSConstance.Type type){
			this.future = future ;
			this.value = value ;
			this.pathName = pathName ;
			this.type = type ;
		}

		@Override
		public void onResponse(Call call, Response response) {
			if (response.code() != 200) {
				HiveException e = new HiveException("Server Error: " + response.message());
				value.setException(e);
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
					fileAttrs.put(Info.name, name);

					File.Info fileInfo = new File.Info(fileAttrs);
					IPFSFile file = new IPFSFile(pathName, fileInfo, rpcHelper);

					value.setValue(file);
					future.complete(value);
					break;
				}
				case MKDIR: {
					HashMap<String, String> dirAttrs = new HashMap<>();
					dirAttrs.put(Directory.Info.itemId, getId());
					dirAttrs.put(Info.childCount, "0");

					int LastPos = pathName.lastIndexOf("/");
					String name = pathName.substring(LastPos + 1);
					dirAttrs.put(Info.name, name);

					Directory.Info dirInfo = new Directory.Info(dirAttrs);
					IPFSDirectory directory = new IPFSDirectory(pathName, dirInfo, rpcHelper);

					value.setValue(directory);
					future.complete(value);
					break;
				}
				case MOVE_TO:
					IPFSDirectory.this.pathName = pathName;
				case COPY_TO:
				case DELETE_ITEM:
					Void padding = new Void();
					value.setValue(padding);
					future.complete(value);
					break;
				case GET_CHILDREN: {
					ListChildResponse listChildResponse = (ListChildResponse) response.body();
					List<ListChildResponse.EntriesBean> entriesBeanList = listChildResponse.getEntries() ;
					ArrayList<ItemInfo> childList = new ArrayList<>();
					if (entriesBeanList != null){
						for (ListChildResponse.EntriesBean entriesBean : entriesBeanList){
							HashMap<String, String> attrs = new HashMap<>();
							attrs.put(Directory.Info.itemId, rpcHelper.getIpfsEntry().getUid());
							ItemInfo info = new ItemInfo(attrs);
							info.put(ItemInfo.name, entriesBean.getName());
							childList.add(info);
						}
					}

					Children children = new Children(childList);
					if (value != null && value.getCallback() != null) {
						Callback<Children> callback = (Callback<Children>) value.getCallback();
						callback.onSuccess(children);
					}

					future.complete(children);
					
					break;
				}
			}
		}

		@Override
		public void onFailure(Call call, Throwable t) {
			HiveException e = new HiveException(t.getMessage());
			value.setException(e);
			future.completeExceptionally(e);
		}
	}

	private class IPFSDirCallback implements retrofit2.Callback{
		private final String pathName ;
		private final CompletableFuture future;
		private final Callback callback;
		private final IPFSConstance.Type type;

		IPFSDirCallback(CompletableFuture future , Callback callback , String pathName , IPFSConstance.Type type) {
			this.future = future;
			this.callback = callback;
			this.pathName = pathName ;
			this.type = type;
		}

		@Override
		public void onResponse(Call call, Response response) {
			if (response.code() != 200) {
				HiveException ex = new HiveException("Server Error: " + response.message());
				this.callback.onError(ex);
				future.completeExceptionally(ex);
				return;
			}

			switch (type) {
				case GET_INFO: {
					StatResponse statResponse = (StatResponse) response.body();
					HashMap<String, String> attrs = new HashMap<>();
					attrs.put(Info.itemId, getId());

					if (IPFSDirectory.this.pathName.equals("/")) {
						attrs.put(Info.name, "/");
					}
					else {
						int LastPos = IPFSDirectory.this.pathName.lastIndexOf("/");
						String name = IPFSDirectory.this.pathName.substring(LastPos + 1);
						attrs.put(Info.name, name);
					}

					attrs.put(Info.childCount, Integer.toString(statResponse.getBlocks()));

					dirInfo = new Directory.Info(attrs);
					this.callback.onSuccess(dirInfo);
					future.complete(dirInfo);
					break;
				}
				case GET_DIR: {
					StatResponse statResponse = (StatResponse) response.body();
					if (!rpcHelper.isFolder(statResponse.getType())) {
						HiveException e = new HiveException("This is not a directory");
						this.callback.onError(e);
						future.completeExceptionally(e);
						return;
					}

					HashMap<String, String> attrs = new HashMap<>();
					attrs.put(Info.itemId, getId());

					int LastPos = pathName.lastIndexOf("/");
					String name = pathName.substring(LastPos + 1);
					attrs.put(Info.name, name);

					attrs.put(Info.childCount, Integer.toString(statResponse.getBlocks()));

					Directory.Info dirInfo = new Directory.Info(attrs);
					IPFSDirectory directory = new IPFSDirectory(pathName, dirInfo, rpcHelper);
					this.callback.onSuccess(directory);
					future.complete(directory);
					break;
				}
				case GET_FILE: {
					StatResponse fileStatResponse = (StatResponse) response.body();
					if (!rpcHelper.isFile(fileStatResponse.getType())) {
						HiveException e = new HiveException("This is not a file");
						this.callback.onError(e);
						future.completeExceptionally(e);
						return;
					}

					HashMap<String, String> fileAttrs = new HashMap<>();
					fileAttrs.put(File.Info.itemId, getId());
					fileAttrs.put(File.Info.size, Integer.toString(fileStatResponse.getSize()));

					int LastPos = pathName.lastIndexOf("/");
					String name = pathName.substring(LastPos + 1);
					fileAttrs.put(File.Info.name, name);

					File.Info fileInfo = new File.Info(fileAttrs);
					IPFSFile file = new IPFSFile(pathName, fileInfo, rpcHelper);
					this.callback.onSuccess(file);
					future.complete(file);

					break;
				}
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
