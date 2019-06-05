package org.elastos.hive.vendors.hiveIpfs;

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

import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;

final class HiveIpfsDrive extends Drive{
	private volatile Drive.Info driveInfo;

	HiveIpfsDrive(Drive.Info driveInfo) {
		this.driveInfo = driveInfo;
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
		CompletableFuture<Info> future = new CompletableFuture<Info>();

		if (callback == null)
			callback = new NullCallback<Info>();

		String url = String.format("%s%s", HiveIpfsUtils.BASEURL, "files/stat");
		Unirest.get(url)
			.header(HiveIpfsUtils.CONTENTTYPE, HiveIpfsUtils.TYPE_Json)
			.queryString(HiveIpfsUtils.UID, getId())
			.queryString(HiveIpfsUtils.PATH, "/")
			.asJsonAsync(new GetInfoCallback(future, callback));

		return future;
	}

	@Override
	public CompletableFuture<Directory> createDirectory(String path) {
		return createDirectory(path, new NullCallback<Directory>());
	}

	@Override
	public CompletableFuture<Directory> createDirectory(String path, Callback<Directory> callback) {
		CompletableFuture<Directory> future = new CompletableFuture<Directory>();

		if (callback == null)
			callback = new NullCallback<Directory>();
		
		final Callback<Directory> callbackForPublish = callback;

		if (!path.startsWith("/")) {
			HiveException e = new HiveException("Path name must be a abosulte path");
			callback.onError(e);
			future.completeExceptionally(e);
			return future;
		}

		//mkdir
		CompletableFuture<Status> mkdirStatus = CompletableFuture.supplyAsync(() -> {
			return HiveIpfsUtils.mkdir(getId(), path);
		});
		
		//using stat to get the path's hash
		CompletableFuture<String> hashFuture = mkdirStatus.thenCompose(status -> {
			return CompletableFuture.supplyAsync(() -> {
				if (status.getStatus() == 0) {
					return null;
				}

				return HiveIpfsUtils.stat(getId(), path);
			});
		});

		//using name/publish to publish the hash
		hashFuture.thenCompose(hash -> {
			return CompletableFuture.supplyAsync(() -> {
				String url = String.format("%s%s", HiveIpfsUtils.BASEURL, "name/publish");
				Unirest.get(url)
					.header(HiveIpfsUtils.CONTENTTYPE, HiveIpfsUtils.TYPE_Json)
					.queryString(HiveIpfsUtils.UID, getId())
					.queryString(HiveIpfsUtils.PATH, hash)
					.asJsonAsync(new CreateDirectoryCallback(path, future, callbackForPublish));

				return future;
			});
		});

		return future;
	}

	@Override
	public CompletableFuture<Directory> getDirectory(String path) {
		return getDirectory(path, new NullCallback<Directory>());
	}

	@Override
	public CompletableFuture<Directory> getDirectory(String path, Callback<Directory> callback) {
		CompletableFuture<Directory> future = new CompletableFuture<Directory>();

		if (callback == null)
			callback = new NullCallback<Directory>();

		final Callback<Directory> callbackForPublish = callback;

		if (!path.startsWith("/")) {
			HiveException e = new HiveException("Path name must be a abosulte path");
			callback.onError(e);
			future.completeExceptionally(e);
			return future;
		}

		//stat
		String url = String.format("%s%s", HiveIpfsUtils.BASEURL, "files/stat");
		Unirest.get(url)
			.header(HiveIpfsUtils.CONTENTTYPE, HiveIpfsUtils.TYPE_Json)
			.queryString(HiveIpfsUtils.UID, getId())
			.queryString(HiveIpfsUtils.PATH, path)
			.asJsonAsync(new GetDirectoryCallback(path, future, callbackForPublish));

		return future;
	}

	@Override
	public CompletableFuture<File> createFile(String path) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public CompletableFuture<File> createFile(String path, Callback<File> callback) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public CompletableFuture<File> getFile(String path) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public CompletableFuture<File> getFile(String path, Callback<File> callback) {
		// TODO Auto-generated method stub
		return null;
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
			HiveIpfsDirectory directory = new HiveIpfsDirectory(pathName, dirInfo);
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

			Directory.Info dirInfo = new Directory.Info(getId());
			HiveIpfsDirectory directory = new HiveIpfsDirectory(pathName, dirInfo);
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
}
