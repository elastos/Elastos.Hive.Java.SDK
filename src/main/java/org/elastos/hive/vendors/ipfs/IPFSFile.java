package org.elastos.hive.vendors.ipfs;

import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.concurrent.CompletableFuture;

import org.elastos.hive.Callback;
import org.elastos.hive.File;
import org.elastos.hive.HiveException;
import org.elastos.hive.Length;
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
			HiveException e = new HiveException("Can't move to the oneself");
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
			HiveException e = new HiveException("Can't copy to the oneself");
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

			IPFSFile.this.pathName = pathName;
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

	@Override
	public CompletableFuture<Length> read(ByteBuffer dest) {
		// TODO
		return null;
	}

	@Override
	public CompletableFuture<Length> read(ByteBuffer dest, long position) {
		// TODO
		return null;
	}

	@Override
	public CompletableFuture<Length> read(ByteBuffer dest, long position, Callback<Length> callback) {
		// TODO
		return null;
	}

	@Override
	public CompletableFuture<Length> write(ByteBuffer dest) {
		// TODO
		return null;
	}

	@Override
	public CompletableFuture<Length> write(ByteBuffer dest, long position) {
		// TODO
		return null;
	}

	@Override
	public CompletableFuture<Length> write(ByteBuffer dest, long position, Callback<Length> callback) {
		// TODO
		return null;
	}

	@Override
	public CompletableFuture<Void> commit() {
		// TODO
		return null;
	}

	@Override
	public void discard() {
		// TODO

	}
}
