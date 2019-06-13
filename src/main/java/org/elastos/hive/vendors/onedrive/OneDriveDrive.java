package org.elastos.hive.vendors.onedrive;

import java.util.concurrent.CompletableFuture;

import org.elastos.hive.AuthHelper;
import org.elastos.hive.Callback;
import org.elastos.hive.Directory;
import org.elastos.hive.Drive;
import org.elastos.hive.DriveType;
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

final class OneDriveDrive extends Drive {
	private final AuthHelper authHelper;
	private volatile Drive.Info driveInfo;

	OneDriveDrive(Drive.Info driveInfo, AuthHelper authHelper) {
		this.driveInfo = driveInfo;
		this.authHelper = authHelper;
	}

	@Override
	public DriveType getType() {
		return DriveType.oneDrive;
	}

	@Override
	public String getId() {
		return driveInfo.getId();
	}

	@Override
	public Drive.Info getLastInfo() {
		return driveInfo;
	}

	@Override
	public CompletableFuture<Drive.Info> getInfo() {
		return getInfo(new NullCallback<Drive.Info>());
	}

	@Override
	public CompletableFuture<Drive.Info> getInfo(Callback<Drive.Info> callback) {
		return authHelper.checkExpired()
				.thenCompose(status -> getInfo(status, callback));
	}

	private CompletableFuture<Drive.Info> getInfo(Status status, Callback<Drive.Info> callback) {
		CompletableFuture<Drive.Info> future = new CompletableFuture<Drive.Info>();

		if (callback == null)
			callback = new NullCallback<Drive.Info>();

		Unirest.get(OneDriveURL.API)
			.header(OneDriveHttpHeader.Authorization,
					OneDriveHttpHeader.bearerValue(authHelper))
			.asJsonAsync(new GetDriveInfoCallback(future, callback));

		return future;
	}

	@Override
	public CompletableFuture<Directory> getRootDir() {
		return getRootDir(new NullCallback<Directory>());
	}

	@Override
	public CompletableFuture<Directory> getRootDir(Callback<Directory> callback) {
		return authHelper.checkExpired()
				.thenCompose(status -> getRootDir(status, callback));
	}

	private CompletableFuture<Directory> getRootDir(Status status, Callback<Directory> callback) {
		if (callback == null)
			callback = new NullCallback<Directory>();

		return getDirectory("/", callback);
	}

	@Override
	public CompletableFuture<Directory> createDirectory(String pathName) {
		return createDirectory(pathName, new NullCallback<Directory>());
	}

	@Override
	public CompletableFuture<Directory> createDirectory(String pathName, Callback<Directory> callback) {
		return authHelper.checkExpired()
				.thenCompose(status -> createDirectory(status, pathName, callback));
	}

	private CompletableFuture<Directory> createDirectory(Status status, String pathName, Callback<Directory> callback) {
		CompletableFuture<Directory> future = new CompletableFuture<Directory>();

		if (callback == null)
			callback = new NullCallback<Directory>();

		if (!pathName.startsWith("/")) {
			HiveException e = new HiveException("Path name must be a abosulte path");
			callback.onError(e);
			future.completeExceptionally(e);
			return future;
		}

		if (pathName.equals("/")) {
			HiveException e = new HiveException("Can't create root directory");
			callback.onError(e);
			future.completeExceptionally(e);
			return future;
		}

		String url;
		String name;

		int pos = pathName.lastIndexOf("/");
		if (pos == 0) {
			name = pathName.replace("/", "");
			url = String.format("%s/root/children", OneDriveURL.API)
						.replace(" ", "%20");
		} else {
			String parentPath = pathName.substring(0, pos);
			name = pathName.substring(pos + 1);
			url = String.format("%s/root:/%s:/children", OneDriveURL.API, parentPath)
						.replace(" ", "%20");
		}

		//conflictBehavior' value : fail, replace, or rename
		String body = String.format("{\"name\": \"%s\", \"folder\": { }, \"@microsoft.graph.conflictBehavior\": \"fail\"}", name);
		Unirest.post(url)
			.header(OneDriveHttpHeader.Authorization,
					OneDriveHttpHeader.bearerValue(authHelper))
			.header("Content-Type", "application/json")
			.body(body)
			.asJsonAsync(new CreateDirectoryCallback(pathName, future, callback));

		return future;
	}

	@Override
	public CompletableFuture<Directory> getDirectory(String pathName) {
		return getDirectory(pathName, new NullCallback<Directory>());
	}

	@Override
	public CompletableFuture<Directory> getDirectory(String pathName, Callback<Directory> callback) {
		return authHelper.checkExpired()
				.thenCompose(status -> getDirectory(status, pathName, callback));
	}

	private CompletableFuture<Directory> getDirectory(Status status, String pathName, Callback<Directory> callback) {
		CompletableFuture<Directory> future = new CompletableFuture<Directory>();

		if (callback == null)
			callback = new NullCallback<Directory>();

		if (!pathName.startsWith("/")) {
			HiveException e = new HiveException("Need a absolute path to get a directory.");
			callback.onError(e);
			future.completeExceptionally(e);
			return future;
		}

		String url;

		if (pathName.equals("/"))
			url = String.format("%s/root", OneDriveURL.API)
					    .replace(" ", "%20");
		else
			url = String.format("%s/root:%s", OneDriveURL.API, pathName)
						.replace(" ", "%20");

		Unirest.get(url)
			.header(OneDriveHttpHeader.Authorization,
					OneDriveHttpHeader.bearerValue(authHelper))
			.asJsonAsync(new GetDirectoryCallback(pathName, future, callback));

		return future;
	}

	@Override
	public CompletableFuture<File> createFile(String pathName) {
		return createFile(pathName, new NullCallback<File>());
	}

	@Override
	public CompletableFuture<File> createFile(String pathName, Callback<File> callback) {
		return authHelper.checkExpired()
				.thenCompose(status -> createFile(status, pathName, callback));
	}

	private CompletableFuture<File> createFile(Status status, String pathName, Callback<File> callback) {
		CompletableFuture<File> future = new CompletableFuture<File>();

		if (callback == null)
			callback = new NullCallback<File>();

		if (!pathName.startsWith("/")) {
			HiveException ex = new HiveException("Need a absolute path to create a file.");
			callback.onError(ex);
			future.completeExceptionally(ex);
			return future;
		}

		if (pathName.equals("/")) {
			HiveException ex = new HiveException("Can't create file with root path");
			callback.onError(ex);
			future.completeExceptionally(ex);
			return future;
		}

		String url = String.format("%s/root:%s:/content", OneDriveURL.API, pathName)
						   .replace(" ", "%20");

		Unirest.put(url)
			.header(OneDriveHttpHeader.Authorization,
					OneDriveHttpHeader.bearerValue(authHelper))
			.asJsonAsync(new CreateFileCallback(pathName, future, callback));

		return future;
	}

	@Override
	public CompletableFuture<File> getFile(String pathName) {
		return getFile(pathName, new NullCallback<File>());
	}

	@Override
	public CompletableFuture<File> getFile(String pathName, Callback<File> callback) {
		return authHelper.checkExpired()
				.thenCompose(status -> getFile(status, pathName, callback));
	}

	private CompletableFuture<File> getFile(Status status, String pathName, Callback<File> callback) {
		CompletableFuture<File> future = new CompletableFuture<File>();

		if (callback == null)
			callback = new NullCallback<File>();

		if (!pathName.startsWith("/")) {
			HiveException e = new HiveException("Need a absolute path to get a file.");
			callback.onError(e);
			future.completeExceptionally(e);
			return future;
		}

		if (pathName.equals("/")) {
			HiveException ex = new HiveException("Can't get file with root path");
			callback.onError(ex);
			future.completeExceptionally(ex);
			return future;
		}

		String url = String.format("%s/root:%s", OneDriveURL.API, pathName)
						   .replace(" ", "%20");

		Unirest.get(url)
			.header(OneDriveHttpHeader.Authorization,
					OneDriveHttpHeader.bearerValue(authHelper))
			.asJsonAsync(new GetFileCallback(pathName, future, callback));

		return future;
	}

	private class GetDriveInfoCallback implements UnirestAsyncCallback<JsonNode> {
		private final CompletableFuture<Drive.Info> future;
		private final Callback<Drive.Info> callback;

		private GetDriveInfoCallback(CompletableFuture<Drive.Info> future, Callback<Drive.Info> callback) {
			this.future = future;
			this.callback = callback;
		}
		@Override
		public void cancelled() {}

		@Override
		public void completed(HttpResponse<JsonNode> response) {
			if (response.getStatus() != 200) {
				HiveException e = new HiveException("Server Error: " + response.getStatusText());
				this.callback.onError(e);
				future.completeExceptionally(e);
				return;
			}

			JSONObject jsonObject = response.getBody().getObject();
			driveInfo = new Drive.Info(jsonObject.getString("id"));
			this.callback.onSuccess(driveInfo);
			future.complete(driveInfo);
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
			if (response.getStatus() != 201 && response.getStatus() != 200) {
				HiveException e = new HiveException("Server Error: " + response.getStatusText());
				this.callback.onError(e);
				future.completeExceptionally(e);
				return;
			}

			JSONObject jsonObject = response.getBody().getObject();
			boolean isFile = !jsonObject.has("folder");

			if (!isFile) {
				HiveException e = new HiveException("This is not a file");
				this.callback.onError(e);
				future.completeExceptionally(e);
				return;
			}

			File.Info fileInfo = new File.Info(jsonObject.getString("id"));
			OneDriveFile file = new OneDriveFile(pathName, fileInfo, authHelper);
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
			if (response.getStatus() != 201) {
				HiveException e = new HiveException("Server Error: " + response.getStatusText());
				this.callback.onError(e);
				future.completeExceptionally(e);
				return;
			}

			JSONObject jsonObject = response.getBody().getObject();
			boolean isFolder = jsonObject.has("folder");

			if (!isFolder) {
				HiveException e = new HiveException("This is not a folder");
				this.callback.onError(e);
				future.completeExceptionally(e);
				return;
			}

			Directory.Info dirInfo = new Directory.Info(jsonObject.getString("id"));
			OneDriveDirectory directory = new OneDriveDirectory(pathName, dirInfo, authHelper);
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
			if (response.getStatus() != 200 && response.getStatus() != 201) {
				HiveException e = new HiveException("Server Error: " + response.getStatusText());
				this.callback.onError(e);
				future.completeExceptionally(e);
				return;
			}

			JSONObject jsonObject = response.getBody().getObject();
			boolean isFile = !jsonObject.has("folder");
			if (!isFile) {
				HiveException e = new HiveException("This is not a file");
				this.callback.onError(e);
				future.completeExceptionally(e);
				return;
			}

			File.Info fileInfo = new File.Info(jsonObject.getString("id"));
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
				HiveException e = new HiveException("Server Error: " + response.getStatusText());
				this.callback.onError(e);
				future.completeExceptionally(e);
				return;
			}

			JSONObject jsonObject = response.getBody().getObject();
			boolean isDir = jsonObject.has("folder");
			if (!isDir) {
				HiveException e = new HiveException("This is not a folder");
				this.callback.onError(e);
				future.completeExceptionally(e);
				return;
			}

			Directory.Info dirInfo = new Directory.Info(jsonObject.getString("id"));
			OneDriveDirectory directory = new OneDriveDirectory(pathName, dirInfo, authHelper);
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
}
