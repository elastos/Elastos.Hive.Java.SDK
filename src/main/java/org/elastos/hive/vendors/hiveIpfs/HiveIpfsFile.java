package org.elastos.hive.vendors.hiveIpfs;

import java.util.concurrent.CompletableFuture;

import org.elastos.hive.Callback;
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

final class HiveIpfsFile extends File {
	private String pathName;
	private volatile File.Info fileInfo;

	HiveIpfsFile(String pathName, File.Info fileInfo) {
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

		Unirest.get(HiveIpfsUtils.BASEURL)
			.header(HiveIpfsUtils.CONTENTTYPE, HiveIpfsUtils.TYPE_Json)
			.queryString(HiveIpfsUtils.UID, getId())
			.queryString(HiveIpfsUtils.PATH, pathName)
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
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public CompletableFuture<Status> moveTo(String path, Callback<Status> callback) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public CompletableFuture<Status> copyTo(String path) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public CompletableFuture<Status> copyTo(String path, Callback<Status> callback) {
		// TODO Auto-generated method stub
		return null;
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
		CompletableFuture<Status> mkdirStatus = CompletableFuture.supplyAsync(() -> {
			return HiveIpfsUtils.rm(getId(), pathName);
		});
		
		//using stat to get the path's hash
		CompletableFuture<String> hashFuture = mkdirStatus.thenCompose(status -> {
			return CompletableFuture.supplyAsync(() -> {
				if (status.getStatus() == 0) {
					return null;
				}

				return HiveIpfsUtils.stat(getId(), "/");
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

				String url = String.format("%s%s", HiveIpfsUtils.BASEURL, "name/publish");
				Unirest.get(url)
					.header(HiveIpfsUtils.CONTENTTYPE, HiveIpfsUtils.TYPE_Json)
					.queryString(HiveIpfsUtils.UID, getId())
					.queryString(HiveIpfsUtils.PATH, hash)
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

			JSONObject jsonObject = response.getBody().getObject();
			fileInfo = new Info(jsonObject.getString("Hash"));
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
