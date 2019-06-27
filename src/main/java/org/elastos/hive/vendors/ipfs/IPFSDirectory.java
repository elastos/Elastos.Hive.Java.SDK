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

		String url = String.format("%s%s", rpcHelper.getBaseUrl(), IPFSMethod.MKDIR);
		Unirest.get(url)
			.header(IPFSURL.ContentType, IPFSURL.Json)
			.queryString(IPFSURL.UID, rpcHelper.getIpfsEntry().getUid())
			.queryString(IPFSURL.PATH, newPath)
			.queryString("parents", "false")
			.asJsonAsync(new CreateDirectoryCallback(value, newPath, future));

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

		String url = String.format("%s%s", rpcHelper.getBaseUrl(), IPFSMethod.WRITE);
		String type = String.format("multipart/form-data; boundary=%s", UUID.randomUUID().toString());
		Unirest.post(url)
			.header(IPFSURL.ContentType, type)
			.queryString(IPFSURL.UID, rpcHelper.getIpfsEntry().getUid())
			.queryString(IPFSURL.PATH, pathName)
			.queryString("create", "true")
			.asJsonAsync(new CreateFileCallback(value, pathName, future));

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

		String url = String.format("%s%s", rpcHelper.getBaseUrl(), IPFSMethod.MV);
		Unirest.get(url)
				.header(IPFSURL.ContentType, IPFSURL.Json)
				.queryString(IPFSURL.UID, rpcHelper.getIpfsEntry().getUid())
				.queryString(IPFSURL.SOURCE, this.pathName)
				.queryString(IPFSURL.DEST, newPath)
				.asJsonAsync(new moveToCallback(value, newPath, future));

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

		String url = String.format("%s%s", rpcHelper.getBaseUrl(), IPFSMethod.CP);
		Unirest.get(url)
				.header(IPFSURL.ContentType, IPFSURL.Json)
				.queryString(IPFSURL.UID, rpcHelper.getIpfsEntry().getUid())
				.queryString(IPFSURL.SOURCE, IPFSURL.PREFIX + hash)
				.queryString(IPFSURL.DEST, newPath)
				.asJsonAsync(new copyToCallback(value, future));

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

		String url = String.format("%s%s", rpcHelper.getBaseUrl(), IPFSMethod.RM);
		Unirest.get(url)
			.header(IPFSURL.ContentType, IPFSURL.Json)
			.queryString(IPFSURL.UID, rpcHelper.getIpfsEntry().getUid())
			.queryString(IPFSURL.PATH, pathName)
			.queryString("recursive", "true")
			.asJsonAsync(new deleteItemCallback(value, future));

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

		//1. Using files/ls to get the nameList.
		ArrayList<String> nameList = null;
		try {
			String url = rpcHelper.getBaseUrl() + IPFSMethod.LS;

			HttpResponse<JsonNode> response = Unirest.get(url)
				.header(IPFSURL.ContentType, IPFSURL.Json)
				.queryString(IPFSURL.UID, getId())
				.queryString(IPFSURL.PATH, this.pathName)
				.asJson();
			if (response.getStatus() == 200)
				nameList = getNameList(pathName, response.getBody().getObject());
		} catch (Exception ex) {
			HiveException e = new HiveException(ex.getMessage());
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
				HiveException e = new HiveException("Server Error: " + response.getStatusText());
				this.callback.onError(e);
				future.completeExceptionally(e);
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
			HiveException e = new HiveException(exception.getMessage());
			this.callback.onError(e);
			future.completeExceptionally(e);
		}
	}

	private class CreateDirectoryCallback implements UnirestAsyncCallback<JsonNode> {
		private final CompletableFuture<PackValue> future;
		private final String pathName;
		private final PackValue value;

		CreateDirectoryCallback(PackValue value, String pathName, CompletableFuture<PackValue> future) {
			this.pathName = pathName;
			this.future = future;
			this.value = value;
		}

		@Override
		public void cancelled() {}

		@Override
		public void completed(HttpResponse<JsonNode> response) {
			if (response.getStatus() != 200) {
				HiveException e = new HiveException("Server Error: " + response.getStatusText());
				value.setException(e);
				future.completeExceptionally(e);
				return;
			}

			HashMap<String, String> attrs = new HashMap<>();
			attrs.put(Directory.Info.itemId, getId());
			// TODO:

			Directory.Info dirInfo = new Directory.Info(attrs);
			IPFSDirectory directory = new IPFSDirectory(pathName, dirInfo, rpcHelper);

			value.setValue(directory);
			future.complete(value);
		}

		@Override
		public void failed(UnirestException exception) {
			HiveException e = new HiveException(exception.getMessage());
			value.setException(e);
			future.completeExceptionally(e);
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
				HiveException e = new HiveException("This is not a directory");
				this.callback.onError(e);
				future.completeExceptionally(e);
				return;
			}

			HashMap<String, String> attrs = new HashMap<>();
			attrs.put(Directory.Info.itemId, getId());
			// TODO;

			Directory.Info dirInfo = new Directory.Info(attrs);
			IPFSDirectory directory = new IPFSDirectory(pathName, dirInfo, rpcHelper);
			this.callback.onSuccess(directory);
			future.complete(directory);
		}

		@Override
		public void failed(UnirestException exception) {
			HiveException e = new HiveException(exception.getMessage());
			this.callback.onError(e);
			future.completeExceptionally(e);
		}
	}

	private class CreateFileCallback implements UnirestAsyncCallback<JsonNode> {
		private final CompletableFuture<PackValue> future;
		private final String pathName;
		private final PackValue value;

		CreateFileCallback(PackValue value, String pathName, CompletableFuture<PackValue> future) {
			this.pathName = pathName;
			this.future = future;
			this.value = value;
		}

		@Override
		public void cancelled() {}

		@Override
		public void completed(HttpResponse<JsonNode> response) {
			if (response.getStatus() != 200) {
				HiveException e = new HiveException("Server Error: " + response.getStatusText());
				value.setException(e);
				future.completeExceptionally(e);
				return;
			}

			HashMap<String, String> attrs = new HashMap<>();
			attrs.put(Directory.Info.itemId, getId());
			// TOOD:

			File.Info fileInfo = new File.Info(attrs);
			IPFSFile file = new IPFSFile(pathName, fileInfo, rpcHelper);

			value.setValue(file);
			future.complete(value);
		}

		@Override
		public void failed(UnirestException exception) {
			HiveException e = new HiveException(exception.getMessage());
			value.setException(e);
			future.completeExceptionally(e);
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
				HiveException e = new HiveException("This is not a file");
				this.callback.onError(e);
				future.completeExceptionally(e);
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
			HiveException e = new HiveException(exception.getMessage());
			this.callback.onError(e);
			future.completeExceptionally(e);
		}
	}

	private class moveToCallback implements UnirestAsyncCallback<JsonNode> {
		private final CompletableFuture<PackValue> future;
		private final String pathName;
		private final PackValue value;

		moveToCallback(PackValue value, String pathName, CompletableFuture<PackValue> future) {
			this.pathName = pathName;
			this.future = future;
			this.value = value;
		}

		@Override
		public void cancelled() {}

		@Override
		public void completed(HttpResponse<JsonNode> response) {
			if (response.getStatus() != 200) {
				HiveException e = new HiveException("Server Error: " + response.getStatusText());
				value.setException(e);
				future.completeExceptionally(e);
				return;
			}

			IPFSDirectory.this.pathName = pathName;
			Void padding = new Void();
			value.setValue(padding);
			future.complete(value);
		}

		@Override
		public void failed(UnirestException exception) {
			HiveException e = new HiveException(exception.getMessage());
			value.setException(e);
			future.completeExceptionally(e);
		}
	}

	private class copyToCallback implements UnirestAsyncCallback<JsonNode> {
		private final CompletableFuture<PackValue> future;
		private final PackValue value;

		copyToCallback(PackValue value, CompletableFuture<PackValue> future) {
			this.future = future;
			this.value = value;
		}

		@Override
		public void cancelled() {}

		@Override
		public void completed(HttpResponse<JsonNode> response) {
			if (response.getStatus() != 200) {
				HiveException e = new HiveException("Server Error: " + response.getStatusText());
				value.setException(e);
				future.completeExceptionally(e);
				return;
			}

			Void padding = new Void();
			value.setValue(padding);
			future.complete(value);
		}

		@Override
		public void failed(UnirestException exception) {
			HiveException e = new HiveException(exception.getMessage());
			value.setException(e);
			future.completeExceptionally(e);
		}
	}

	private class deleteItemCallback implements UnirestAsyncCallback<JsonNode> {
		private final CompletableFuture<PackValue> future;
		private final PackValue value;

		deleteItemCallback(PackValue value, CompletableFuture<PackValue> future) {
			this.future = future;
			this.value = value;
		}

		@Override
		public void cancelled() {}

		@Override
		public void completed(HttpResponse<JsonNode> response) {
			if (response.getStatus() != 200) {
				HiveException e = new HiveException("Server Error: " + response.getStatusText());
				value.setException(e);
				future.completeExceptionally(e);
				return;
			}

			Void padding = new Void();
			value.setValue(padding);
			future.complete(value);
		}

		@Override
		public void failed(UnirestException exception) {
			HiveException e = new HiveException(exception.getMessage());
			value.setException(e);
			future.completeExceptionally(e);
		}
	}
}
