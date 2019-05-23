package org.elastos.hive.vendors.onedrive;

import java.util.concurrent.CompletableFuture;

import org.elastos.hive.AuthHelper;
import org.elastos.hive.Callback;
import org.elastos.hive.Directory;
import org.elastos.hive.Drive;
import org.elastos.hive.DriveInfo;
import org.elastos.hive.DriveType;
import org.elastos.hive.File;
import org.elastos.hive.FileInfo;
import org.elastos.hive.HiveException;
import org.elastos.hive.NullCallback;
import org.elastos.hive.UnirestAsyncCallback;
import org.json.JSONObject;

import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;

public final class OneDriveDrive implements Drive {
	private final String driveId;
	private final AuthHelper authHelper;
	private DriveInfo driveInfo;

	OneDriveDrive(DriveInfo driveInfo, AuthHelper authHelper) {
		this.driveId = driveInfo.getId();
		this.authHelper = authHelper;
	}

	@Override
	public DriveType getType() {
		return DriveType.oneDrive;
	}

	@Override
	public String getId() {
		return driveId;
	}

	@Override
	public DriveInfo getLastInfo() {
		return driveInfo;
	}

	@Override
	public CompletableFuture<DriveInfo> getInfo() {
		return getInfo(null);
	}

	@Override
	public CompletableFuture<DriveInfo> getInfo(Callback<DriveInfo> callback) {
		CompletableFuture<DriveInfo> future = new CompletableFuture<DriveInfo>();

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
		return getDirectory("/", callback);
	}

	@Override
	public CompletableFuture<Directory> createDirectory(String pathName) {
		return createDirectory(pathName, new NullCallback<Directory>());
	}

	@Override
	public CompletableFuture<Directory> createDirectory(String pathName, Callback<Directory> callback) {
		// TODO
		return null;
	}

	@Override
	public CompletableFuture<Directory> getDirectory(String pathName) {
		return getDirectory(pathName, new NullCallback<Directory>());
	}

	@Override
	public CompletableFuture<Directory> getDirectory(String pathName, Callback<Directory> callback) {
		// TODO
		return null;
	}

	@Override
	public CompletableFuture<File> createFile(String pathName) {
		return createFile(pathName, new NullCallback<File>());
	}

	@Override
	public CompletableFuture<File> createFile(String pathName, Callback<File> callback) {
		CompletableFuture<File> future = new CompletableFuture<File>();

		if (!pathName.startsWith("/")) {
			HiveException e = new HiveException("Not absolute PathName:  " + pathName);
			callback.onError(e);
			future.completeExceptionally(e);
			return future;
		}

		String url = String.format("%s/root:%s:/content", OneDriveURL.API, pathName)
				.replace(" ", "%20");

		Unirest.put(url)
			.header("Authorization",  "bearer " + authHelper.getToken().getAccessToken())
			.asJsonAsync(new CreateFileCallback(future, callback));

		return future;
	}

	@Override
	public CompletableFuture<File> getFile(String pathName) {
		return getFile(pathName, new NullCallback<File>());
	}

	@Override
	public CompletableFuture<File> getFile(String pathName, Callback<File> callback) {
		CompletableFuture<File> future = new CompletableFuture<File>();

		if (!pathName.startsWith("/")) {
			HiveException ex = new HiveException("Inavalid pathname: " + pathName);
			callback.onError(ex);
			future.completeExceptionally(ex);
			return future;
		}

		String url = String.format("%s/root:%s:/content", OneDriveURL.API, pathName)
				.replace(" ", "%20");

		Unirest.get(url)
			.header(OneDriveHttpHeader.Authorization,
					OneDriveHttpHeader.bearerValue(authHelper))
			.asJsonAsync(new GetFileCallback(future, callback));

		return future;
	}

	private class GetDriveInfoCallback implements UnirestAsyncCallback<JsonNode> {
		private final CompletableFuture<DriveInfo> future;
		private final Callback<DriveInfo> callback;

		private GetDriveInfoCallback(CompletableFuture<DriveInfo> future, Callback<DriveInfo> callback) {
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
			driveInfo = new DriveInfo(jsonObject.getString("id"));
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
		private final CompletableFuture<File> future;
		private final Callback<File> callback;

		private CreateFileCallback(CompletableFuture<File> future, Callback<File> callback) {
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

			// TODO;

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

		private GetFileCallback(CompletableFuture<File> future, Callback<File> callback) {
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

			// TODO;

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
}
