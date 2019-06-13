package org.elastos.hive.vendors.ipfs;

import java.util.ArrayList;
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

class IPFSDirectory extends Directory  {
	private String pathName;
	private volatile Directory.Info dirInfo;

	IPFSDirectory(String pathName, Directory.Info dirInfo) {
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

		String url = String.format("%s%s", IPFSUtils.BASEURL, "files/stat");
		Unirest.get(url)
			.header(IPFSUtils.CONTENTTYPE, IPFSUtils.TYPE_Json)
			.queryString(IPFSUtils.UID, getId())
			.queryString(IPFSUtils.PATH, pathName)
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
			return IPFSUtils.mkdir(getId(), pathName);
		});

		//using stat to get the path's hash
		CompletableFuture<String> hashFuture = mkdirStatus.thenCompose(status -> {
			return CompletableFuture.supplyAsync(() -> {
				if (status.getStatus() == 0) {
					future.completeExceptionally(new HiveException("createDirectory failed"));
					return null;
				}

				return IPFSUtils.stat(getId(), "/");
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

				String url = String.format("%s%s", IPFSUtils.BASEURL, "name/publish");
				Unirest.get(url)
					.header(IPFSUtils.CONTENTTYPE, IPFSUtils.TYPE_Json)
					.queryString(IPFSUtils.UID, getId())
					.queryString(IPFSUtils.PATH, hash)
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
		String url = String.format("%s%s", IPFSUtils.BASEURL, "files/stat");
		Unirest.get(url)
			.header(IPFSUtils.CONTENTTYPE, IPFSUtils.TYPE_Json)
			.queryString(IPFSUtils.UID, getId())
			.queryString(IPFSUtils.PATH, pathName)
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
			return IPFSUtils.createEmptyFile(getId(), pathName);
		});

		//using stat to get the path's hash
		CompletableFuture<String> hashFuture = createStatus.thenCompose(status -> {
			return CompletableFuture.supplyAsync(() -> {
				if (status.getStatus() == 0) {
					future.completeExceptionally(new HiveException("Create File failed."));
					return null;
				}

				return IPFSUtils.stat(getId(), "/");
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

				String url = String.format("%s%s", IPFSUtils.BASEURL, "name/publish");
				Unirest.get(url)
					.header(IPFSUtils.CONTENTTYPE, IPFSUtils.TYPE_Json)
					.queryString(IPFSUtils.UID, getId())
					.queryString(IPFSUtils.PATH, hash)
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
		String url = String.format("%s%s", IPFSUtils.BASEURL, "files/stat");
		Unirest.get(url)
			.header(IPFSUtils.CONTENTTYPE, IPFSUtils.TYPE_Json)
			.queryString(IPFSUtils.UID, getId())
			.queryString(IPFSUtils.PATH, pathName)
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
			HiveException e = new HiveException("Can't move to the oneself directory");
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
			HiveException e = new HiveException("Can't copy to the oneself directory");
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
					future.completeExceptionally(new HiveException("Delete directory failed."));
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

	@Override
	public CompletableFuture<Children> getChildren() {
		return getChildren(new NullCallback<Children>());
	}

	@Override
	public CompletableFuture<Children> getChildren(Callback<Children> callback) {
		CompletableFuture<Children> future = new CompletableFuture<Children>();

		if (callback == null)
			callback = new NullCallback<Children>();

		final Callback<Children> finalCallback = callback;
		//1. Using files/ls to get the nameList.
		CompletableFuture<ArrayList<String>> nameList = CompletableFuture.supplyAsync(() -> {
			try {
				String url = IPFSUtils.BASEURL + "files/ls";

				HttpResponse<JsonNode> response = Unirest.get(url)
					.header(IPFSUtils.CONTENTTYPE, IPFSUtils.TYPE_Json)
					.queryString(IPFSUtils.UID, getId())
					.queryString(IPFSUtils.PATH, this.pathName)
					.asJson();
				if (response.getStatus() == 200) {
					return IPFSUtils.getNameList(pathName, response.getBody().getObject());
				}
			} catch (Exception ex) {
				HiveException e = new HiveException("GetChildren failed [ls]");
				finalCallback.onError(e);
				future.completeExceptionally(e);
			}

			return null;
		});

		//2. Using stat to get the child's type.
		nameList.thenCompose(names -> {
			return CompletableFuture.supplyAsync(() -> {
				if (names == null) {
					future.completeExceptionally(new HiveException("GetChildren failed"));
					return future;
				}

				try {
					int len = names.size();
					ArrayList<Object> childList = new ArrayList<Object>(len);
					if (len > 0) {
						for (int i = 0; i < len; i++) {
							String childPath = names.get(i);

							//Check the item whether is a folder or a file: 0 is folder
							if (IPFSUtils.isFolder(getId(), childPath)) {
								Directory.Info dirInfo = new Directory.Info(getId());
								IPFSDirectory directory = new IPFSDirectory(childPath, dirInfo);
								childList.add(directory);
							} else {
								File.Info fileInfo = new File.Info(getId());
								IPFSFile file = new IPFSFile(childPath, fileInfo);
								childList.add(file);
							}
						}
					}

					Children children = new Children(childList);
					finalCallback.onSuccess(children);
					future.complete(children);
				} 
				catch (Exception ex) {
					finalCallback.onSuccess(null);
					future.completeExceptionally(new HiveException("GetChildren failed [child type]"));
				}

				return future;
			});
		});

		return future;
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

			dirInfo = new Directory.Info(getId());
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
			IPFSDirectory directory = new IPFSDirectory(pathName, dirInfo);
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
			IPFSDirectory directory = new IPFSDirectory(pathName, dirInfo);
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
			IPFSFile file = new IPFSFile(pathName, fileInfo);
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
			IPFSFile file = new IPFSFile(pathName, fileInfo);
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

			IPFSDirectory.this.pathName = pathName;
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
}
