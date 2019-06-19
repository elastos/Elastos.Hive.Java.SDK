package org.elastos.hive.vendors.ipfs;

import java.util.concurrent.CompletableFuture;

import org.elastos.hive.Callback;
import org.elastos.hive.File;
import org.elastos.hive.HiveException;
import org.elastos.hive.NullCallback;
import org.elastos.hive.Status;
import org.elastos.hive.UnirestAsyncCallback;

import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;

final class IPFSFile extends File {
	private String pathName;
	private volatile File.Info fileInfo;
	private IPFSRpcHelper rpcHelper;

	IPFSFile(String pathName, File.Info fileInfo, IPFSRpcHelper rpcHelper) {
		this.fileInfo = fileInfo;
		this.pathName = pathName;
		this.rpcHelper = rpcHelper;
	}

	@Override
	public String getId() {
		return fileInfo.getId();
	}

	@Override
	public Info getLastInfo() {
		return fileInfo;
	}

	@Override
	public CompletableFuture<Info> getInfo() {
		return getInfo(new NullCallback<File.Info>());
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
			.queryString(IPFSURL.PATH, pathName)
			.asJsonAsync(new GetFileInfoCallback(future, callback));

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
	public CompletableFuture<Status> moveTo(String path) {
		return moveTo(path, new NullCallback<Status>());
	}

	@Override
	public CompletableFuture<Status> moveTo(String path, Callback<Status> callback) {
		return rpcHelper.checkExpired()
				.thenCompose(status -> moveTo(status, path, callback));
	}

	private CompletableFuture<Status> moveTo(Status checkStatus, String path, Callback<Status> callback) {
		CompletableFuture<Status> future = new CompletableFuture<Status>();
		if (callback == null)
			callback = new NullCallback<Status>();

		if (!path.startsWith("/")) {
			HiveException e = new HiveException("Path name must be a abosulte path");
			callback.onError(e);
			future.completeExceptionally(e);
			return future;
		}

		if (path.equals(this.pathName)) {
			HiveException e = new HiveException("Can't move to the oneself");
			callback.onError(e);
			future.completeExceptionally(e);
			return future;
		}

		if (checkStatus.getStatus() == 0) {
			future.completeExceptionally(new HiveException("moveTo failed"));
			return future;
		}

		//1. move to the path: using this.pathName, not a hash
		int LastPos = this.pathName.lastIndexOf("/");
		String name = this.pathName.substring(LastPos + 1);
		final String newPath = String.format("%s/%s", path, name);

		Status moveStatus = IPFSUtils.moveTo(rpcHelper, this.pathName, newPath);

		//2. using stat to get the new path's hash
		if (moveStatus.getStatus() == 0) {
			future.completeExceptionally(new HiveException("moveTo failed"));
			return future;
		}

		String homeHash = IPFSUtils.stat(rpcHelper, "/");

		//3. using name/publish to publish the hash
		if (homeHash == null) {
			future.completeExceptionally(new HiveException("The stat hash is invalid"));
			return future;
		}

		String url = String.format("%s%s", rpcHelper.getBaseUrl(), IPFSMethod.PUBLISH);
		Unirest.get(url)
			.header(IPFSURL.ContentType, IPFSURL.Json)
			.queryString(IPFSURL.UID, getId())
			.queryString(IPFSURL.PATH, homeHash)
			.asJsonAsync(new MoveToCallback(newPath, future, callback));

		return future;
	}
	
	@Override
	public CompletableFuture<Status> copyTo(String path) {
		return copyTo(path, new NullCallback<Status>());
	}

	@Override
	public CompletableFuture<Status> copyTo(String path, Callback<Status> callback) {
		return rpcHelper.checkExpired()
				.thenCompose(status -> copyTo(status, path, callback));
	}

	private CompletableFuture<Status> copyTo(Status checkStatus, String path, Callback<Status> callback) {
		CompletableFuture<Status> future = new CompletableFuture<Status>();
		if (callback == null)
			callback = new NullCallback<Status>();

		if (!path.startsWith("/")) {
			HiveException e = new HiveException("Path name must be a abosulte path");
			callback.onError(e);
			future.completeExceptionally(e);
			return future;
		}

		if (path.equals(this.pathName)) {
			HiveException e = new HiveException("Can't copy to the oneself");
			callback.onError(e);
			future.completeExceptionally(e);
			return future;
		}

		if (checkStatus.getStatus() == 0) {
			future.completeExceptionally(new HiveException("copyTo failed"));
			return null;
		}

		//1. using stat to get myself hash
		String hash = IPFSUtils.stat(rpcHelper, this.pathName);
		if (hash == null || hash.isEmpty()) {
			future.complete(new Status(0));
			return future;
		}

		//2. copy to the path
		int LastPos = this.pathName.lastIndexOf("/");
		String name = this.pathName.substring(LastPos + 1);
		final String newPath = String.format("%s/%s", path, name);
		Status copyStatus = IPFSUtils.copyTo(rpcHelper, hash, newPath);

		//3. using stat to get the new path's hash
		if (copyStatus.getStatus() == 0) {
			future.completeExceptionally(new HiveException("copy failed."));
			return null;
		}

		String homeHash = IPFSUtils.stat(rpcHelper, "/");

		//4. using name/publish to publish the hash
		if (homeHash == null) {
			future.completeExceptionally(new HiveException("The stat hash is invalid"));
			return future;
		}

		String url = String.format("%s%s", rpcHelper.getBaseUrl(), IPFSMethod.PUBLISH);
		Unirest.get(url)
			.header(IPFSURL.ContentType, IPFSURL.Json)
			.queryString(IPFSURL.UID, getId())
			.queryString(IPFSURL.PATH, homeHash)
			.asJsonAsync(new CopyToCallback(future, callback));

		return future;
	}
	
	@Override
	public CompletableFuture<Status> deleteItem() {
		return deleteItem(new NullCallback<Status>());
	}

	@Override
	public CompletableFuture<Status> deleteItem(Callback<Status> callback) {
		return rpcHelper.checkExpired()
				.thenCompose(status -> deleteItem(status, callback));
	}

	private CompletableFuture<Status> deleteItem(Status checkStatus, Callback<Status> callback) {
		CompletableFuture<Status> future = new CompletableFuture<Status>();

		if (callback == null)
			callback = new NullCallback<Status>();

		if (pathName.equals("/")) {
			HiveException e = new HiveException("Can't delete the root.");
			callback.onError(e);
			future.completeExceptionally(e);
			return future;
		}

		if (checkStatus.getStatus() == 0) {
			future.completeExceptionally(new HiveException("deleteItem failed"));
			return null;
		}

		//1. rm
		Status status = IPFSUtils.rm(rpcHelper, pathName);

		//2. using stat to get the path's hash
		if (status.getStatus() == 0) {
			future.completeExceptionally(new HiveException("Delete failed."));
			return null;
		}

		String homeHash = IPFSUtils.stat(rpcHelper, "/");

		//3. using name/publish to publish the hash
		if (homeHash == null) {
			future.completeExceptionally(new HiveException("The stat hash is invalid"));
			return future;
		}

		String url = String.format("%s%s", rpcHelper.getBaseUrl(), IPFSMethod.PUBLISH);
		Unirest.get(url)
			.header(IPFSURL.ContentType, IPFSURL.Json)
			.queryString(IPFSURL.UID, getId())
			.queryString(IPFSURL.PATH, homeHash)
			.asJsonAsync(new DeleteItemCallback(future, callback));

		return future;
	}
	
	@Override
	public void close() {
		// TODO Auto-generated method stub

	}

	private class GetFileInfoCallback implements UnirestAsyncCallback<JsonNode> {
		private final CompletableFuture<Info> future;
		private final Callback<Info> callback;

		GetFileInfoCallback(CompletableFuture<Info> future, Callback<Info> callback) {
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

			fileInfo = new Info(getId());
			this.callback.onSuccess(fileInfo);
			future.complete(fileInfo);
		}

		@Override
		public void failed(UnirestException exception) {
			HiveException ex = new HiveException(exception.getMessage());
			this.callback.onError(ex);
			future.completeExceptionally(ex);
		}
	}

	private class MoveToCallback implements UnirestAsyncCallback<JsonNode> {
		private final String pathName;
		private final CompletableFuture<Status> future;
		private final Callback<Status> callback;

		MoveToCallback(String pathName, CompletableFuture<Status> future, Callback<Status> callback) {
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

			IPFSFile.this.pathName = pathName;
			Status status = new Status(1);
			this.callback.onSuccess(status);
			future.complete(status);
		}

		@Override
		public void failed(UnirestException exception) {
			HiveException ex = new HiveException(exception.getMessage());
			this.callback.onError(ex);
			future.completeExceptionally(ex);
		}
	}

	private class CopyToCallback implements UnirestAsyncCallback<JsonNode> {
		private final CompletableFuture<Status> future;
		private final Callback<Status> callback;

		CopyToCallback(CompletableFuture<Status> future, Callback<Status> callback) {
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

			Status status = new Status(1);
			this.callback.onSuccess(status);
			future.complete(status);
		}

		@Override
		public void failed(UnirestException exception) {
			HiveException ex = new HiveException(exception.getMessage());
			this.callback.onError(ex);
			future.completeExceptionally(ex);
		}
	}

	private class DeleteItemCallback implements UnirestAsyncCallback<JsonNode> {
		private final CompletableFuture<Status> future;
		private final Callback<Status> callback;

		DeleteItemCallback(CompletableFuture<Status> future, Callback<Status> callback) {
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

			Status status = new Status(1);
			this.callback.onSuccess(status);
			future.complete(status);
		}

		@Override
		public void failed(UnirestException exception) {
			HiveException ex = new HiveException(exception.getMessage());
			this.callback.onError(ex);
			future.completeExceptionally(ex);
		}
	}
}
