package org.elastos.hive.vendors.onedrive;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.CompletableFuture;

import org.elastos.hive.AuthHelper;
import org.elastos.hive.Callback;
import org.elastos.hive.Children;
import org.elastos.hive.Directory;
import org.elastos.hive.File;
import org.elastos.hive.HiveException;
import org.elastos.hive.ItemInfo;
import org.elastos.hive.NullCallback;
import org.elastos.hive.UnirestAsyncCallback;
import org.elastos.hive.Void;
import org.json.JSONArray;
import org.json.JSONObject;

import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;

class OneDriveDirectory extends Directory {
	private final AuthHelper authHelper;
	private String pathName;
	private Directory.Info dirInfo;

	OneDriveDirectory(String pathName, Directory.Info dirInfo, AuthHelper authHelper) {
		this.authHelper = authHelper;
		this.pathName = pathName;
		this.dirInfo = dirInfo;
	}

	@Override
	public String getId() {
		return dirInfo.get(Directory.Info.itemId);
	}

	@Override
	public String getPath() {
		return pathName;
	}

	@Override
	public String getParentPath() {
		if (pathName.equals("/"))
			return pathName;

		return pathName.substring(0, pathName.lastIndexOf("/"));
	}

	@Override
	public Directory.Info getLastInfo() {
		return dirInfo;
	}

	@Override
	public CompletableFuture<Directory.Info> getInfo() {
		return getInfo(new NullCallback<Directory.Info>());
	}

	@Override
	public CompletableFuture<Directory.Info> getInfo(Callback<Directory.Info> callback) {
		return authHelper.checkExpired()
				.thenCompose(placeHolder -> getInfo(placeHolder, callback));
	}

	private CompletableFuture<Directory.Info> getInfo(Void placeHolder,  Callback<Directory.Info> callback) {
		CompletableFuture<Directory.Info> future = new CompletableFuture<Directory.Info>();

		if (callback == null)
			callback = new NullCallback<Directory.Info>();

		String url = String.format("%s/root:/%s", OneDriveURL.API, pathName)
						   .replace(" ", "%20");
		Unirest.get(url)
			.header(OneDriveHttpHeader.Authorization,
					OneDriveHttpHeader.bearerValue(authHelper))
			.asJsonAsync(new GetDirInfoCallback(future, callback));

		return future;
	}

	@Override
	public CompletableFuture<Void> moveTo(String pathName) {
		return moveTo(pathName, new NullCallback<Void>());
	}

	@Override
	public CompletableFuture<Void> moveTo(String pathName, Callback<Void> callback) {
		return authHelper.checkExpired()
				.thenCompose(placeHolder -> moveTo(placeHolder, pathName, callback));
	}

	private CompletableFuture<Void> moveTo(Void placeHolder, String pathName, Callback<Void> callback) {
		CompletableFuture<Void> future = new CompletableFuture<Void>();

		if (callback == null)
			callback = new NullCallback<Void>();

		// the pathname must be a absolute path name
		if (!pathName.startsWith("/")) {
			HiveException e = new HiveException("Need a absolute path to moveTo");
			callback.onError(e);
			future.completeExceptionally(e);
			return future;
		}

		if (this.pathName.equals("/")) {
			HiveException e = new HiveException("Can't move the root.");
			callback.onError(e);
			future.completeExceptionally(e);
			return future;
		}

		if (this.pathName.equals(pathName)) {
			HiveException e = new HiveException("Can't move to same path name");
			callback.onError(e);
			future.completeExceptionally(e);
			return future;
		}

		try {
			int LastPos = this.pathName.lastIndexOf("/");
			String name = this.pathName.substring(LastPos + 1);

			String url  = String.format("%s/root:%s", OneDriveURL.API, this.pathName)
								.replace(" ", "%20");
			String body = String.format("{\"parentReference\":{\"path\":\"/drive/root:%s\"},\"name\":\"%s\"}", pathName, name)
								.replace(" ", "%20");
			String newPathName = String.format("%s/%s", pathName, name);

			Unirest.patch(url)
				.header(OneDriveHttpHeader.Authorization,
						OneDriveHttpHeader.bearerValue(authHelper))
				.header(OneDriveHttpHeader.ContentType, OneDriveHttpHeader.Json)
				.body(body)
				.asJsonAsync(new MoveToCallback(newPathName, future, callback));
		} catch (Exception ex) {
			HiveException e = new HiveException(ex.getMessage());
			callback.onError(e);
			future.completeExceptionally(e);
		}

		return future;
	}

	@Override
	public CompletableFuture<Void> copyTo(String pathName) {
		return copyTo(pathName, new NullCallback<Void>());
	}

	@Override
	public CompletableFuture<Void> copyTo(String pathName, Callback<Void> callback) {
		return authHelper.checkExpired()
				.thenCompose(placeHolder -> copyTo(placeHolder, pathName, callback));
	}

	private CompletableFuture<Void> copyTo(Void placeHolder, String pathName, Callback<Void> callback) {
		CompletableFuture<Void> future = new CompletableFuture<Void>();

		if (callback == null)
			callback = new NullCallback<Void>();

		if (!pathName.startsWith("/")) {
			HiveException e = new HiveException("Need a absolute path to copyTo");
			callback.onError(e);
			future.completeExceptionally(e);
			return future;
		}

		if (this.pathName.equals("/")) {
			HiveException e = new HiveException("Can't copy the root");
			callback.onError(e);
			future.completeExceptionally(e);
			return future;
		}

		if (this.pathName.equals(pathName)) {
			HiveException e = new HiveException("Can't copy to same path name");
			callback.onError(e);
			future.completeExceptionally(e);
			return future;
		}

		try {
			int LastPos = this.pathName.lastIndexOf("/");
			String name = this.pathName.substring(LastPos + 1);

			String url  = String.format("%s/root:%s:/copy", OneDriveURL.API, this.pathName)
							    .replace(" ", "%20");
			String body = String.format("{\"parentReference\":{\"path\":\"/drive/root:%s\"},\"name\":\"%s\"}", pathName, name)
								.replace(" ", "%20");

			Unirest.post(url)
				.header(OneDriveHttpHeader.Authorization,
						OneDriveHttpHeader.bearerValue(authHelper))
				.header(OneDriveHttpHeader.ContentType, OneDriveHttpHeader.Json)
				.body(body)
				.asJsonAsync(new CopyToCallback(future, callback));
		} catch (Exception ex) {
			HiveException e = new HiveException(ex.getMessage());
			callback.onError(e);
			future.completeExceptionally(e);
		}

		return future;
	}

	@Override
	public CompletableFuture<Void> deleteItem() {
		return deleteItem(new NullCallback<Void>());
	}

	@Override
	public CompletableFuture<Void> deleteItem(Callback<Void> callback) {
		return authHelper.checkExpired()
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

		String url = String.format("%s/root:%s", OneDriveURL.API, this.pathName)
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
		return authHelper.checkExpired()
				.thenCompose(placeHolder -> createDirectory(placeHolder, name, callback));
	}

	private CompletableFuture<Directory> createDirectory(Void placeHolder, String name, Callback<Directory> callback) {
		CompletableFuture<Directory> future = new CompletableFuture<Directory>();

		if (callback == null)
			callback = new NullCallback<Directory>();

		if (name.contains("/")) {
			HiveException e = new HiveException("Only need the last part of directory name");
			callback.onError(e);
			future.completeExceptionally(e);
			return future;
		}

		String url = String.format("%s/root:%s:/children", OneDriveURL.API, this.pathName)
						    .replace(" ", "%20");

		//conflictBehavior' value : fail, replace, or rename
		String body = String.format("{\"name\": \"%s\", \"folder\": { }, \"@microsoft.graph.conflictBehavior\": \"fail\"}", name);
		String path = String.format("%s/%s", this.pathName, name);
		Unirest.post(url)
			.header("Authorization",  "bearer " + authHelper.getToken().getAccessToken())
			.header(OneDriveHttpHeader.ContentType, OneDriveHttpHeader.Json)
			.body(body)
			.asJsonAsync(new CreateDirCallback(path, future, callback));

		return future;
	}

	@Override
	public CompletableFuture<Directory> getDirectory(String name) {
		return getDirectory(name, new NullCallback<Directory>());
	}

	@Override
	public CompletableFuture<Directory> getDirectory(String name, Callback<Directory> callback) {
		return authHelper.checkExpired()
				.thenCompose(status -> getDirectory(status, name, callback));
	}

	private CompletableFuture<Directory> getDirectory(Void status, String name, Callback<Directory> callback) {
		CompletableFuture<Directory> future = new CompletableFuture<Directory>();

		if (callback == null)
			callback = new NullCallback<Directory>();

		if (name.contains("/")) {
			HiveException e = new HiveException("Only need the the last part of directory name");
			callback.onError(e);
			future.completeExceptionally(e);
			return future;
		}

		String url  = String.format("%s/root:%s/%s", OneDriveURL.API, pathName, name)
						    .replace(" ", "%20");
		String path = String.format("%s/%s", this.pathName, name);
		Unirest.get(url)
			.header(OneDriveHttpHeader.Authorization,
					OneDriveHttpHeader.bearerValue(authHelper))
			.asJsonAsync(new GetDirCallback(path, future, callback));

		return future;
	}

	@Override
	public CompletableFuture<File> createFile(String name) {
		return createFile(name, new NullCallback<File>());
	}

	@Override
	public CompletableFuture<File> createFile(String name, Callback<File> callback) {
		return authHelper.checkExpired()
				.thenCompose(status -> createFile(status, name, callback));
	}

	private CompletableFuture<File> createFile(Void status, String name, Callback<File> callback) {
		CompletableFuture<File> future = new CompletableFuture<File>();

		if (callback == null)
			callback = new NullCallback<File>();

		if (name.contains("/")) {
			HiveException e = new HiveException("Only need the name of a file.");
			callback.onError(e);
			future.completeExceptionally(e);
			return future;
		}

		String path = String.format("%s/%s", this.pathName, name);
		String url  = String.format("%s/root:%s:/content", OneDriveURL.API, path)
						    .replace(" ", "%20");

		Unirest.put(url)
			.header(OneDriveHttpHeader.Authorization,
					OneDriveHttpHeader.bearerValue(authHelper))
			.asJsonAsync(new CreateFileCallback(path, future, callback));

		return future;
	}

	@Override
	public CompletableFuture<File> getFile(String name) {
		return getFile(name, new NullCallback<File>());
	}

	@Override
	public CompletableFuture<File> getFile(String name, Callback<File> callback) {
		return authHelper.checkExpired()
				.thenCompose(status -> getFile(status, name, callback));
	}

	private CompletableFuture<File> getFile(Void status, String name, Callback<File> callback) {
		CompletableFuture<File> future = new CompletableFuture<File>();

		if (callback == null)
			callback = new NullCallback<File>();

		if (name.contains("/")) {
			HiveException e = new HiveException("Only need the name of a file.");
			callback.onError(e);
			future.completeExceptionally(e);
			return future;
		}

		String path = String.format("%s/%s", this.pathName, name);
		String url  = String.format("%s/root:%s", OneDriveURL.API, path)
				   		    .replace(" ", "%20");

		Unirest.get(url)
			.header(OneDriveHttpHeader.Authorization,
					OneDriveHttpHeader.bearerValue(authHelper))
			.asJsonAsync(new GetFileCallback(path, future, callback));

		return future;
	}

	@Override
	public CompletableFuture<Children> getChildren() {
		return getChildren(new NullCallback<Children>());
	}

	@Override
	public CompletableFuture<Children> getChildren(Callback<Children> callback) {
		return authHelper.checkExpired()
				.thenCompose(status -> getChildren(status,callback));
	}

	private CompletableFuture<Children> getChildren(Void status, Callback<Children> callback) {
		CompletableFuture<Children> future = new CompletableFuture<Children>();

		if (callback == null)
			callback = new NullCallback<Children>();

		String url = String.format("%s/root:%s:/children", OneDriveURL.API, this.pathName)
						   .replace(" ", "%20");

		Unirest.get(url)
			.header(OneDriveHttpHeader.Authorization,
					OneDriveHttpHeader.bearerValue(authHelper))
			.asJsonAsync(new GetChildrenCallback(future, callback));

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
			if (response.getStatus() == 401) {
				authHelper.getToken().expired();
				HiveException e = new HiveException("Server Error: " + response.getStatusText());
				this.callback.onError(e);
				future.completeExceptionally(e);
				return;
			}

			if (response.getStatus() != 200) {
				HiveException e = new HiveException("Server Error: " + response.getStatusText());
				this.callback.onError(e);
				future.completeExceptionally(e);
				return;
			}

			JSONObject jsonObject = response.getBody().getObject();
			HashMap<String, String> attrs = new HashMap<>();
			attrs.put(Directory.Info.itemId, jsonObject.getString("id"));
			// TODO;

			Directory.Info info = new Directory.Info(attrs);
			this.callback.onSuccess(info);
			future.complete(dirInfo);
		}

		@Override
		public void failed(UnirestException exception) {
			HiveException e = new HiveException(exception.getMessage());
			this.callback.onError(e);
			future.completeExceptionally(e);
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
			if (response.getStatus() == 401) {
				authHelper.getToken().expired();
				HiveException e = new HiveException("Server Error: " + response.getStatusText());
				this.callback.onError(e);
				future.completeExceptionally(e);
				return;
			}

			if (response.getStatus() != 200) {
				HiveException e = new HiveException("Server Error: " + response.getStatusText());
				this.callback.onError(e);
				future.completeExceptionally(e);
				return;
			}

			OneDriveDirectory.this.pathName = pathName;
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
			if (response.getStatus() == 401) {
				authHelper.getToken().expired();
				HiveException e = new HiveException("Server Error: " + response.getStatusText());
				this.callback.onError(e);
				future.completeExceptionally(e);
				return;
			}

			if (response.getStatus() != 202) {
				HiveException e = new HiveException("Server Error: " + response.getStatusText());
				this.callback.onError(e);
				future.completeExceptionally(e);
				return;
			}

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
			if (response.getStatus() == 401) {
				authHelper.getToken().expired();
				HiveException e = new HiveException("Server Error: " + response.getStatusText());
				this.callback.onError(e);
				future.completeExceptionally(e);
				return;
			}

			if (response.getStatus() != 204) {
				HiveException e = new HiveException("Server Error: " + response.getStatusText());
				this.callback.onError(e);
				future.completeExceptionally(e);
				return;
			}

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

	private class CreateDirCallback implements UnirestAsyncCallback<JsonNode> {
		private final String pathName;
		private final CompletableFuture<Directory> future;
		private final Callback<Directory> callback;

		CreateDirCallback(String pathName, CompletableFuture<Directory> future, Callback<Directory> callback) {
			this.pathName = pathName;
			this.future = future;
			this.callback = callback;
		}

		@Override
		public void cancelled() {}

		@Override
		public void completed(HttpResponse<JsonNode> response) {
			if (response.getStatus() == 401) {
				authHelper.getToken().expired();
				HiveException e = new HiveException("Server Error: " + response.getStatusText());
				this.callback.onError(e);
				future.completeExceptionally(e);
				return;
			}

			if (response.getStatus() != 201) {
				HiveException e = new HiveException("Server Error: " + response.getStatusText());
				this.callback.onError(e);
				future.completeExceptionally(e);
				return;
			}

			JSONObject jsonObject = response.getBody().getObject();
			if (!jsonObject.has("folder")) {
				HiveException e = new HiveException("Impossible: Create a file instead of directory");
				this.callback.onError(e);
				future.completeExceptionally(e);
				return;
			}

			HashMap<String, String> attrs = new HashMap<>();
			attrs.put(Directory.Info.itemId, jsonObject.getString("id"));
			// TODO;

			Directory.Info info = new Directory.Info(attrs);
			OneDriveDirectory directory = new OneDriveDirectory(pathName, info, authHelper);
			this.callback.onSuccess(directory);
			future.complete(directory);
		}

		@Override
		public void failed(UnirestException exception) {
			HiveException e = new HiveException(exception.getMessage());
			this.callback.onError(e);
			future.completeExceptionally(e);
		}
	}

	private class GetDirCallback implements UnirestAsyncCallback<JsonNode> {
		private final String pathName;
		private final CompletableFuture<Directory> future;
		private final Callback<Directory> callback;

		GetDirCallback(String pathName, CompletableFuture<Directory> future, Callback<Directory> callback) {
			this.pathName = pathName;
			this.future = future;
			this.callback = callback;
		}
		@Override
		public void cancelled() {}

		@Override
		public void completed(HttpResponse<JsonNode> response) {
			if (response.getStatus() == 401) {
				authHelper.getToken().expired();
				HiveException e = new HiveException("Server Error: " + response.getStatusText());
				this.callback.onError(e);
				future.completeExceptionally(e);
				return;
			}

			if (response.getStatus() != 200) {
				HiveException e = new HiveException("Server Error: " + response.getStatusText());
				this.callback.onError(e);
				future.completeExceptionally(e);
				return;
			}

			JSONObject jsonObject = response.getBody().getObject();
			if (!jsonObject.has("folder")) {
				HiveException e = new HiveException("This is not a folder");
				this.callback.onError(e);
				future.completeExceptionally(e);
				return;
			}

			HashMap<String, String> attrs = new HashMap<>();
			attrs.put(Directory.Info.itemId, jsonObject.getString("id"));
			// TODO;

			Directory.Info info = new Directory.Info(attrs);
			OneDriveDirectory directory = new OneDriveDirectory(pathName, info, authHelper);
			this.callback.onSuccess(directory);
			future.complete(directory);
		}

		@Override
		public void failed(UnirestException exception) {
			HiveException e = new HiveException(exception.getMessage());
			this.callback.onError(e);
			future.completeExceptionally(e);
		}
	}

	private class CreateFileCallback implements UnirestAsyncCallback<JsonNode> {
		private final String pathName;
		private final CompletableFuture<File> future;
		private final Callback<File> callback;

		CreateFileCallback(String pathName, CompletableFuture<File> future, Callback<File> callback) {
			this.pathName = pathName;
			this.future = future;
			this.callback = callback;
		}

		@Override
		public void cancelled() {}

		@Override
		public void completed(HttpResponse<JsonNode> response) {
			if (response.getStatus() == 401) {
				authHelper.getToken().expired();
				HiveException e = new HiveException("Server Error: " + response.getStatusText());
				this.callback.onError(e);
				future.completeExceptionally(e);
				return;
			}

			if (response.getStatus() != 201 && response.getStatus() != 200) {
				HiveException e = new HiveException("Server Error: " + response.getStatusText());
				this.callback.onError(e);
				future.completeExceptionally(e);
				return;
			}

			JSONObject jsonObject = response.getBody().getObject();
			if (!jsonObject.has("file")) {
				HiveException e = new HiveException("Impossible: Create a directory instead of file");
				this.callback.onError(e);
				future.completeExceptionally(e);
				return;
			}

			HashMap<String, String> attrs = new HashMap<>();
			attrs.put(File.Info.itemId, jsonObject.getString("id"));
			// TODO;

			File.Info info = new File.Info(attrs);
			OneDriveFile file = new OneDriveFile(pathName, info, authHelper);
			this.callback.onSuccess(file);
			future.complete(file);
		}

		@Override
		public void failed(UnirestException exception) {
			HiveException e = new HiveException(exception.getMessage());
			this.callback.onError(e);
			future.completeExceptionally(e);
		}
	}

	private class GetFileCallback implements UnirestAsyncCallback<JsonNode> {
		private final String pathName;
		private final CompletableFuture<File> future;
		private final Callback<File> callback;

		GetFileCallback(String pathName, CompletableFuture<File> future, Callback<File> callback) {
			this.pathName = pathName;
			this.future = future;
			this.callback = callback;
		}

		@Override
		public void cancelled() {}

		@Override
		public void completed(HttpResponse<JsonNode> response) {
			if (response.getStatus() == 401) {
				authHelper.getToken().expired();
				HiveException e = new HiveException("Server Error: " + response.getStatusText());
				this.callback.onError(e);
				future.completeExceptionally(e);
				return;
			}

			if (response.getStatus() != 200 && response.getStatus() != 201) {
				HiveException e = new HiveException("Server Error: " + response.getStatusText());
				this.callback.onError(e);
				future.completeExceptionally(e);
				return;
			}

			JSONObject jsonObject = response.getBody().getObject();
			if (jsonObject.has("folder")) {
				HiveException e = new HiveException("This is not a file");
				this.callback.onError(e);
				future.completeExceptionally(e);
				return;
			}

			HashMap<String, String> attrs = new HashMap<>();
			attrs.put(File.Info.itemId, jsonObject.getString("id"));
			// TODO;

			File.Info fileInfo = new File.Info(attrs);
			OneDriveFile file = new OneDriveFile(pathName, fileInfo, authHelper);
			this.callback.onSuccess(file);
			future.complete(file);
		}

		@Override
		public void failed(UnirestException exception) {
			HiveException e = new HiveException(exception.getMessage());
			this.callback.onError(e);
			future.completeExceptionally(e);
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
			if (response.getStatus() == 401) {
				authHelper.getToken().expired();
				HiveException e = new HiveException("Server Error: " + response.getStatusText());
				this.callback.onError(e);
				future.completeExceptionally(e);
				return;
			}

			if (response.getStatus() != 200) {
				HiveException e = new HiveException("Server Error: " + response.getStatusText());
				this.callback.onError(e);
				future.completeExceptionally(e);
				return;
			}

			JSONObject baseJson = response.getBody().getObject();
			JSONArray values = baseJson.getJSONArray("value");
			int len = values.length();
			ArrayList<ItemInfo> array = new ArrayList<ItemInfo>(len);
			if (len > 0) {
				for (int i = 0; i < len; i++) {
					JSONObject itemJson = values.getJSONObject(i);

					HashMap<String, String> attrs = new HashMap<>();
					attrs.put(ItemInfo.itemId, itemJson.getString("id"));
					attrs.put(ItemInfo.type, itemJson.has("Folder") ? "Folder": "File");

					array.add(new ItemInfo(attrs));
				}
			}

			Children children = new Children(array);
			this.callback.onSuccess(children);
			future.complete(children);
		}

		@Override
		public void failed(UnirestException exception) {
			HiveException e = new HiveException(exception.getMessage());
			this.callback.onError(e);
			future.completeExceptionally(e);
		}
	}
}
