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

	IPFSFile(String pathName, File.Info fileInfo) {
		this.fileInfo = fileInfo;
		this.pathName = pathName;
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
		CompletableFuture<Info> future = new CompletableFuture<Info>();

		if (callback == null)
			callback = new NullCallback<Info>();

		String url = String.format("%s%s", IPFSUtils.BASEURL, "files/stat");
		Unirest.get(url)
			.header(IPFSUtils.CONTENTTYPE, IPFSUtils.TYPE_Json)
			.queryString(IPFSUtils.UID, getId())
			.queryString(IPFSUtils.PATH, pathName)
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

		//1. move to the path: using this.pathName, not a hash
		int LastPos = this.pathName.lastIndexOf("/");
		String name = this.pathName.substring(LastPos + 1);
		final String newPath = String.format("%s/%s", path, name);
		CompletableFuture<Status> moveFuture = CompletableFuture.supplyAsync(() -> {
			return IPFSUtils.moveTo(getId(), this.pathName, newPath);
		});
		
		//2. using stat to get the new path's hash
		CompletableFuture<String> newStatFuture = moveFuture.thenCompose(status -> {
			return CompletableFuture.supplyAsync(() -> {
				if (status.getStatus() == 0) {
					future.completeExceptionally(new HiveException("moveTo failed"));
					return null;
				}

				return IPFSUtils.stat(getId(), "/");
			});
		});

		final Callback<Status> callbackForPublish = callback;
		//3. using name/publish to publish the hash
		newStatFuture.thenCompose(hash -> {
			return CompletableFuture.supplyAsync(() -> {
				if (hash == null) {
					future.completeExceptionally(new HiveException("The stat hash is invalid"));
					return future;
				}

				String url = String.format("%s%s", IPFSUtils.BASEURL, "name/publish");
				Unirest.get(url)
					.header(IPFSUtils.CONTENTTYPE, IPFSUtils.TYPE_Json)
					.queryString(IPFSUtils.UID, getId())
					.queryString(IPFSUtils.PATH, hash)
					.asJsonAsync(new MoveToCallback(newPath, future, callbackForPublish));

				return future;
			});
		});

		return future;
	}

	@Override
	public CompletableFuture<Status> copyTo(String path) {
		return copyTo(path, new NullCallback<Status>());
	}

	@Override
	public CompletableFuture<Status> copyTo(String path, Callback<Status> callback) {
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

		//1. using stat to get myself hash
		CompletableFuture<String> hashFuture = CompletableFuture.supplyAsync(() -> {
			return IPFSUtils.stat(getId(), this.pathName);
		});

		//2. copy to the path
		int LastPos = this.pathName.lastIndexOf("/");
		String name = this.pathName.substring(LastPos + 1);
		final String newPath = String.format("%s/%s", path, name);
		CompletableFuture<Status> copyFuture = hashFuture.thenCompose(hash -> {
			return CompletableFuture.supplyAsync(() -> {
				if (hash == null || hash.isEmpty()) {
					return new Status(0);
				}

				return IPFSUtils.copyTo(getId(), hash, newPath);
			});
		});

		//3. using stat to get the new path's hash
		CompletableFuture<String> newStatFuture = copyFuture.thenCompose(status -> {
			return CompletableFuture.supplyAsync(() -> {
				if (status.getStatus() == 0) {
					future.completeExceptionally(new HiveException("copy failed."));
					return null;
				}

				return IPFSUtils.stat(getId(), "/");
			});
		});

		final Callback<Status> callbackForPublish = callback;
		//4. using name/publish to publish the hash
		newStatFuture.thenCompose(hash -> {
			return CompletableFuture.supplyAsync(() -> {
				if (hash == null) {
					future.completeExceptionally(new HiveException("The stat hash is invalid"));
					return future;
				}

				String url = String.format("%s%s", IPFSUtils.BASEURL, "name/publish");
				Unirest.get(url)
					.header(IPFSUtils.CONTENTTYPE, IPFSUtils.TYPE_Json)
					.queryString(IPFSUtils.UID, getId())
					.queryString(IPFSUtils.PATH, hash)
					.asJsonAsync(new CopyToCallback(future, callbackForPublish));

				return future;
			});
		});

		return future;
	}

	@Override
	public CompletableFuture<Status> deleteItem() {
		return deleteItem(new NullCallback<Status>());
	}

	@Override
	public CompletableFuture<Status> deleteItem(Callback<Status> callback) {
		CompletableFuture<Status> future = new CompletableFuture<Status>();

		if (callback == null)
			callback = new NullCallback<Status>();

		if (pathName.equals("/")) {
			HiveException e = new HiveException("Can't delete the root.");
			callback.onError(e);
			future.completeExceptionally(e);
			return future;
		}

		//rm
		CompletableFuture<Status> deleteStatus = CompletableFuture.supplyAsync(() -> {
			return IPFSUtils.rm(getId(), pathName);
		});
		
		//using stat to get the path's hash
		CompletableFuture<String> hashFuture = deleteStatus.thenCompose(status -> {
			return CompletableFuture.supplyAsync(() -> {
				if (status.getStatus() == 0) {
					future.completeExceptionally(new HiveException("Delete failed."));
					return null;
				}

				return IPFSUtils.stat(getId(), "/");
			});
		});

		final Callback<Status> callbackForPublish = callback;
		//using name/publish to publish the hash
		hashFuture.thenCompose(hash -> {
			return CompletableFuture.supplyAsync(() -> {
				if (hash == null) {
					future.completeExceptionally(new HiveException("The stat hash is invalid"));
					return future;
				}

				String url = String.format("%s%s", IPFSUtils.BASEURL, "name/publish");
				Unirest.get(url)
					.header(IPFSUtils.CONTENTTYPE, IPFSUtils.TYPE_Json)
					.queryString(IPFSUtils.UID, getId())
					.queryString(IPFSUtils.PATH, hash)
					.asJsonAsync(new DeleteItemCallback(future, callbackForPublish));

				return future;
			});
		});

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
