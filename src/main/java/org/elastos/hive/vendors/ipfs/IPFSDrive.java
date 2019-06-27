package org.elastos.hive.vendors.ipfs;

import java.util.HashMap;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

import org.elastos.hive.Callback;
import org.elastos.hive.Directory;
import org.elastos.hive.Drive;
import org.elastos.hive.DriveType;
import org.elastos.hive.File;
import org.elastos.hive.HiveException;
import org.elastos.hive.ItemInfo;
import org.elastos.hive.NullCallback;
import org.elastos.hive.UnirestAsyncCallback;
import org.json.JSONObject;

import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;

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
		return rpcHelper.checkExpiredNew()
				.thenCompose(value -> getInfo(value, callback));
	}

	private CompletableFuture<Info> getInfo(PackValue value, Callback<Info> callback) {
		CompletableFuture<Info> future = new CompletableFuture<Info>();

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
			.queryString(IPFSURL.PATH, "/")
			.asJsonAsync(new GetInfoCallback(callback, future));

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
			HiveException e = new HiveException("Empty path name");
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

		String url = String.format("%s%s", rpcHelper.getBaseUrl(), IPFSMethod.MKDIR);
		Unirest.get(url)
			.header(IPFSURL.ContentType, IPFSURL.Json)
			.queryString(IPFSURL.UID, rpcHelper.getIpfsEntry().getUid())
			.queryString(IPFSURL.PATH, path)
			.queryString("parents", "false")
			.asJsonAsync(new CreateDirectoryCallback(value, path, future));

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

		String url = String.format("%s%s", rpcHelper.getBaseUrl(), IPFSMethod.STAT);
		Unirest.get(url)
			.header(IPFSURL.ContentType, IPFSURL.Json)
			.queryString(IPFSURL.UID, getId())
			.queryString(IPFSURL.PATH, path)
			.asJsonAsync(new GetDirectoryCallback(path, callback, future));

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
			HiveException e = new HiveException("Empty path name");
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

		String url = String.format("%s%s", rpcHelper.getBaseUrl(), IPFSMethod.WRITE);
		String type = String.format("multipart/form-data; boundary=%s", UUID.randomUUID().toString());
		Unirest.post(url)
			.header(IPFSURL.ContentType, type)
			.queryString(IPFSURL.UID, rpcHelper.getIpfsEntry().getUid())
			.queryString(IPFSURL.PATH, path)
			.queryString("create", "true")
			.asJsonAsync(new CreateFileCallback(value, path, future));

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

		String url = String.format("%s%s", rpcHelper.getBaseUrl(), IPFSMethod.STAT);
		Unirest.get(url)
			.header(IPFSURL.ContentType, IPFSURL.Json)
			.queryString(IPFSURL.UID, getId())
			.queryString(IPFSURL.PATH, path)
			.asJsonAsync(new GetFileCallback(path, future, callback));

		return future;
	}

	private class GetInfoCallback implements UnirestAsyncCallback<JsonNode> {
		private final CompletableFuture<Info> future;
		private final Callback<Info> callback;

		GetInfoCallback(Callback<Info> callback, CompletableFuture<Info> future) {
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
			attrs.put(Drive.Info.driveId, getId());  // TODO:

			this.callback.onSuccess(driveInfo);
			future.complete(driveInfo);
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
		private final Callback<Directory> callback;
		private final CompletableFuture<Directory> future;

		private GetDirectoryCallback(String pathName, Callback<Directory> callback, CompletableFuture<Directory> future) {
			this.pathName = pathName;
			this.future = future;
			this.callback = callback;
		}

		@Override
		public void cancelled() {}

		@Override
		public void completed(HttpResponse<JsonNode> response) {
			if (response.getStatus() != 200) {
				HiveException e = new HiveException("Server Error: " + response.getStatusText());
				callback.onError(e);
				future.completeExceptionally(e);
				return;
			}

			JSONObject jsonObject = response.getBody().getObject();
			String type = jsonObject.getString("Type");
			if (!rpcHelper.isFolder(type)) {
				HiveException e = new HiveException("This is not a directory");
				callback.onError(e);
				future.completeExceptionally(e);
				return;
			}

			HashMap<String, String> attrs = new HashMap<>();
			attrs.put(Directory.Info.itemId, getId());
			// TODO:

			Directory.Info info = new Directory.Info(attrs);
			IPFSDirectory directory = new IPFSDirectory(pathName, info, rpcHelper);
			callback.onSuccess(directory);
			future.complete(directory);
		}

		@Override
		public void failed(UnirestException exception) {
			HiveException e = new HiveException(exception.getMessage());
			callback.onError(e);
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
				HiveException e = new HiveException("Server Error: " + response.getStatusText());
				callback.onError(e);
				future.completeExceptionally(e);
				return;
			}

			JSONObject jsonObject = response.getBody().getObject();
			String type = jsonObject.getString("Type");
			if (!rpcHelper.isFile(type)) {
				HiveException e = new HiveException("This is not a file");
				callback.onError(e);
				future.completeExceptionally(e);
				return;
			}

			HashMap<String, String> attrs = new HashMap<>();
			attrs.put(Directory.Info.itemId, getId());
			// TODO:

			File.Info info = new File.Info(attrs);
			IPFSFile file = new IPFSFile(pathName, info, rpcHelper);
			callback.onSuccess(file);
			future.complete(file);
		}

		@Override
		public void failed(UnirestException exception) {
			HiveException e = new HiveException(exception.getMessage());
			callback.onError(e);
			future.completeExceptionally(e);
		}
	}

	@Override
	public CompletableFuture<ItemInfo> getItemInfo(String path) {
		// TODO
		return null;
	}

	@Override
	public CompletableFuture<ItemInfo> getItemInfo(String path, Callback<ItemInfo> callback) {
		// TODO
		return null;
	}
}
