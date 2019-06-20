package org.elastos.hive.vendors.ipfs;

import java.util.concurrent.CompletableFuture;

import org.elastos.hive.Callback;
import org.elastos.hive.Directory;
import org.elastos.hive.Drive;
import org.elastos.hive.DriveType;
import org.elastos.hive.File;
import org.elastos.hive.HiveException;
import org.elastos.hive.NullCallback;
import org.elastos.hive.Status;
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
	public String getId() {
		return driveInfo.getId();
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
		return rpcHelper.checkExpired()
				.thenCompose(status -> getInfo(status, callback));
	}

	private CompletableFuture<Info> getInfo(Status status, Callback<Info> callback) {
		CompletableFuture<Info> future = new CompletableFuture<Info>();

		if (callback == null)
			callback = new NullCallback<Info>();

		if (status.getStatus() == 0) {
			future.completeExceptionally(new HiveException("getInfo failed"));
			return null;
		}

		String url = String.format("%s%s", rpcHelper.getBaseUrl(), IPFSMethod.STAT);
		Unirest.get(url)
			.header(IPFSURL.ContentType, IPFSURL.Json)
			.queryString(IPFSURL.UID, getId())
			.queryString(IPFSURL.PATH, "/")
			.asJsonAsync(new GetInfoCallback(future, callback));

		return future;
	}
	
	@Override
	public CompletableFuture<Directory> createDirectory(String path) {
		return createDirectory(path, new NullCallback<Directory>());
	}

	@Override
	public CompletableFuture<Directory> createDirectory(String path, Callback<Directory> callback) {
		return rpcHelper.checkExpired()
				.thenCompose(status -> createDirectory(status, path, callback));
	}

	private CompletableFuture<Directory> createDirectory(Status checkStatus, String path, Callback<Directory> callback) {
		CompletableFuture<Directory> future = new CompletableFuture<Directory>();

		if (callback == null)
			callback = new NullCallback<Directory>();

		if (!path.startsWith("/")) {
			HiveException e = new HiveException("Path name must be a abosulte path");
			callback.onError(e);
			future.completeExceptionally(e);
			return future;
		}

		if (checkStatus.getStatus() == 0) {
			future.completeExceptionally(new HiveException("createDirectory failed"));
			return null;
		}

		//1. mkdir
		Status status = IPFSUtils.mkdir(rpcHelper, path);
		if (status.getStatus() == 0) {
			future.completeExceptionally(new HiveException("createDirectory failed"));
			return null;
		}

		//2. using stat to get the home hash
		String homeHash = IPFSUtils.stat(rpcHelper, "/");

		if (homeHash == null) {
			future.completeExceptionally(new HiveException("The stat hash is invalid"));
			return future;
		}

		//3. using name/publish to publish the hash
		String url = String.format("%s%s", rpcHelper.getBaseUrl(), IPFSMethod.PUBLISH);
		Unirest.get(url)
			.header(IPFSURL.ContentType, IPFSURL.Json)
			.queryString(IPFSURL.UID, getId())
			.queryString(IPFSURL.PATH, homeHash)
			.asJsonAsync(new CreateDirectoryCallback(path, future, callback));

		return future;
	}
	
	@Override
	public CompletableFuture<Directory> getDirectory(String path) {
		return getDirectory(path, new NullCallback<Directory>());
	}

	@Override
	public CompletableFuture<Directory> getDirectory(String path, Callback<Directory> callback) {
		return rpcHelper.checkExpired()
				.thenCompose(status -> getDirectory(status, path, callback));
	}

	private CompletableFuture<Directory> getDirectory(Status status, String path, Callback<Directory> callback) {
		CompletableFuture<Directory> future = new CompletableFuture<Directory>();

		if (callback == null)
			callback = new NullCallback<Directory>();

		if (!path.startsWith("/")) {
			HiveException e = new HiveException("Path name must be a abosulte path");
			callback.onError(e);
			future.completeExceptionally(e);
			return future;
		}

		if (status.getStatus() == 0) {
			future.completeExceptionally(new HiveException("getDirectory failed"));
			return null;
		}

		//stat
		String url = String.format("%s%s", rpcHelper.getBaseUrl(), IPFSMethod.STAT);
		Unirest.get(url)
			.header(IPFSURL.ContentType, IPFSURL.Json)
			.queryString(IPFSURL.UID, getId())
			.queryString(IPFSURL.PATH, path)
			.asJsonAsync(new GetDirectoryCallback(path, future, callback));

		return future;
	}
	
	@Override
	public CompletableFuture<File> createFile(String path) {
		return createFile(path, new NullCallback<File>());
	}

	@Override
	public CompletableFuture<File> createFile(String path, Callback<File> callback) {
		return rpcHelper.checkExpired()
				.thenCompose(status -> createFile(status, path, callback));
	}

	private CompletableFuture<File> createFile(Status checkStatus, String path, Callback<File> callback) {
		CompletableFuture<File> future = new CompletableFuture<File>();

		if (callback == null)
			callback = new NullCallback<File>();

		if (!path.startsWith("/")) {
			HiveException e = new HiveException("Need a absolute path to create a file.");
			callback.onError(e);
			future.completeExceptionally(e);
			return future;
		}

		if (checkStatus.getStatus() == 0) {
			future.completeExceptionally(new HiveException("createFile failed"));
			return null;
		}

		//1. create file
		Status status = IPFSUtils.createEmptyFile(rpcHelper, path);
		if (status.getStatus() == 0) {
			future.completeExceptionally(new HiveException("Create file failed."));
			return null;
		}

		//2. using stat to get the home hash
		String homeHash = IPFSUtils.stat(rpcHelper, "/");

		if (homeHash == null) {
			future.completeExceptionally(new HiveException("The stat hash is invalid"));
			return future;
		}

		//3. using name/publish to publish the hash
		String url = String.format("%s%s", rpcHelper.getBaseUrl(), IPFSMethod.PUBLISH);
		Unirest.get(url)
			.header(IPFSURL.ContentType, IPFSURL.Json)
			.queryString(IPFSURL.UID, getId())
			.queryString(IPFSURL.PATH, homeHash)
			.asJsonAsync(new CreateFileCallback(path, future, callback));

		return future;
	}
	
	@Override
	public CompletableFuture<File> getFile(String path) {
		return getFile(path, new NullCallback<File>());
	}

	@Override
	public CompletableFuture<File> getFile(String path, Callback<File> callback) {
		return rpcHelper.checkExpired()
				.thenCompose(status -> getFile(status, path, callback));
	}

	private CompletableFuture<File> getFile(Status status, String path, Callback<File> callback) {
		CompletableFuture<File> future = new CompletableFuture<File>();

		if (callback == null)
			callback = new NullCallback<File>();

		if (!path.startsWith("/")) {
			HiveException e = new HiveException("Need a absolute path to get a file.");
			callback.onError(e);
			future.completeExceptionally(e);
			return future;
		}

		if (status.getStatus() == 0) {
			future.completeExceptionally(new HiveException("getFile failed"));
			return null;
		}

		String url = String.format("%s%s", rpcHelper.getBaseUrl(), IPFSMethod.STAT);
		Unirest.get(url)
			.header(IPFSURL.ContentType, IPFSURL.Json)
			.queryString(IPFSURL.UID, getId())
			.queryString(IPFSURL.PATH, path)
			.asJsonAsync(new GetFileCallback(path, future, callback));

		return future;
	}

	@Override
	public DriveType getType() {
		return DriveType.hiveIpfs;
	}

	@Override
	public CompletableFuture<Directory> getRootDir() {
		return getRootDir(new NullCallback<Directory>());
	}

	@Override
	public CompletableFuture<Directory> getRootDir(Callback<Directory> callback) {
		if (callback == null)
			callback = new NullCallback<Directory>();

		return getDirectory("/", callback);
	}

	private class GetInfoCallback implements UnirestAsyncCallback<JsonNode> {
		private final CompletableFuture<Info> future;
		private final Callback<Info> callback;

		GetInfoCallback(CompletableFuture<Info> future, Callback<Info> callback) {
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

			driveInfo = new Info(getId());
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
		private final String pathName;
		private final CompletableFuture<Directory> future;
		private final Callback<Directory> callback;

		private CreateDirectoryCallback(String pathName, CompletableFuture<Directory> future, Callback<Directory> callback) {
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

			Directory.Info dirInfo = new Directory.Info(getId());
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
			if (!IPFSUtils.isFolder(type)) {
				HiveException ex = new HiveException("This is not a directory");
				this.callback.onError(ex);
				future.completeExceptionally(ex);
				return;
			}

			Directory.Info dirInfo = new Directory.Info(getId());
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

	private class CreateFileCallback implements UnirestAsyncCallback<JsonNode> {
		private final String pathName;
		private final CompletableFuture<File> future;
		private final Callback<File> callback;

		private CreateFileCallback(String pathName, CompletableFuture<File> future, Callback<File> callback) {
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

			File.Info fileInfo = new File.Info(getId());
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
			if (!IPFSUtils.isFile(type)) {
				HiveException ex = new HiveException("This is not a file");
				this.callback.onError(ex);
				future.completeExceptionally(ex);
				return;
			}

			File.Info fileInfo = new File.Info(getId());
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
}
