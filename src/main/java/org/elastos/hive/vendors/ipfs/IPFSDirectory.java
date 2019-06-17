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
import org.json.JSONArray;
import org.json.JSONObject;

import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;

class IPFSDirectory extends Directory  {
	private String pathName;
	private volatile Directory.Info dirInfo;
	private IPFSHelper ipfsHelper;

	IPFSDirectory(String pathName, Directory.Info dirInfo, IPFSHelper ipfsHelper) {
		this.pathName = pathName;
		this.dirInfo = dirInfo;
		this.ipfsHelper = ipfsHelper;
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

		Callback<Info> finalCallback = callback;
		ipfsHelper.checkValid().thenCompose(status -> {
			return CompletableFuture.supplyAsync(() -> {
				if (status.getStatus() == 0) {
					future.completeExceptionally(new HiveException("getInfo failed"));
					return null;
				}

				String url = String.format("%s%s", ipfsHelper.getBaseUrl(), IPFSMethod.STAT);
				Unirest.get(url)
					.header(IPFSURL.ContentType, IPFSURL.Json)
					.queryString(IPFSURL.UID, getId())
					.queryString(IPFSURL.PATH, pathName)
					.asJsonAsync(new GetDirInfoCallback(future, finalCallback));

				return future;
			});
		});

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

		Callback<Directory> finalCallback = callback;
		ipfsHelper.checkValid().thenCompose(checkStatus -> {
			return CompletableFuture.supplyAsync(() -> {
				if (checkStatus.getStatus() == 0) {
					future.completeExceptionally(new HiveException("createDirectory failed"));
					return null;
				}

				String pathName = String.format("%s/%s", this.pathName, path);
				//1. mkdir
				Status status = IPFSUtils.mkdir(ipfsHelper, pathName);

				//2. using stat to get the path's hash
				if (status.getStatus() == 0) {
					future.completeExceptionally(new HiveException("createDirectory failed"));
					return null;
				}

				String homeHash = IPFSUtils.stat(ipfsHelper, "/");

				//3. using name/publish to publish the hash
				if (homeHash == null) {
					future.completeExceptionally(new HiveException("The stat hash is invalid"));
					return future;
				}

				String url = String.format("%s%s", ipfsHelper.getBaseUrl(), IPFSMethod.PUBLISH);
				Unirest.get(url)
					.header(IPFSURL.ContentType, IPFSURL.Json)
					.queryString(IPFSURL.UID, getId())
					.queryString(IPFSURL.PATH, homeHash)
					.asJsonAsync(new CreateDirectoryCallback(pathName, future, finalCallback));

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

		Callback<Directory> finalCallback = callback;
		ipfsHelper.checkValid().thenCompose(status -> {
			return CompletableFuture.supplyAsync(() -> {
				if (status.getStatus() == 0) {
					future.completeExceptionally(new HiveException("getDirectory failed"));
					return null;
				}

				String pathName = String.format("%s/%s", this.pathName, path);
				//stat
				String url = String.format("%s%s", ipfsHelper.getBaseUrl(), IPFSMethod.STAT);
				Unirest.get(url)
					.header(IPFSURL.ContentType, IPFSURL.Json)
					.queryString(IPFSURL.UID, getId())
					.queryString(IPFSURL.PATH, pathName)
					.asJsonAsync(new GetDirectoryCallback(pathName, future, finalCallback));

				return future;
			});
		});

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

		Callback<File> finalCallback = callback;
		ipfsHelper.checkValid().thenCompose(checkStatus -> {
			return CompletableFuture.supplyAsync(() -> {
				if (checkStatus.getStatus() == 0) {
					future.completeExceptionally(new HiveException("createFile failed"));
					return null;
				}

				String pathName = String.format("%s/%s", this.pathName, path);

				//1. create file
				Status status = IPFSUtils.createEmptyFile(ipfsHelper, pathName);

				//2. using stat to get the path's hash
				if (status.getStatus() == 0) {
					future.completeExceptionally(new HiveException("Create File failed."));
					return null;
				}

				String homeHash = IPFSUtils.stat(ipfsHelper, "/");

				//using name/publish to publish the hash
				if (homeHash == null) {
					future.completeExceptionally(new HiveException("The stat hash is invalid"));
					return future;
				}

				String url = String.format("%s%s", ipfsHelper.getBaseUrl(), IPFSMethod.PUBLISH);
				Unirest.get(url)
					.header(IPFSURL.ContentType, IPFSURL.Json)
					.queryString(IPFSURL.UID, getId())
					.queryString(IPFSURL.PATH, homeHash)
					.asJsonAsync(new CreateFileCallback(pathName, future, finalCallback));

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

		Callback<File> finalCallback = callback;
		ipfsHelper.checkValid().thenCompose(status -> {
			return CompletableFuture.supplyAsync(() -> {
				if (status.getStatus() == 0) {
					future.completeExceptionally(new HiveException("getFile failed"));
					return null;
				}

				String pathName = String.format("%s/%s", this.pathName, path);
				String url = String.format("%s%s", ipfsHelper.getBaseUrl(), IPFSMethod.STAT);
				Unirest.get(url)
					.header(IPFSURL.ContentType, IPFSURL.Json)
					.queryString(IPFSURL.UID, getId())
					.queryString(IPFSURL.PATH, pathName)
					.asJsonAsync(new GetFileCallback(pathName, future, finalCallback));

				return future;
			});
		});

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

		Callback<Status> finalCallback = callback;
		ipfsHelper.checkValid().thenCompose(checkStatus -> {
			return CompletableFuture.supplyAsync(() -> {
				if (checkStatus.getStatus() == 0) {
					future.completeExceptionally(new HiveException("moveTo failed"));
					return null;
				}

				//1. move to the path: using this.pathName, not a hash
				int LastPos = this.pathName.lastIndexOf("/");
				String name = this.pathName.substring(LastPos + 1);
				final String newPath = String.format("%s/%s", path, name);
				Status status = IPFSUtils.moveTo(ipfsHelper, this.pathName, newPath);

				//2. using stat to get the new path's hash
				if (status.getStatus() == 0) {
					future.completeExceptionally(new HiveException("moveTo failed"));
					return null;
				}

				String homeHash = IPFSUtils.stat(ipfsHelper, "/");

				//3. using name/publish to publish the hash
				if (homeHash == null) {
					future.completeExceptionally(new HiveException("The stat hash is invalid"));
					return future;
				}

				String url = String.format("%s%s", ipfsHelper.getBaseUrl(), IPFSMethod.PUBLISH);
				Unirest.get(url)
					.header(IPFSURL.ContentType, IPFSURL.Json)
					.queryString(IPFSURL.UID, getId())
					.queryString(IPFSURL.PATH, homeHash)
					.asJsonAsync(new MoveToCallback(newPath, future, finalCallback));

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

		Callback<Status> finalCallback = callback;
		ipfsHelper.checkValid().thenCompose(checkStatus -> {
			return CompletableFuture.supplyAsync(() -> {
				if (checkStatus.getStatus() == 0) {
					future.completeExceptionally(new HiveException("copyTo failed"));
					return future;
				}

				//1. using stat to get myself hash
				String hash = IPFSUtils.stat(ipfsHelper, this.pathName);

				//2. copy to the path
				int LastPos = this.pathName.lastIndexOf("/");
				String name = this.pathName.substring(LastPos + 1);
				final String newPath = String.format("%s/%s", path, name);
				Status copyStatus = null;
				if (hash == null || hash.isEmpty()) {
					copyStatus = new Status(0);
				}

				copyStatus = IPFSUtils.copyTo(ipfsHelper, hash, newPath);

				//3. using stat to get the new path's hash
				if (copyStatus.getStatus() == 0) {
					future.completeExceptionally(new HiveException("copy failed."));
					return future;
				}

				String homeHash = IPFSUtils.stat(ipfsHelper, "/");

				//4. using name/publish to publish the hash
				if (homeHash == null) {
					future.completeExceptionally(new HiveException("The stat hash is invalid"));
					return future;
				}

				String url = String.format("%s%s", ipfsHelper.getBaseUrl(), IPFSMethod.PUBLISH);
				Unirest.get(url)
					.header(IPFSURL.ContentType, IPFSURL.Json)
					.queryString(IPFSURL.UID, getId())
					.queryString(IPFSURL.PATH, homeHash)
					.asJsonAsync(new CopyToCallback(future, finalCallback));

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

		Callback<Status> finalCallback = callback;
		ipfsHelper.checkValid().thenCompose(checkStatus -> {
			return CompletableFuture.supplyAsync(() -> {
				if (checkStatus.getStatus() == 0) {
					future.completeExceptionally(new HiveException("deleteItem failed"));
					return null;
				}

				//1. rm
				Status status = IPFSUtils.rm(ipfsHelper, pathName);

				//2. using stat to get the home hash
				if (status.getStatus() == 0) {
					future.completeExceptionally(new HiveException("Delete directory failed."));
					return null;
				}

				String homeHash = IPFSUtils.stat(ipfsHelper, "/");

				//3. using name/publish to publish the hash
				if (homeHash == null) {
					future.completeExceptionally(new HiveException("The stat hash is invalid"));
					return future;
				}

				String url = String.format("%s%s", ipfsHelper.getBaseUrl(), IPFSMethod.PUBLISH);
				Unirest.get(url)
					.header(IPFSURL.ContentType, IPFSURL.Json)
					.queryString(IPFSURL.UID, getId())
					.queryString(IPFSURL.PATH, homeHash)
					.asJsonAsync(new DeleteItemCallback(future, finalCallback));
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

		Callback<Children> finalCallback = callback;
		ipfsHelper.checkValid().thenCompose(checkSatus -> {
			return CompletableFuture.supplyAsync(() -> {
				if (checkSatus.getStatus() == 0) {
					future.completeExceptionally(new HiveException("getChildren failed"));
					return future;
				}

				//1. Using files/ls to get the nameList.
				ArrayList<String> nameList = null;
				try {
					String url = ipfsHelper.getBaseUrl() + IPFSMethod.LS;

					HttpResponse<JsonNode> response = Unirest.get(url)
						.header(IPFSURL.ContentType, IPFSURL.Json)
						.queryString(IPFSURL.UID, getId())
						.queryString(IPFSURL.PATH, this.pathName)
						.asJson();
					if (response.getStatus() == 200) {
						nameList = getNameList(pathName, response.getBody().getObject());
					}
				} catch (Exception ex) {
					HiveException e = new HiveException("GetChildren failed [ls]");
					finalCallback.onError(e);
					future.completeExceptionally(e);
				}

				//2. Using stat to get the child's type.
				if (nameList == null) {
					future.completeExceptionally(new HiveException("GetChildren failed"));
					return future;
				}

				try {
					int len = nameList.size();
					ArrayList<Object> childList = new ArrayList<Object>(len);
					if (len > 0) {
						for (int i = 0; i < len; i++) {
							String childPath = nameList.get(i);

							//Check the item whether is a folder or a file: 0 is folder
							if (IPFSUtils.isFolder(ipfsHelper, childPath)) {
								Directory.Info dirInfo = new Directory.Info(getId());
								IPFSDirectory directory = new IPFSDirectory(childPath, dirInfo, ipfsHelper);
								childList.add(directory);
							} else {
								File.Info fileInfo = new File.Info(getId());
								IPFSFile file = new IPFSFile(childPath, fileInfo, ipfsHelper);
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

	private ArrayList<String> getNameList(String parentPath, JSONObject baseJson) {
		JSONArray entries = null;
		try {
			entries = baseJson.getJSONArray("Entries");
		} catch (Exception e) {
			e.printStackTrace();
		}

		int len = 0;
		if (entries != null) {
			len = entries.length();
		}

		ArrayList<String> nameList = new ArrayList<String>(len);
		if (len > 0) {
			for (int i = 0; i < len; i++) {
				JSONObject itemJson = entries.getJSONObject(i);
				String name = itemJson.getString("Name");
				nameList.add(String.format("%s/%s", parentPath, name));
			}
		}

		return nameList;
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
			IPFSDirectory directory = new IPFSDirectory(pathName, dirInfo, ipfsHelper);
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
			IPFSDirectory directory = new IPFSDirectory(pathName, dirInfo, ipfsHelper);
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
			IPFSFile file = new IPFSFile(pathName, fileInfo, ipfsHelper);
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
			IPFSFile file = new IPFSFile(pathName, fileInfo, ipfsHelper);
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
