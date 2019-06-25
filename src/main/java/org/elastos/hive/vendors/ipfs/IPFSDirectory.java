package org.elastos.hive.vendors.ipfs;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

import org.elastos.hive.Callback;
import org.elastos.hive.Children;
import org.elastos.hive.Directory;
import org.elastos.hive.File;
import org.elastos.hive.HiveException;
import org.elastos.hive.ItemInfo;
import org.elastos.hive.NullCallback;
import org.elastos.hive.UnirestAsyncCallback;
import org.elastos.hive.Void;
import org.json.JSONArray;
import org.json.JSONObject;

import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;

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
		return rpcHelper.checkExpired()
				.thenCompose(placeHolder -> getInfo(placeHolder, callback));
	}

	private CompletableFuture<Info> getInfo(Void placeHolder, Callback<Info> callback) {
		CompletableFuture<Directory.Info> future = new CompletableFuture<Directory.Info>();

		if (callback == null)
			callback = new NullCallback<Directory.Info>();

		String url = String.format("%s%s", rpcHelper.getBaseUrl(), IPFSMethod.STAT);
		Unirest.get(url)
			.header(IPFSURL.ContentType, IPFSURL.Json)
			.queryString(IPFSURL.UID, getId())
			.queryString(IPFSURL.PATH, pathName)
			.asJsonAsync(new GetDirInfoCallback(future, callback));

		return future;
	}

	@Override
	public CompletableFuture<Directory> createDirectory(String path) {
		return  createDirectory(path, new NullCallback<Directory>());
	}

	@Override
	public CompletableFuture<Directory> createDirectory(String path, Callback<Directory> callback) {
		CompletableFuture<Directory> future = new CompletableFuture<Directory>();

		if (callback == null)
			callback = new NullCallback<Directory>();
		
		if (path == null || path.isEmpty()) {
			HiveException e = new HiveException("The path is invalid");
			callback.onError(e);
			future.completeExceptionally(e);
			return future;
		}

		if (path.contains("/")) {
			HiveException e = new HiveException("Only need the path name");
			callback.onError(e);
			future.completeExceptionally(e);
			return future;
		}

		String newPath = String.format("%s/%s", this.pathName, path);
		final Callback<Directory> finalCallback = callback;
		return rpcHelper.checkExpired()
				.thenCompose(placeHolder -> createDirectory(placeHolder, newPath))
				.thenCompose(placeHolder -> rpcHelper.stat("/"))
				.thenCompose(homeHash -> publishCreateDirectoryResult(newPath, homeHash, finalCallback));
	}

	private CompletableFuture<Void> createDirectory(Void placeHolder, String path) {
		CompletableFuture<Void> future = new CompletableFuture<Void>();

		String url = String.format("%s%s", rpcHelper.getBaseUrl(), IPFSMethod.MKDIR);
		Unirest.get(url)
			.header(IPFSURL.ContentType, IPFSURL.Json)
			.queryString(IPFSURL.UID, rpcHelper.getIpfsEntry().getUid())
			.queryString(IPFSURL.PATH, path)
			.queryString("parents", "false")
			.asJsonAsync(new commonCallback(future));

		return future;
	}

	private CompletableFuture<Directory> publishCreateDirectoryResult(String pathName, String homeHash, Callback<Directory> callback) {
		CompletableFuture<Directory> future = new CompletableFuture<Directory>();

		//using name/publish to publish the hash
		if (homeHash == null) {
			HiveException e = new HiveException("The stat hash is invalid");
			callback.onError(e);
			future.completeExceptionally(e);
			return future;
		}

		String url = String.format("%s%s", rpcHelper.getBaseUrl(), IPFSMethod.PUBLISH);
		Unirest.get(url)
			.header(IPFSURL.ContentType, IPFSURL.Json)
			.queryString(IPFSURL.UID, getId())
			.queryString(IPFSURL.PATH, homeHash)
			.asJsonAsync(new publishCreateDirectoryResultCallback(pathName, future, callback));

		return future;
	}

	@Override
	public CompletableFuture<Directory> getDirectory(String path) {
		return getDirectory(path, new NullCallback<Directory>());
	}

	@Override
	public CompletableFuture<Directory> getDirectory(String path, Callback<Directory> callback) {
		return rpcHelper.checkExpired()
				.thenCompose(placeHolder -> getDirectory(placeHolder, path, callback));
	}

	private CompletableFuture<Directory> getDirectory(Void placeHolder, String path, Callback<Directory> callback) {
		CompletableFuture<Directory> future = new CompletableFuture<Directory>();

		if (callback == null)
			callback = new NullCallback<Directory>();

		if (path.contains("/")) {
			HiveException e = new HiveException("Only need the name of a directory.");
			callback.onError(e);
			future.completeExceptionally(e);
			return future;
		}

		String pathName = String.format("%s/%s", this.pathName, path);
		//stat
		String url = String.format("%s%s", rpcHelper.getBaseUrl(), IPFSMethod.STAT);
		Unirest.get(url)
			.header(IPFSURL.ContentType, IPFSURL.Json)
			.queryString(IPFSURL.UID, getId())
			.queryString(IPFSURL.PATH, pathName)
			.asJsonAsync(new GetDirectoryCallback(pathName, future, callback));

		return future;
	}

	@Override
	public CompletableFuture<File> createFile(String path) {
		return createFile(path, new NullCallback<File>());
	}

	@Override
	public CompletableFuture<File> createFile(String path, Callback<File> callback) {
		CompletableFuture<File> future = new CompletableFuture<File>();

		if (callback == null)
			callback = new NullCallback<File>();

		if (path.contains("/")) {
			HiveException e = new HiveException("Only need the name of a file.");
			callback.onError(e);
			future.completeExceptionally(e);
			return future;
		}

		String pathName = String.format("%s/%s", this.pathName, path);

		final Callback<File> finalCallback = callback;
		return rpcHelper.checkExpired()
				.thenCompose(placeHolder -> createFile(placeHolder, pathName))
				.thenCompose(placeHolder -> rpcHelper.stat("/"))
				.thenCompose(homeHash -> publishCreateFileResult(pathName, homeHash, finalCallback));
	}

	private CompletableFuture<Void> createFile(Void placeHolder, String pathName) {
		CompletableFuture<Void> future = new CompletableFuture<Void>();

		String url = String.format("%s%s", rpcHelper.getBaseUrl(), IPFSMethod.WRITE);
		String type = String.format("multipart/form-data; boundary=%s", UUID.randomUUID().toString());
		Unirest.post(url)
			.header(IPFSURL.ContentType, type)
			.queryString(IPFSURL.UID, rpcHelper.getIpfsEntry().getUid())
			.queryString(IPFSURL.PATH, pathName)
			.queryString("create", "true")
			.asJsonAsync(new commonCallback(future));
		
		return future;
	}
	
	private CompletableFuture<File> publishCreateFileResult(String pathName, String homeHash, Callback<File> callback) {
		CompletableFuture<File> future = new CompletableFuture<File>();

		//using name/publish to publish the hash
		if (homeHash == null) {
			HiveException e = new HiveException("The stat hash is invalid");
			callback.onError(e);
			future.completeExceptionally(e);
			return future;
		}

		String url = String.format("%s%s", rpcHelper.getBaseUrl(), IPFSMethod.PUBLISH);
		Unirest.get(url)
			.header(IPFSURL.ContentType, IPFSURL.Json)
			.queryString(IPFSURL.UID, getId())
			.queryString(IPFSURL.PATH, homeHash)
			.asJsonAsync(new publishCreateFileResultCallback(pathName, future, callback));

		return future;
	}

	@Override
	public CompletableFuture<File> getFile(String path) {
		return getFile(path, new NullCallback<File>());
	}

	@Override
	public CompletableFuture<File> getFile(String path, Callback<File> callback) {
		return rpcHelper.checkExpired()
				.thenCompose(placeHolder -> getFile(placeHolder, path, callback));
	}

	private CompletableFuture<File> getFile(Void placeHolder, String path, Callback<File> callback) {
		CompletableFuture<File> future = new CompletableFuture<File>();

		if (callback == null)
			callback = new NullCallback<File>();

		if (path.contains("/")) {
			HiveException e = new HiveException("Only need the name of a file.");
			callback.onError(e);
			future.completeExceptionally(e);
			return future;
		}

		String pathName = String.format("%s/%s", this.pathName, path);
		String url = String.format("%s%s", rpcHelper.getBaseUrl(), IPFSMethod.STAT);
		Unirest.get(url)
			.header(IPFSURL.ContentType, IPFSURL.Json)
			.queryString(IPFSURL.UID, getId())
			.queryString(IPFSURL.PATH, pathName)
			.asJsonAsync(new GetFileCallback(pathName, future, callback));

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
		if (callback == null)
			callback = new NullCallback<Void>();

		CompletableFuture<Void> future = new CompletableFuture<Void>();
		if (path == null || path.isEmpty()) {
			HiveException e = new HiveException("The path is invalid");
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

		if (path.equals(this.pathName)) {
			HiveException e = new HiveException("Can't move to the oneself directory");
			callback.onError(e);
			future.completeExceptionally(e);
			return future;
		}

		int LastPos = this.pathName.lastIndexOf("/");
		String name = this.pathName.substring(LastPos + 1);
		final String newPath = String.format("%s/%s", path, name);

		final Callback<Void> finalCallback = callback;
		return rpcHelper.checkExpired()
				.thenCompose(placeHolder -> moveTo(placeHolder, newPath))
				.thenCompose(placeHolder -> rpcHelper.stat("/"))
				.thenCompose(homeHash -> publishMoveResult(newPath, homeHash, finalCallback));
	}

	private CompletableFuture<Void> moveTo(Void placeHolder, String newPath) {
		CompletableFuture<Void> future = new CompletableFuture<Void>();

		String url = String.format("%s%s", rpcHelper.getBaseUrl(), IPFSMethod.MV);
		Unirest.get(url)
				.header(IPFSURL.ContentType, IPFSURL.Json)
				.queryString(IPFSURL.UID, rpcHelper.getIpfsEntry().getUid())
				.queryString(IPFSURL.SOURCE, this.pathName)
				.queryString(IPFSURL.DEST, newPath)
				.asJsonAsync(new commonCallback(future));

		return future;
	}

	private CompletableFuture<Void> publishMoveResult(String newPath, String homeHash, Callback<Void> callback) {
		CompletableFuture<Void> future = new CompletableFuture<Void>();
		//using name/publish to publish the hash
		if (homeHash == null) {
			future.completeExceptionally(new HiveException("The stat hash is invalid"));
			return future;
		}

		String url = String.format("%s%s", rpcHelper.getBaseUrl(), IPFSMethod.PUBLISH);
		Unirest.get(url)
			.header(IPFSURL.ContentType, IPFSURL.Json)
			.queryString(IPFSURL.UID, getId())
			.queryString(IPFSURL.PATH, homeHash)
			.asJsonAsync(new publishMoveResultCallback(newPath, future, callback));

		return future;
	}

	@Override
	public CompletableFuture<Void> copyTo(String path) {
		return copyTo(path, new NullCallback<Void>());
	}

	@Override
	public CompletableFuture<Void> copyTo(String path, Callback<Void> callback) {
		if (callback == null)
			callback = new NullCallback<Void>();

		CompletableFuture<Void> future = new CompletableFuture<Void>();
		if (path == null || path.isEmpty()) {
			HiveException e = new HiveException("The path is invalid");
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

		if (path.equals(this.pathName)) {
			HiveException e = new HiveException("Can't copy to the oneself directory");
			callback.onError(e);
			future.completeExceptionally(e);
			return future;
		}

		final Callback<Void> finalCallback = callback;
		return rpcHelper.checkExpired()
				.thenCompose(placeHolder -> rpcHelper.stat(this.pathName))
				.thenCompose(hash -> copyTo(hash, path))
				.thenCompose(placeHolder -> rpcHelper.stat("/"))
				.thenCompose(hash -> commonPublish(hash, finalCallback));
	}

	private CompletableFuture<Void> copyTo(String hash, String path) {
		CompletableFuture<Void> future = new CompletableFuture<Void>();

		if (hash == null || hash.isEmpty()) {
			HiveException e = new HiveException("The hash is invalid");
			future.completeExceptionally(e);
			return future;
		}

		int LastPos = this.pathName.lastIndexOf("/");
		String name = this.pathName.substring(LastPos + 1);
		final String newPath = String.format("%s/%s", path, name);
		
		String url = String.format("%s%s", rpcHelper.getBaseUrl(), IPFSMethod.CP);
		Unirest.get(url)
				.header(IPFSURL.ContentType, IPFSURL.Json)
				.queryString(IPFSURL.UID, rpcHelper.getIpfsEntry().getUid())
				.queryString(IPFSURL.SOURCE, IPFSURL.PREFIX + hash)
				.queryString(IPFSURL.DEST, newPath)
				.asJsonAsync(new commonCallback(future));

		return future;
	}
	
	private CompletableFuture<Void> commonPublish(String homeHash, Callback<Void> callback) {
		CompletableFuture<Void> future = new CompletableFuture<Void>();
		//using name/publish to publish the hash
		if (homeHash == null) {
			future.completeExceptionally(new HiveException("The stat hash is invalid"));
			return future;
		}

		String url = String.format("%s%s", rpcHelper.getBaseUrl(), IPFSMethod.PUBLISH);
		Unirest.get(url)
			.header(IPFSURL.ContentType, IPFSURL.Json)
			.queryString(IPFSURL.UID, getId())
			.queryString(IPFSURL.PATH, homeHash)
			.asJsonAsync(new publishCallback(future, callback));

		return future;
	}

	@Override
	public CompletableFuture<Void> deleteItem() {
		return deleteItem(new NullCallback<Void>());
	}

	@Override
	public CompletableFuture<Void> deleteItem(Callback<Void> callback) {
		if (callback == null)
			callback = new NullCallback<Void>();
		
		if (pathName.equals("/")) {
			CompletableFuture<Void> future = new CompletableFuture<Void>();
			HiveException e = new HiveException("Can't delete the root.");
			callback.onError(e);
			future.completeExceptionally(e);
			return future;
		}

		final Callback<Void> finalCallback = callback;
		return rpcHelper.checkExpired()
				.thenCompose(placeHolder -> deleteItem(placeHolder))
				.thenCompose(placeHolder -> rpcHelper.stat("/"))
				.thenCompose(hash -> commonPublish(hash, finalCallback));
	}

	private CompletableFuture<Void> deleteItem(Void placeHolder) {
		CompletableFuture<Void> future = new CompletableFuture<Void>();

		String url = String.format("%s%s", rpcHelper.getBaseUrl(), IPFSMethod.RM);
		Unirest.get(url)
			.header(IPFSURL.ContentType, IPFSURL.Json)
			.queryString(IPFSURL.UID, rpcHelper.getIpfsEntry().getUid())
			.queryString(IPFSURL.PATH, pathName)
			.queryString("recursive", "true")
			.asJsonAsync(new commonCallback(future));

		return future;
	}

	@Override
	public void close() {
		// TODO Auto-generated method stub

	}

	@Override
	public CompletableFuture<Children> getChildren() {
		return getChildren(new NullCallback<Children>());
	}

	@Override
	public CompletableFuture<Children> getChildren(Callback<Children> callback) {
		return rpcHelper.checkExpired()
				.thenCompose(placeHolder -> getChildren(placeHolder, callback));
	}

	private CompletableFuture<Children> getChildren(Void placeHolder, Callback<Children> callback) {
		CompletableFuture<Children> future = new CompletableFuture<Children>();

		if (callback == null)
			callback = new NullCallback<Children>();

		//1. Using files/ls to get the nameList.
		ArrayList<String> nameList = null;
		try {
			String url = rpcHelper.getBaseUrl() + IPFSMethod.LS;

			HttpResponse<JsonNode> response = Unirest.get(url)
				.header(IPFSURL.ContentType, IPFSURL.Json)
				.queryString(IPFSURL.UID, getId())
				.queryString(IPFSURL.PATH, this.pathName)
				.asJson();
			if (response.getStatus() == 200) {
				nameList = getNameList(pathName, response.getBody().getObject());
			}
		} catch (Exception ex) {
			HiveException e = new HiveException("GetChildren failed [ls]");
			callback.onError(e);
			future.completeExceptionally(e);
		}

		//2. Using stat to get the child's type.
		if (nameList == null) {
			future.completeExceptionally(new HiveException("GetChildren failed"));
			return future;
		}

		try {
			int len = nameList.size();
			ArrayList<ItemInfo> childList = new ArrayList<ItemInfo>(len);
			if (len > 0) {
				for (int i = 0; i < len; i++) {
					String childPath = nameList.get(i);
					HashMap<String, String> attrs = new HashMap<>();
					attrs.put(Directory.Info.itemId, rpcHelper.getIpfsEntry().getUid());
					ItemInfo info = new ItemInfo(attrs);
					// TODO;
					childList.add(info);
				}
			}

			Children children = new Children(childList);
			callback.onSuccess(children);
			future.complete(children);
		}
		catch (Exception ex) {
			callback.onSuccess(null);
			future.completeExceptionally(new HiveException("GetChildren failed [child type]"));
		}

		return future;
	}

	private ArrayList<String> getNameList(String parentPath, JSONObject baseJson) {
		JSONArray entries = null;
		try {
			entries = baseJson.getJSONArray("Entries");
		} catch (Exception e) {
			e.printStackTrace();
		}

		int len = 0;
		if (entries != null) {
			len = entries.length();
		}

		ArrayList<String> nameList = new ArrayList<String>(len);
		if (len > 0) {
			for (int i = 0; i < len; i++) {
				JSONObject itemJson = entries.getJSONObject(i);
				String name = itemJson.getString("Name");
				nameList.add(String.format("%s/%s", parentPath, name));
			}
		}

		return nameList;
	}

	private class GetDirInfoCallback implements UnirestAsyncCallback<JsonNode> {
		private final CompletableFuture<Directory.Info> future;
		private final Callback<Directory.Info> callback;

		GetDirInfoCallback(CompletableFuture<Directory.Info> future,
				   Callback<Directory.Info> callback) {
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

			HashMap<String, String> attrs = new HashMap<>();
			attrs.put(Directory.Info.itemId, getId());  // TODO;
			Directory.Info dirInfo = new Directory.Info(attrs);
			this.callback.onSuccess(dirInfo);
			future.complete(dirInfo);
		}

		@Override
		public void failed(UnirestException exception) {
			HiveException ex = new HiveException(exception.getMessage());
			this.callback.onError(ex);
			future.completeExceptionally(ex);
		}
	}

	private class publishCreateDirectoryResultCallback implements UnirestAsyncCallback<JsonNode> {
		private final String pathName;
		private final CompletableFuture<Directory> future;
		private final Callback<Directory> callback;

		private publishCreateDirectoryResultCallback(String pathName, CompletableFuture<Directory> future, Callback<Directory> callback) {
			this.pathName = pathName;
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

			HashMap<String, String> attrs = new HashMap<>();
			attrs.put(Directory.Info.itemId, getId());  // TODO;
			Directory.Info dirInfo = new Directory.Info(attrs);
			IPFSDirectory directory = new IPFSDirectory(pathName, dirInfo, rpcHelper);
			this.callback.onSuccess(directory);
			future.complete(directory);
		}

		@Override
		public void failed(UnirestException exception) {
			HiveException ex = new HiveException(exception.getMessage());
			this.callback.onError(ex);
			future.completeExceptionally(ex);
		}
	}

	private class GetDirectoryCallback implements UnirestAsyncCallback<JsonNode> {
		private final String pathName;
		private final CompletableFuture<Directory> future;
		private final Callback<Directory> callback;

		private GetDirectoryCallback(String pathName, CompletableFuture<Directory> future, Callback<Directory> callback) {
			this.pathName = pathName;
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

			JSONObject jsonObject = response.getBody().getObject();
			String type = jsonObject.getString("Type");
			if (!rpcHelper.isFolder(type)) {
				HiveException ex = new HiveException("This is not a directory");
				this.callback.onError(ex);
				future.completeExceptionally(ex);
				return;
			}

			HashMap<String, String> attrs = new HashMap<>();
			attrs.put(Directory.Info.itemId, getId());  // TODO;
			Directory.Info dirInfo = new Directory.Info(attrs);
			IPFSDirectory directory = new IPFSDirectory(pathName, dirInfo, rpcHelper);
			this.callback.onSuccess(directory);
			future.complete(directory);
		}

		@Override
		public void failed(UnirestException exception) {
			HiveException ex = new HiveException(exception.getMessage());
			this.callback.onError(ex);
			future.completeExceptionally(ex);
		}
	}

	private class publishCreateFileResultCallback implements UnirestAsyncCallback<JsonNode> {
		private final String pathName;
		private final CompletableFuture<File> future;
		private final Callback<File> callback;

		private publishCreateFileResultCallback(String pathName, CompletableFuture<File> future, Callback<File> callback) {
			this.pathName = pathName;
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

			HashMap<String, String> attrs = new HashMap<>();
			attrs.put(Directory.Info.itemId, getId());  // TODO;
			File.Info fileInfo = new File.Info(attrs);
			IPFSFile file = new IPFSFile(pathName, fileInfo, rpcHelper);
			this.callback.onSuccess(file);
			future.complete(file);
		}

		@Override
		public void failed(UnirestException exception) {
			HiveException ex = new HiveException(exception.getMessage());
			this.callback.onError(ex);
			future.completeExceptionally(ex);
		}
	}

	private class GetFileCallback implements UnirestAsyncCallback<JsonNode> {
		private final String pathName;
		private final CompletableFuture<File> future;
		private final Callback<File> callback;

		private GetFileCallback(String pathName, CompletableFuture<File> future, Callback<File> callback) {
			this.pathName = pathName;
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

			JSONObject jsonObject = response.getBody().getObject();
			String type = jsonObject.getString("Type");
			if (!rpcHelper.isFile(type)) {
				HiveException ex = new HiveException("This is not a file");
				this.callback.onError(ex);
				future.completeExceptionally(ex);
				return;
			}

			HashMap<String, String> attrs = new HashMap<>();
			attrs.put(Directory.Info.itemId, getId());  // TODO;
			File.Info fileInfo = new File.Info(attrs);
			IPFSFile file = new IPFSFile(pathName, fileInfo, rpcHelper);
			this.callback.onSuccess(file);
			future.complete(file);
		}

		@Override
		public void failed(UnirestException exception) {
			HiveException ex = new HiveException(exception.getMessage());
			this.callback.onError(ex);
			future.completeExceptionally(ex);
		}
	}

	private class commonCallback implements UnirestAsyncCallback<JsonNode> {
		private final CompletableFuture<Void> future;

		commonCallback(CompletableFuture<Void> future) {
			this.future = future;
		}

		@Override
		public void cancelled() {}

		@Override
		public void completed(HttpResponse<JsonNode> response) {
			if (response.getStatus() != 200) {
				HiveException e = new HiveException("Server Error: " + response.getStatusText());
				future.completeExceptionally(e);
				return;
			}

			Void placeHolder = new Void();
			future.complete(placeHolder);
		}

		@Override
		public void failed(UnirestException exception) {
			HiveException e = new HiveException(exception.getMessage());
			future.completeExceptionally(e);
		}
	}
	
	private class publishMoveResultCallback implements UnirestAsyncCallback<JsonNode> {
		private final String pathName;
		private final CompletableFuture<Void> future;
		private final Callback<Void> callback;

		publishMoveResultCallback(String pathName, CompletableFuture<Void> future, Callback<Void> callback) {
			this.pathName = pathName;
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

			IPFSDirectory.this.pathName = pathName;
			Void placeHolder = new Void();
			this.callback.onSuccess(placeHolder);
			future.complete(placeHolder);
		}

		@Override
		public void failed(UnirestException exception) {
			HiveException ex = new HiveException(exception.getMessage());
			this.callback.onError(ex);
			future.completeExceptionally(ex);
		}
	}

	private class publishCallback implements UnirestAsyncCallback<JsonNode> {
		private final CompletableFuture<Void> future;
		private final Callback<Void> callback;

		publishCallback(CompletableFuture<Void> future, Callback<Void> callback) {
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

			Void placeHolder = new Void();
			this.callback.onSuccess(placeHolder);
			future.complete(placeHolder);
		}

		@Override
		public void failed(UnirestException exception) {
			HiveException ex = new HiveException(exception.getMessage());
			this.callback.onError(ex);
			future.completeExceptionally(ex);
		}
	}
}
