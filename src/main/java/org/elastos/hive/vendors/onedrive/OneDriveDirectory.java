package org.elastos.hive.vendors.onedrive;

import java.util.ArrayList;
import java.util.concurrent.CompletableFuture;

import org.elastos.hive.AuthHelper;
import org.elastos.hive.Callback;
import org.elastos.hive.Children;
import org.elastos.hive.Directory;
import org.elastos.hive.DirectoryInfo;
import org.elastos.hive.File;
import org.elastos.hive.FileInfo;
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

class OneDriveDirectory implements Directory {
	private final AuthHelper authHelper;
	private final String dirId;
	private DirectoryInfo dirInfo;
	private String pathName;

	OneDriveDirectory(DirectoryInfo dirInfo, AuthHelper authHelper) {
		this.authHelper = authHelper;
		this.dirId = dirInfo.getId();
		this.dirInfo = dirInfo;
	}

	@Override
	public String getId() {
		return dirId;
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
	public DirectoryInfo getLastInfo() {
		return dirInfo;
	}

	@Override
	public CompletableFuture<DirectoryInfo> getInfo() {
		return getInfo(new NullCallback<DirectoryInfo>());
	}

	@Override
	public CompletableFuture<DirectoryInfo> getInfo(Callback<DirectoryInfo> callback) {
		CompletableFuture<DirectoryInfo> future = new CompletableFuture<DirectoryInfo>();

		if (callback == null)
			callback = new NullCallback<DirectoryInfo>();

		String url = String.format("%s/root:/%s", OneDriveURL.API, pathName)
						   .replace(" ", "%20");
		Unirest.get(url)
			.header(OneDriveHttpHeader.Authorization, OneDriveHttpHeader.bearerValue(authHelper))
			.asJsonAsync(new GetDirInfoCallback(future, callback));

		return future;
	}

	@Override
	public CompletableFuture<Directory> moveTo(String pathName) {
		return moveTo(pathName, new NullCallback<Directory>());
	}

	@Override
	public CompletableFuture<Directory> moveTo(String pathName, Callback<Directory> callback) {
		CompletableFuture<Directory> future = new CompletableFuture<Directory>();

		if (callback == null)
			callback = new NullCallback<Directory>();

		try {
			int LastPos = this.pathName.lastIndexOf("/");
			String name = this.pathName.substring(LastPos + 1);

			String url  = String.format("%s/items/%s", OneDriveURL.API, dirId)
								.replace(" ", "%20");
			String body = String.format("{\"parentReference\": \"path\": \"%s\"name\":\"%s\"}",
										pathName, name);

			Unirest.patch(url)
				.header(OneDriveHttpHeader.Authorization,
						OneDriveHttpHeader.bearerValue(authHelper))
				.header("Content-Type", "application/json")
				.body(body)
				.asJsonAsync(new MoveToCallback(future, callback));
		} catch (Exception e) {
			HiveException ex = new HiveException(e.getMessage());
			callback.onError(ex);
			future.completeExceptionally(ex);
		}

		return future;
	}

	@Override
	public CompletableFuture<Directory> copyTo(String pathName) {
		return copyTo(pathName, new NullCallback<Directory>());
	}

	@Override
	public CompletableFuture<Directory> copyTo(String pathName, Callback<Directory> callback) {
		CompletableFuture<Directory> future = new CompletableFuture<Directory>();

		if (callback == null)
			callback = new NullCallback<Directory>();

		try {
			int LastPos = this.pathName.lastIndexOf("/");
			String name = this.pathName.substring(LastPos + 1);

			String url  = String.format("%s/items/%s/copy", OneDriveURL.API, dirId)
							    .replace(" ", "%20");
			String body = "{\"parentReference\": {\"path\":\""
					 + pathName + "\"},\"name\": \"" + name + "\"}";

			Unirest.post(url)
				.header(OneDriveHttpHeader.Authorization,
						OneDriveHttpHeader.bearerValue(authHelper))
				.header("Content-Type", "application/json")
				.body(body)
				.asJsonAsync(new CopyToCallback(future, callback));
		} catch (Exception e) {
			HiveException ex = new HiveException(e.getMessage());
			callback.onError(ex);
			future.completeExceptionally(ex);
		}

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

		String url = String.format("%s/items/%s", OneDriveURL.API, dirId)
						   .replace(" ", "%20");

		Unirest.delete(url)
			.header(OneDriveHttpHeader.Authorization,
					OneDriveHttpHeader.bearerValue(authHelper))
			.asJsonAsync(new DeleteItemCallback(future, callback));

		return future;
	}

	@Override
	public void close() {
		// TODO
	}

	@Override
	public CompletableFuture<Directory> createDirectory(String name) {
		return  createDirectory(name, new NullCallback<Directory>());
	}

	@Override
	public CompletableFuture<Directory> createDirectory(String name, Callback<Directory> callback) {
		CompletableFuture<Directory> future = new CompletableFuture<Directory>();

		if (callback == null)
			callback = new NullCallback<Directory>();

		if (name.equals("/")) {
			HiveException e = new HiveException("Can't create root dirctory");
			callback.onError(e);
			future.completeExceptionally(e);
			return future;
		}

		String url = String.format("%s/items/%s/children", OneDriveURL.API, dirId)
						   .replace(" ", "%20");

		//conflictBehavior' value : fail, replace, or rename
		String body = "{\"name\": \"" + name + "\", \"folder\": { }, \"@microsoft.graph.conflictBehavior\": \"fail\"}";

		Unirest.post(url)
			.header("Authorization",  "bearer " + authHelper.getToken().getAccessToken())
			.header("Content-Type", "application/json")
			.body(body)
			.asJsonAsync(new CreateDirCallback(future, callback));

		return future;
	}

	@Override
	public CompletableFuture<Directory> getDirectory(String name) {
		return getDirectory(name, new NullCallback<Directory>());
	}

	@Override
	public CompletableFuture<Directory> getDirectory(String name, Callback<Directory> callback) {
		CompletableFuture<Directory> future = new CompletableFuture<Directory>();

		if (callback == null)
			callback = new NullCallback<Directory>();

		if (!name.startsWith("/"))
			name = "/" + name;

		String url = String.format("%s/root:%s", OneDriveURL.API, pathName + name)
						   .replace(" ", "%20");

		Unirest.get(url)
			.header(OneDriveHttpHeader.Authorization,
					OneDriveHttpHeader.bearerValue(authHelper))
			.asJsonAsync(new GetDirCallback(future, callback));

		return future;
	}

	@Override
	public CompletableFuture<File> createFile(String name) {
		return createFile(name, new NullCallback<File>());
	}

	@Override
	public CompletableFuture<File> createFile(String name, Callback<File> callback) {
		CompletableFuture<File> future = new CompletableFuture<File>();

		if (callback == null)
			callback = new NullCallback<File>();

		if (!name.startsWith("/"))
			name = "/" + name;

		String url = String.format("%s/root:%s:/content", OneDriveURL.API, pathName + name)
						   .replace(" ", "%20");

		Unirest.put(url)
			.header("Authorization",  "bearer " + authHelper.getToken().getAccessToken())
			.asJsonAsync(new CreateFileCallback(future, callback));

		return future;
	}

	@Override
	public CompletableFuture<File> getFile(String name) {
		return getFile(name, new NullCallback<File>());
	}

	@Override
	public CompletableFuture<File> getFile(String name, Callback<File> callback) {
		CompletableFuture<File> future = new CompletableFuture<File>();

		if (callback == null)
			callback = new NullCallback<File>();

		if (!name.startsWith("/"))
			name = "/" + name;

		String url = String.format("%s/root:%s", OneDriveURL.API, pathName + name)
						   .replace(" ", "%20");

		Unirest.get(url)
			.header(OneDriveHttpHeader.Authorization,
					OneDriveHttpHeader.bearerValue(authHelper))
			.asJsonAsync(new GetFileCallback(future, callback));

		return future;
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

		String url = String.format("%s/items/%s/children", OneDriveURL.API, dirId)
						   .replace(" ", "%20");

		Unirest.get(url)
			.header("Authorization",  "bearer " + authHelper.getToken().getAccessToken())
			.asJsonAsync(new GetChildrenCallback(future, callback));

		return future;
	}

	private class GetDirInfoCallback implements UnirestAsyncCallback<JsonNode> {
		private final CompletableFuture<DirectoryInfo> future;
		private final Callback<DirectoryInfo> callback;

		GetDirInfoCallback(CompletableFuture<DirectoryInfo> future,
						   Callback<DirectoryInfo> callback) {
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
			dirInfo = new DirectoryInfo(jsonObject.getString("id"));
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

	private class MoveToCallback implements UnirestAsyncCallback<JsonNode> {
		private final CompletableFuture<Directory> future;
		private final Callback<Directory> callback;

		MoveToCallback(CompletableFuture<Directory> future, Callback<Directory> callback) {
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
			DirectoryInfo dirInfo = new DirectoryInfo(jsonObject.getString("id"));
			OneDriveDirectory dir = new OneDriveDirectory(dirInfo, authHelper);

			this.callback.onSuccess(dir);
			future.complete(dir);
		}

		@Override
		public void failed(UnirestException exception) {
			HiveException ex = new HiveException(exception.getMessage());
			this.callback.onError(ex);
			future.completeExceptionally(ex);
		}
	}

	private class CopyToCallback implements UnirestAsyncCallback<JsonNode> {
		private final CompletableFuture<Directory> future;
		private final Callback<Directory> callback;

		CopyToCallback(CompletableFuture<Directory> future, Callback<Directory> callback) {
			this.future = future;
			this.callback = callback;
		}
		@Override
		public void cancelled() {}
		@Override
		public void completed(HttpResponse<JsonNode> response) {
			if (response.getStatus() != 202) {
				HiveException ex = new HiveException("Server Error: " + response.getStatusText());
				this.callback.onError(ex);
				future.completeExceptionally(ex);
				return;
			}

			JSONObject jsonObject = response.getBody().getObject();
			DirectoryInfo dirInfo = new DirectoryInfo(jsonObject.getString("id"));
			OneDriveDirectory dir = new OneDriveDirectory(dirInfo, authHelper);

			this.callback.onSuccess(dir);
			future.complete(dir);
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
			if (response.getStatus() != 204) {
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

	private class CreateDirCallback implements UnirestAsyncCallback<JsonNode> {
		private final CompletableFuture<Directory> future;
		private final Callback<Directory> callback;

		CreateDirCallback(CompletableFuture<Directory> future, Callback<Directory> callback) {
			this.future = future;
			this.callback = callback;
		}
		@Override
		public void cancelled() {}
		@Override
		public void completed(HttpResponse<JsonNode> response) {
			if (response.getStatus() != 201) {
				HiveException ex = new HiveException("Server Error: " + response.getStatusText());
				this.callback.onError(ex);
				future.completeExceptionally(ex);
				return;
			}

			JSONObject jsonObject = response.getBody().getObject();
			boolean isFolder = jsonObject.has("folder");

			if (!isFolder) {
				HiveException ex = new HiveException("This is not a folder");
				this.callback.onError(ex);
				future.completeExceptionally(ex);
				return;
			}

			DirectoryInfo dirInfo = new DirectoryInfo(jsonObject.getString("id"));

			OneDriveDirectory directory = new OneDriveDirectory(dirInfo, authHelper);
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

	private class GetDirCallback implements UnirestAsyncCallback<JsonNode> {
		private final CompletableFuture<Directory> future;
		private final Callback<Directory> callback;

		GetDirCallback(CompletableFuture<Directory> future, Callback<Directory> callback) {
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
			boolean isDir = jsonObject.has("folder");
			if (!isDir) {
				HiveException ex = new HiveException("This is not a folder");
				this.callback.onError(ex);
				future.completeExceptionally(ex);
				return;
			}

			DirectoryInfo dirInfo = new DirectoryInfo(jsonObject.getString("id"));
			OneDriveDirectory directory = new OneDriveDirectory(dirInfo, authHelper);
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
		private final CompletableFuture<File> future;
		private final Callback<File> callback;

		CreateFileCallback(CompletableFuture<File> future, Callback<File> callback) {
			this.future = future;
			this.callback = callback;
		}
		@Override
		public void cancelled() {}
		@Override
		public void completed(HttpResponse<JsonNode> response) {
			if (response.getStatus() != 201 && response.getStatus() != 200) {
				HiveException ex = new HiveException("Server Error: " + response.getStatusText());
				this.callback.onError(ex);
				future.completeExceptionally(ex);
				return;
			}

			JSONObject jsonObject = response.getBody().getObject();
			boolean isFile = !jsonObject.has("folder");

			if (!isFile) {
				HiveException ex = new HiveException("This is not a file");
				this.callback.onError(ex);
				future.completeExceptionally(ex);
				return;
			}

			FileInfo fileInfo = new FileInfo(jsonObject.getString("id"));
			OneDriveFile file = new OneDriveFile(fileInfo, authHelper);
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
		private final CompletableFuture<File> future;
		private final Callback<File> callback;

		GetFileCallback(CompletableFuture<File> future, Callback<File> callback) {
			this.future = future;
			this.callback = callback;
		}
		@Override
		public void cancelled() {}
		@Override
		public void completed(HttpResponse<JsonNode> response) {
			if (response.getStatus() != 200 && response.getStatus() != 201) {
				HiveException ex = new HiveException("Server Error: " + response.getStatusText());
				this.callback.onError(ex);
				future.completeExceptionally(ex);
				return;
			}

			JSONObject jsonObject = response.getBody().getObject();
			boolean isFile = jsonObject.has("folder");

			if (!isFile) {
				HiveException ex = new HiveException("This is not a file");
				this.callback.onError(ex);
				future.completeExceptionally(ex);
				return;
			}

			FileInfo fileInfo = new FileInfo(jsonObject.getString("id"));
			OneDriveFile file = new OneDriveFile(fileInfo, authHelper);
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

	private class GetChildrenCallback implements UnirestAsyncCallback<JsonNode> {
		private final CompletableFuture<Children> future;
		private final Callback<Children> callback;

		GetChildrenCallback(CompletableFuture<Children> future, Callback<Children> callback) {
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

			JSONObject baseJson = response.getBody().getObject();
			JSONArray values = baseJson.getJSONArray("value");
			int len = values.length();
			ArrayList<Object> childList = null;
			if (len > 0) {
				childList = new ArrayList<Object>();
				for (int i = 0; i < len; i++) {
					JSONObject itemJson = values.getJSONObject(i);
					String id = itemJson.getString("id");

					if (itemJson.has("folder")) {
						DirectoryInfo dirInfo = new DirectoryInfo(id);
						OneDriveDirectory directory = new OneDriveDirectory(dirInfo, authHelper);
						childList.add(directory);
					}
					else {
						FileInfo fileInfo = new FileInfo(id);
						OneDriveFile file = new OneDriveFile(fileInfo, authHelper);
						childList.add(file);
					}

				}
			}

			Children children = new Children(childList);
			this.callback.onSuccess(children);
			future.complete(children);
		}

		@Override
		public void failed(UnirestException exception) {
			HiveException ex = new HiveException(exception.getMessage());
			this.callback.onError(ex);
			future.completeExceptionally(ex);
		}
	}
}
