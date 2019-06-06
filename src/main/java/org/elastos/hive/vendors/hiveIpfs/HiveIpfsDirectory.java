package org.elastos.hive.vendors.hiveIpfs;

import java.util.concurrent.CompletableFuture;

import org.elastos.hive.Callback;
import org.elastos.hive.Children;
import org.elastos.hive.Directory;
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

class HiveIpfsDirectory extends Directory  {
	private String pathName;
	private volatile Directory.Info dirInfo;

	HiveIpfsDirectory(String pathName, Directory.Info dirInfo) {
		this.pathName = pathName;
		this.dirInfo = dirInfo;
	}

	@Override
	public String getId() {
		return dirInfo.getId();
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
		CompletableFuture<Directory.Info> future = new CompletableFuture<Directory.Info>();

		if (callback == null)
			callback = new NullCallback<Directory.Info>();

		String url = String.format("%s%s", HiveIpfsUtils.BASEURL, "files/stat");
		Unirest.get(url)
			.header(HiveIpfsUtils.CONTENTTYPE, HiveIpfsUtils.TYPE_Json)
			.queryString(HiveIpfsUtils.UID, getId())
			.queryString(HiveIpfsUtils.PATH, pathName)
			.asJsonAsync(new GetDirInfoCallback(future, callback));

		return future;
	}

	@Override
	public CompletableFuture<Directory> createDirectory(String path) {
		return  createDirectory(path, new NullCallback<Directory>());
	}

	@Override
	public CompletableFuture<Directory> createDirectory(String path, Callback<Directory> callback) {
		CompletableFuture<Directory> future = new CompletableFuture<Directory>();

		if (callback == null)
			callback = new NullCallback<Directory>();

		if (path.contains("/")) {
			HiveException e = new HiveException("Only need the path name");
			callback.onError(e);
			future.completeExceptionally(e);
			return future;
		}

		String pathName = String.format("%s/%s", this.pathName, path);
		//mkdir
		CompletableFuture<Status> mkdirStatus = CompletableFuture.supplyAsync(() -> {
			return HiveIpfsUtils.mkdir(getId(), pathName);
		});

		//using stat to get the path's hash
		CompletableFuture<String> hashFuture = mkdirStatus.thenCompose(status -> {
			return CompletableFuture.supplyAsync(() -> {
				if (status.getStatus() == 0) {
					return null;
				}

				return HiveIpfsUtils.stat(getId(), pathName);
			});
		});

		final Callback<Directory> callbackForPublish = callback;
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
					.asJsonAsync(new CreateDirectoryCallback(pathName, future, callbackForPublish));

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

		if (path.contains("/")) {
			HiveException e = new HiveException("Only need the name of a directory.");
			callback.onError(e);
			future.completeExceptionally(e);
			return future;
		}

		String pathName = String.format("%s/%s", this.pathName, path);
		//stat
		String url = String.format("%s%s", HiveIpfsUtils.BASEURL, "files/stat");
		Unirest.get(url)
			.header(HiveIpfsUtils.CONTENTTYPE, HiveIpfsUtils.TYPE_Json)
			.queryString(HiveIpfsUtils.UID, getId())
			.queryString(HiveIpfsUtils.PATH, pathName)
			.asJsonAsync(new GetDirectoryCallback(pathName, future, callback));

		return future;
	}

	@Override
	public CompletableFuture<File> createFile(String path) {
		return createFile(path, new NullCallback<File>());
	}

	@Override
	public CompletableFuture<File> createFile(String path, Callback<File> callback) {
		CompletableFuture<File> future = new CompletableFuture<File>();

		if (callback == null)
			callback = new NullCallback<File>();

		if (path.contains("/")) {
			HiveException e = new HiveException("Only need the name of a file.");
			callback.onError(e);
			future.completeExceptionally(e);
			return future;
		}

		String pathName = String.format("%s/%s", this.pathName, path);
		//create file
		CompletableFuture<Status> createStatus = CompletableFuture.supplyAsync(() -> {
			return HiveIpfsUtils.createEmptyFile(getId(), pathName);
		});

		//using stat to get the path's hash
		CompletableFuture<String> hashFuture = createStatus.thenCompose(status -> {
			return CompletableFuture.supplyAsync(() -> {
				if (status.getStatus() == 0) {
					return null;
				}

				return HiveIpfsUtils.stat(getId(), pathName);
			});
		});

		final Callback<File> callbackForPublish = callback;
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
					.asJsonAsync(new CreateFileCallback(pathName, future, callbackForPublish));

				return future;
			});
		});

		return future;
	}

	@Override
	public CompletableFuture<File> getFile(String path) {
		return getFile(path, new NullCallback<File>());
	}

	@Override
	public CompletableFuture<File> getFile(String path, Callback<File> callback) {
		CompletableFuture<File> future = new CompletableFuture<File>();

		if (callback == null)
			callback = new NullCallback<File>();

		if (path.contains("/")) {
			HiveException e = new HiveException("Only need the name of a file.");
			callback.onError(e);
			future.completeExceptionally(e);
			return future;
		}

		String pathName = String.format("%s/%s", this.pathName, path);
		String url = String.format("%s%s", HiveIpfsUtils.BASEURL, "files/stat");
		Unirest.get(url)
			.header(HiveIpfsUtils.CONTENTTYPE, HiveIpfsUtils.TYPE_Json)
			.queryString(HiveIpfsUtils.UID, getId())
			.queryString(HiveIpfsUtils.PATH, pathName)
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
	public CompletableFuture<Status> moveTo(String path) {
		return moveTo(pathName, new NullCallback<Status>());
	}

	@Override
	public CompletableFuture<Status> moveTo(String path, Callback<Status> callback) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public CompletableFuture<Status> copyTo(String path) {
		return copyTo(pathName, new NullCallback<Status>());
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

	@Override
	public CompletableFuture<Children> getChildren() {
		return getChildren(new NullCallback<Children>());
	}

	@Override
	public CompletableFuture<Children> getChildren(Callback<Children> callback) {
		// TODO Auto-generated method stub
		return null;
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
				HiveException ex = new HiveException("Server Error: " + response.getStatusText());
				this.callback.onError(ex);
				future.completeExceptionally(ex);
				return;
			}

			JSONObject jsonObject = response.getBody().getObject();
			dirInfo = new Directory.Info(jsonObject.getString("Hash"));
			this.callback.onSuccess(dirInfo);
			future.complete(dirInfo);
		}

		@Override
		public void failed(UnirestException exception) {
			HiveException ex = new HiveException(exception.getMessage());
			this.callback.onError(ex);
			future.completeExceptionally(ex);
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

			JSONObject jsonObject = response.getBody().getObject();
			String type = jsonObject.getString("Type");
			if (!HiveIpfsUtils.isFolder(type)) {
				HiveException ex = new HiveException("This is not a directory");
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
			HiveIpfsFile file = new HiveIpfsFile(pathName, fileInfo);
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
			if (!HiveIpfsUtils.isFile(type)) {
				HiveException ex = new HiveException("This is not a file");
				this.callback.onError(ex);
				future.completeExceptionally(ex);
				return;
			}

			File.Info fileInfo = new File.Info(getId());
			HiveIpfsFile file = new HiveIpfsFile(pathName, fileInfo);
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
