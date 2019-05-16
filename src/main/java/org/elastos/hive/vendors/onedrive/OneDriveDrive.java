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
import org.elastos.hive.Result;
import org.json.JSONObject;

import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;

final class OneDriveDrive implements Drive {
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
	public CompletableFuture<Result<DriveInfo>> getInfo() {
		return getInfo(null);
	}

	@Override
	public CompletableFuture<Result<DriveInfo>> getInfo(Callback<DriveInfo> callback) {
		CompletableFuture<Result<DriveInfo>> future = new CompletableFuture<Result<DriveInfo>>();

		Unirest.get(OneDriveURL.API)
			.header(OneDriveHttpHeader.Authorization, OneDriveHttpHeader.bearerValue(authHelper))
			.asJsonAsync(new GetDriveInfoCallback(future, callback));

		return future;
	}

	@Override
	public CompletableFuture<Result<Directory>> getRootDir() {
		return getRootDir(new NullCallback<Directory>());
	}

	@Override
	public CompletableFuture<Result<Directory>> getRootDir(Callback<Directory> callback) {
		return getDirectory("/", callback);
	}

	@Override
	public CompletableFuture<Result<Directory>> createDirectory(String pathName) {
		return createDirectory(pathName, new NullCallback<Directory>());
	}

	@Override
	public CompletableFuture<Result<Directory>> createDirectory(String pathName, Callback<Directory> callback) {
		// TODO
		return null;
	}

	@Override
	public CompletableFuture<Result<Directory>> getDirectory(String pathName) {
		return getDirectory(pathName, new NullCallback<Directory>());
	}

	@Override
	public CompletableFuture<Result<Directory>> getDirectory(String pathName, Callback<Directory> callback) {
		// TODO
		return null;
	}

	@Override
	public CompletableFuture<Result<File>> createFile(String pathName) {
		return createFile(pathName, new NullCallback<File>());
	}

	@Override
	public CompletableFuture<Result<File>> createFile(String pathName, Callback<File> callback) {
		CompletableFuture<Result<File>> future = new CompletableFuture<Result<File>>();

		if (!pathName.startsWith("/")) {
			HiveException e = new HiveException("Not absolute PathName:  " + pathName);
			callback.onError(e);
			future.complete(new Result<File>(e));
			return future;
		}

		String url = String.format("%s/root:%s", OneDriveURL.API, pathName)
				.replace(" ", "%20");

		Unirest.get(url)
			.header("Authorization",  "bearer " + authHelper.getToken().getAccessToken())
			.asJsonAsync(new GetFileObjectCallback(future, callback));

		return future;
	}

	@Override
	public CompletableFuture<Result<File>> getFile(String pathName) {
		return getFile(pathName, new NullCallback<File>());
	}

	@Override
	public CompletableFuture<Result<File>> getFile(String pathName, Callback<File> callback) {
		CompletableFuture<Result<File>> future = new CompletableFuture<Result<File>>();

		if (!pathName.startsWith("/")) {
			HiveException e = new HiveException("Not absolute PathName:  " + pathName);
			callback.onError(e);
			future.complete(new Result<File>(e));
			return future;
		}

		String url = String.format("%s/root:%s:/content", OneDriveURL.API, pathName)
				.replace(" ", "%20");

		Unirest.get(url)
			.header(OneDriveHttpHeader.Authorization, OneDriveHttpHeader.bearerValue(authHelper))
			.asJsonAsync(new CreateFileObjectCallback(future, callback));

		return future;
	}

	private class GetDriveInfoCallback implements com.mashape.unirest.http.async.Callback<JsonNode> {
		private final CompletableFuture<Result<DriveInfo>> future;
		private final Callback<DriveInfo> callback;

		private GetDriveInfoCallback(CompletableFuture<Result<DriveInfo>> future, Callback<DriveInfo> callback) {
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
				future.complete(new Result<DriveInfo>(e));
				return;
			}

			JSONObject jsonObject = response.getBody().getObject();
			driveInfo = new DriveInfo(jsonObject.getString("id"));
			this.callback.onSuccess(driveInfo);
			future.complete(new Result<DriveInfo>(driveInfo));
		}

		@Override
		public void failed(UnirestException exception) {
			HiveException e = new HiveException(exception.getMessage());
			this.callback.onError(e);
			future.complete(new Result<DriveInfo>(e));
		}
	}

	private class CreateFileObjectCallback implements com.mashape.unirest.http.async.Callback<JsonNode> {
		private final CompletableFuture<Result<File>> future;
		private final Callback<File> callback;

		private CreateFileObjectCallback(CompletableFuture<Result<File>> future, Callback<File> callback) {
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
				future.complete(new Result<File>(e));
				return;
			}

			JSONObject jsonObject = response.getBody().getObject();
			boolean isFile = jsonObject.has("folder");

			if (!isFile) {
				HiveException e = new HiveException("This is not a file");
				this.callback.onError(e);
				future.complete(new Result<File>(e));
				return;
			}

			FileInfo fileInfo = new FileInfo(jsonObject.getString("id"));

			// TODO;

			OneDriveFile file = new OneDriveFile(fileInfo, authHelper);
			this.callback.onSuccess(file);
			future.complete(new Result<File>(file));
		}

		@Override
		public void failed(UnirestException exception) {
			HiveException e = new HiveException(exception.getMessage());
			this.callback.onError(e);
			future.complete(new Result<File>(e));
		}
	}

	private class GetFileObjectCallback implements com.mashape.unirest.http.async.Callback<JsonNode> {
		private final CompletableFuture<Result<File>> future;
		private final Callback<File> callback;

		private GetFileObjectCallback(CompletableFuture<Result<File>> future, Callback<File> callback) {
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
				future.complete(new Result<File>(e));
				return;
			}

			JSONObject jsonObject = response.getBody().getObject();
			boolean isFile = jsonObject.has("folder");

			if (!isFile) {
				HiveException e = new HiveException("This is not a file");
				this.callback.onError(e);
				future.complete(new Result<File>(e));
				return;
			}

			FileInfo fileInfo = new FileInfo(jsonObject.getString("id"));

			// TODO;

			OneDriveFile file = new OneDriveFile(fileInfo, authHelper);
			this.callback.onSuccess(file);
			future.complete(new Result<File>(file));

		}

		@Override
		public void failed(UnirestException exception) {
			HiveException e = new HiveException(exception.getMessage());
			this.callback.onError(e);
			future.complete(new Result<File>(e));
		}
	}
}
