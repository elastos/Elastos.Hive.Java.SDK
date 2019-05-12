package org.elastos.hive.vendors.onedrive;

import java.util.concurrent.CompletableFuture;

import org.elastos.hive.AuthHelper;
import org.elastos.hive.Callback;
import org.elastos.hive.DriveInfo;
import org.elastos.hive.DriveType;
import org.elastos.hive.FileInfo;
import org.elastos.hive.Directory;
import org.elastos.hive.Drive;
import org.elastos.hive.HiveException;
import org.elastos.hive.File;
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
			.header("Authorization",  "bearer " + authHelper.getAuthToken().getAccessToken())
			.asJsonAsync(new GetDriveInfoCallback(future, callback));

		return future;
	}

	@Override
	public CompletableFuture<Result<Directory>> getRootDir() {
		return getRootDir(null);
	}

	@Override
	public CompletableFuture<Result<Directory>> getRootDir(Callback<Directory> callback) {
		return getDirectory("/", callback);
	}

	@Override
	public CompletableFuture<Result<Directory>> createDirectory(String pathName) {
		return createDirectory(pathName, null);
	}

	@Override
	public CompletableFuture<Result<Directory>> createDirectory(String pathName, Callback<Directory> callback) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public CompletableFuture<Result<Directory>> getDirectory(String pathName) {
		return getDirectory(pathName);
	}

	@Override
	public CompletableFuture<Result<Directory>> getDirectory(String pathName, Callback<Directory> callback) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public CompletableFuture<Result<File>> createFile(String pathName) {
		return createFile(pathName, null);
	}

	@Override
	public CompletableFuture<Result<File>> createFile(String pathName, Callback<File> callback) {
		CompletableFuture<Result<File>> future = new CompletableFuture<Result<File>>();

		if (!pathName.startsWith("/")) {
			HiveException e = new HiveException("Not absolute PathName:  " + pathName);
			Result<File> value = new Result<File>(e);

			if (callback != null)
				callback.onFailed(e);

			future.complete(value);
			return future;
		}

		String url = String.format("%s/root:%s", OneDriveURL.API, pathName)
				.replace(" ", "%20");

		Unirest.get(url)
			.header("Authorization",  "bearer " + authHelper.getAuthToken().getAccessToken())
			.asJsonAsync(new GetFileObjectCallback(future, callback));

		return future;
	}

	@Override
	public CompletableFuture<Result<File>> getFile(String pathName) {
		return getFile(pathName, null);
	}

	@Override
	public CompletableFuture<Result<File>> getFile(String pathName, Callback<File> callback) {
		CompletableFuture<Result<File>> future = new CompletableFuture<Result<File>>();

		if (!pathName.startsWith("/")) {
			HiveException e = new HiveException("Not absolute PathName:  " + pathName);
			Result<File> value = new Result<File>(e);

			if (callback != null)
				callback.onFailed(e);

			future.complete(value);
			return future;
		}

		String url = String.format("%s/root:%s:/content", OneDriveURL.API, pathName)
				.replace(" ", "%20");

		Unirest.get(url)
			.header("Authorization",  "bearer " + authHelper.getAuthToken().getAccessToken())
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
		public void cancelled() {
		}

		@Override
		public void completed(HttpResponse<JsonNode> response) {
			if (callback == null)
				return;

			if (response.getStatus() != 200) {
				HiveException e = new HiveException("Server Error: " + response.getStatusText());
				Result<DriveInfo> value = new Result<DriveInfo>(e);
				this.callback.onFailed(e);
				future.complete(value);
				return;
			}

			JSONObject jsonObject = response.getBody().getObject();
			driveInfo = new DriveInfo(jsonObject.getString("id"));
			Result<DriveInfo> value = new Result<DriveInfo>(driveInfo);
			this.callback.onSuccess(driveInfo);
			future.complete(value);
		}

		@Override
		public void failed(UnirestException exception) {
			if (callback == null)
				return;

			HiveException e = new HiveException(exception.getMessage());
			Result<DriveInfo> value = new Result<DriveInfo>(e);
			this.callback.onFailed(e);
			future.complete(value);
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
		public void cancelled() {
		}

		@Override
		public void completed(HttpResponse<JsonNode> response) {
			if (callback == null)
				return;

			if (response.getStatus() != 200) {
				HiveException e = new HiveException("Server Error: " + response.getStatusText());
				Result<File> value = new Result<File>(e);
				this.callback.onFailed(e);
				future.complete(value);
				return;
			}

			JSONObject jsonObject = response.getBody().getObject();
			boolean isFile = jsonObject.has("folder");

			if (!isFile) {
				HiveException e = new HiveException("This is not a file");
				Result<File> value = new Result<File>(e);
				this.callback.onFailed(e);
				future.complete(value);
				return;
			}

			FileInfo fileInfo = new FileInfo(jsonObject.getString("id"));

			// TODO;

			OneDriveFile file = new OneDriveFile(fileInfo, authHelper);
			Result<File> value = new Result<File>(file);
			this.callback.onSuccess(file);
			future.complete(value);
		}

		@Override
		public void failed(UnirestException exception) {
			if (callback == null)
				return;

			HiveException e = new HiveException(exception.getMessage());
			Result<File> value = new Result<File>(e);
			this.callback.onFailed(e);
			future.complete(value);
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
		public void cancelled() {
		}

		@Override
		public void completed(HttpResponse<JsonNode> response) {
			if (callback == null)
				return;

			if (response.getStatus() != 200 && response.getStatus() != 201) {
				HiveException e = new HiveException("Server Error: " + response.getStatusText());
				Result<File> value = new Result<File>(e);
				this.callback.onFailed(e);
				future.complete(value);
				return;
			}

			JSONObject jsonObject = response.getBody().getObject();
			boolean isFile = jsonObject.has("folder");

			if (!isFile) {
				HiveException e = new HiveException("This is not a file");
				Result<File> value = new Result<File>(e);
				this.callback.onFailed(e);
				future.complete(value);
				return;
			}

			FileInfo fileInfo = new FileInfo(jsonObject.getString("id"));

			// TODO;

			OneDriveFile file = new OneDriveFile(fileInfo, authHelper);
			Result<File> value = new Result<File>(file);
			this.callback.onSuccess(file);
			future.complete(value);
		}

		@Override
		public void failed(UnirestException exception) {
			if (callback == null)
				return;

			HiveException e = new HiveException(exception.getMessage());
			Result<File> value = new Result<File>(e);
			this.callback.onFailed(e);
			future.complete(value);
		}
	}
}
