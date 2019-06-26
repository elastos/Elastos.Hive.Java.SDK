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
		return rpcHelper.checkExpiredNew()
				.thenCompose(value -> moveTo(value, path, callback))
				.thenCompose(value -> rpcHelper.getRootHash(value))
				.thenCompose(value -> rpcHelper.publishHash(value))
				.thenCompose(value -> rpcHelper.invokeVoidCallback(value));
	}

	private CompletableFuture<PackValue> moveTo(PackValue value, String path, Callback<Void> callback) {
		CompletableFuture<PackValue> future = new CompletableFuture<PackValue>();
		if (value.getException() != null) {
			future.completeExceptionally(value.getException());
			return future;
		}

		if (callback == null)
			callback = new NullCallback<Void>();
		
		value.setCallback(callback);

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
			HiveException e = new HiveException("Can't move to the oneself");
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
		if (value.getException() != null) {
			future.completeExceptionally(value.getException());
			return future;
		}

		if (callback == null)
			callback = new NullCallback<Void>();
		
		value.setCallback(callback);

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
		if (value.getException() != null) {
			future.completeExceptionally(value.getException());
			return future;
		}

		if (callback == null)
			callback = new NullCallback<Void>();

		value.setCallback(callback);
		
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

			PackValue padding = new PackValue();
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

			IPFSFile.this.pathName = pathName;
			PackValue padding = new PackValue();
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

			PackValue padding = new PackValue();
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
