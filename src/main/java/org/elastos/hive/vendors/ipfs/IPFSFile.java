package org.elastos.hive.vendors.ipfs;

import java.util.HashMap;
import java.util.concurrent.CompletableFuture;

import org.elastos.hive.Callback;
import org.elastos.hive.File;
import org.elastos.hive.HiveException;
import org.elastos.hive.NullCallback;
import org.elastos.hive.UnirestAsyncCallback;
import org.elastos.hive.Void;

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
		return fileInfo.get(File.Info.itemId);
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

	private CompletableFuture<Info> getInfo(Void status, Callback<Info> callback) {
		CompletableFuture<Info> future = new CompletableFuture<Info>();

		if (callback == null)
			callback = new NullCallback<Info>();

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
	public CompletableFuture<Void> moveTo(String path) {
		return moveTo(path, new NullCallback<Void>());
	}

	@Override
	public CompletableFuture<Void> moveTo(String path, Callback<Void> callback) {
		return rpcHelper.checkExpired()
				.thenCompose(placeHolder -> moveTo(placeHolder, path, callback));
	}

	private CompletableFuture<Void> moveTo(Void placeHolder, String path, Callback<Void> callback) {
		CompletableFuture<Void> future = new CompletableFuture<Void>();
		if (callback == null)
			callback = new NullCallback<Void>();

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

		//1. move to the path: using this.pathName, not a hash
		int LastPos = this.pathName.lastIndexOf("/");
		String name = this.pathName.substring(LastPos + 1);
		final String newPath = String.format("%s/%s", path, name);

		//Void moveStatus = IPFSUtils.moveTo(rpcHelper, this.pathName, newPath);

		//2. using stat to get the new path's hash
		/* if (moveStatus.getStatus() == 0) {
			future.completeExceptionally(new HiveException("moveTo failed"));
			return future;
		}*/

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
	public CompletableFuture<Void> copyTo(String path) {
		return copyTo(path, new NullCallback<Void>());
	}

	@Override
	public CompletableFuture<Void> copyTo(String path, Callback<Void> callback) {
		return rpcHelper.checkExpired()
				.thenCompose(placeHolder -> copyTo(placeHolder, path, callback));
	}

	private CompletableFuture<Void> copyTo(Void placeHolder, String path, Callback<Void> callback) {
		CompletableFuture<Void> future = new CompletableFuture<Void>();
		if (callback == null)
			callback = new NullCallback<Void>();

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

		//1. using stat to get myself hash
		/* String hash = IPFSUtils.stat(rpcHelper, this.pathName);
		if (hash == null || hash.isEmpty()) {
			future.complete(new Void(0));
			return future;
		}*/

		//2. copy to the path
		int LastPos = this.pathName.lastIndexOf("/");
		String name = this.pathName.substring(LastPos + 1);
		final String newPath = String.format("%s/%s", path, name);
		//Void copyStatus = IPFSUtils.copyTo(rpcHelper, hash, newPath);

		//3. using stat to get the new path's hash
		/*if (copyStatus.getStatus() == 0) {
			future.completeExceptionally(new HiveException("copy failed."));
			return null;
		}*/

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
	public CompletableFuture<Void> deleteItem() {
		return deleteItem(new NullCallback<Void>());
	}

	@Override
	public CompletableFuture<Void> deleteItem(Callback<Void> callback) {
		return rpcHelper.checkExpired()
				.thenCompose(placeHolder -> deleteItem(placeHolder, callback));
	}

	private CompletableFuture<Void> deleteItem(Void placeHolder, Callback<Void> callback) {
		CompletableFuture<Void> future = new CompletableFuture<Void>();

		if (callback == null)
			callback = new NullCallback<Void>();

		if (pathName.equals("/")) {
			HiveException e = new HiveException("Can't delete the root.");
			callback.onError(e);
			future.completeExceptionally(e);
			return future;
		}

		//1. rm
		//Void status = IPFSUtils.rm(rpcHelper, pathName);

		//2. using stat to get the path's hash
		/*if (status.getStatus() == 0) {
			future.completeExceptionally(new HiveException("Delete failed."));
			return null;
		}*/

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

			HashMap<String, String> attrs = new HashMap<>();
			attrs.put(File.Info.itemId, getId()); // TODO:
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
		private final CompletableFuture<Void> future;
		private final Callback<Void> callback;

		MoveToCallback(String pathName, CompletableFuture<Void> future, Callback<Void> callback) {
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
			Void placeHolder = new Void();
			this.callback.onSuccess(placeHolder);
			future.complete(placeHolder);
		}

		@Override
		public void failed(UnirestException exception) {
			HiveException e = new HiveException(exception.getMessage());
			this.callback.onError(e);
			future.completeExceptionally(e);
		}
	}

	private class CopyToCallback implements UnirestAsyncCallback<JsonNode> {
		private final CompletableFuture<Void> future;
		private final Callback<Void> callback;

		CopyToCallback(CompletableFuture<Void> future, Callback<Void> callback) {
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

	private class DeleteItemCallback implements UnirestAsyncCallback<JsonNode> {
		private final CompletableFuture<Void> future;
		private final Callback<Void> callback;

		DeleteItemCallback(CompletableFuture<Void> future, Callback<Void> callback) {
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
