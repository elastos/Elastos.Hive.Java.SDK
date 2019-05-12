package org.elastos.hive.vendors.onedrive;

import java.util.concurrent.CompletableFuture;

import org.elastos.hive.AuthHelper;
import org.elastos.hive.DriveInfo;
import org.elastos.hive.DriveType;
import org.elastos.hive.FileInfo;
import org.elastos.hive.HiveCallback;
import org.elastos.hive.HiveDirectory;
import org.elastos.hive.HiveDrive;
import org.elastos.hive.HiveException;
import org.elastos.hive.HiveFile;
import org.elastos.hive.HiveResult;
import org.json.JSONObject;

import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.async.Callback;
import com.mashape.unirest.http.exceptions.UnirestException;

final class OneDriveDrive implements HiveDrive {
	private final String driveId;
	private final AuthHelper authHelper;
	private DriveInfo driveInfo;

	OneDriveDrive(DriveInfo driveInfo, AuthHelper authHelper) {
		this.driveId = driveInfo.getDriveId();
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
	public CompletableFuture<HiveResult<DriveInfo>> getInfo() {
		return getInfo(null);
	}

	@Override
	public CompletableFuture<HiveResult<DriveInfo>> getInfo(HiveCallback<DriveInfo, HiveException> callback) {
		CompletableFuture<HiveResult<DriveInfo>> future = new CompletableFuture<HiveResult<DriveInfo>>();

		Unirest.get(OneDriveURL.API)
			.header("Authorization",  "bearer " + authHelper.getAuthToken().getAccessToken())
			.asJsonAsync(new GetDriveInfoCallback(future, callback));

		return future;
	}

	@Override
	public CompletableFuture<HiveResult<HiveDirectory>> getRootDir() {
		return getRootDir(null);
	}

	@Override
	public CompletableFuture<HiveResult<HiveDirectory>> getRootDir(HiveCallback<HiveDirectory, HiveException> callback) {
		return getDirectory("/", callback);
	}

	@Override
	public CompletableFuture<HiveResult<HiveDirectory>> createDirectory(String pathName) {
		return createDirectory(pathName, null);
	}

	@Override
	public CompletableFuture<HiveResult<HiveDirectory>> createDirectory(String pathName,
			HiveCallback<HiveDirectory, HiveException> callback) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public CompletableFuture<HiveResult<HiveDirectory>> getDirectory(String pathName) {
		return getDirectory(pathName);
	}

	@Override
	public CompletableFuture<HiveResult<HiveDirectory>> getDirectory(String pathName,
			HiveCallback<HiveDirectory, HiveException> callback) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public CompletableFuture<HiveResult<HiveFile>> createFile(String pathName) {
		return createFile(pathName, null);
	}

	@Override
	public CompletableFuture<HiveResult<HiveFile>> createFile(String pathName,
			HiveCallback<HiveFile, HiveException> callback) {
		CompletableFuture<HiveResult<HiveFile>> future = new CompletableFuture<HiveResult<HiveFile>>();

		if (!pathName.startsWith("/")) {
			HiveException e = new HiveException("Not absolute PathName:  " + pathName);
			HiveResult<HiveFile> value = new HiveResult<HiveFile>(e);

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
	public CompletableFuture<HiveResult<HiveFile>> getFile(String pathName) {
		return getFile(pathName, null);
	}

	@Override
	public CompletableFuture<HiveResult<HiveFile>> getFile(String pathName,
			HiveCallback<HiveFile, HiveException> callback) {
		CompletableFuture<HiveResult<HiveFile>> future = new CompletableFuture<HiveResult<HiveFile>>();

		if (!pathName.startsWith("/")) {
			HiveException e = new HiveException("Not absolute PathName:  " + pathName);
			HiveResult<HiveFile> value = new HiveResult<HiveFile>(e);

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

	private class GetDriveInfoCallback implements Callback<JsonNode> {
		private final CompletableFuture<HiveResult<DriveInfo>> future;
		private final HiveCallback<DriveInfo, HiveException> callback;

		private GetDriveInfoCallback(CompletableFuture<HiveResult<DriveInfo>> future, HiveCallback<DriveInfo, HiveException> callback) {
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
				HiveResult<DriveInfo> value = new HiveResult<DriveInfo>(e);
				this.callback.onFailed(e);
				future.complete(value);
				return;
			}

			JSONObject jsonObject = response.getBody().getObject();
			driveInfo = new DriveInfo(jsonObject.getString("id"));
			HiveResult<DriveInfo> value = new HiveResult<DriveInfo>(driveInfo);
			this.callback.onSuccess(driveInfo);
			future.complete(value);
		}

		@Override
		public void failed(UnirestException exception) {
			if (callback == null)
				return;

			HiveException e = new HiveException(exception.getMessage());
			HiveResult<DriveInfo> value = new HiveResult<DriveInfo>(e);
			this.callback.onFailed(e);
			future.complete(value);
		}
	}

	private class CreateFileObjectCallback implements Callback<JsonNode> {
		private final CompletableFuture<HiveResult<HiveFile>> future;
		private final HiveCallback<HiveFile, HiveException> callback;

		private CreateFileObjectCallback(CompletableFuture<HiveResult<HiveFile>> future, HiveCallback<HiveFile, HiveException> callback) {
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
				HiveResult<HiveFile> value = new HiveResult<HiveFile>(e);
				this.callback.onFailed(e);
				future.complete(value);
				return;
			}

			JSONObject jsonObject = response.getBody().getObject();
			boolean isFile = jsonObject.has("folder");

			if (!isFile) {
				HiveException e = new HiveException("This is not a file");
				HiveResult<HiveFile> value = new HiveResult<HiveFile>(e);
				this.callback.onFailed(e);
				future.complete(value);
				return;
			}

			FileInfo fileInfo = new FileInfo(jsonObject.getString("id"));

			// TODO;

			OneDriveFile file = new OneDriveFile(fileInfo, authHelper);
			HiveResult<HiveFile> value = new HiveResult<HiveFile>(file);
			this.callback.onSuccess(file);
			future.complete(value);
		}

		@Override
		public void failed(UnirestException exception) {
			if (callback == null)
				return;

			HiveException e = new HiveException(exception.getMessage());
			HiveResult<HiveFile> value = new HiveResult<HiveFile>(e);
			this.callback.onFailed(e);
			future.complete(value);
		}
	}

	private class GetFileObjectCallback implements Callback<JsonNode> {
		private final CompletableFuture<HiveResult<HiveFile>> future;
		private final HiveCallback<HiveFile, HiveException> callback;

		private GetFileObjectCallback(CompletableFuture<HiveResult<HiveFile>> future, HiveCallback<HiveFile, HiveException> callback) {
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
				HiveResult<HiveFile> value = new HiveResult<HiveFile>(e);
				this.callback.onFailed(e);
				future.complete(value);
				return;
			}

			JSONObject jsonObject = response.getBody().getObject();
			boolean isFile = jsonObject.has("folder");

			if (!isFile) {
				HiveException e = new HiveException("This is not a file");
				HiveResult<HiveFile> value = new HiveResult<HiveFile>(e);
				this.callback.onFailed(e);
				future.complete(value);
				return;
			}

			FileInfo fileInfo = new FileInfo(jsonObject.getString("id"));

			// TODO;

			OneDriveFile file = new OneDriveFile(fileInfo);
			HiveResult<HiveFile> value = new HiveResult<HiveFile>(file);
			this.callback.onSuccess(file);
			future.complete(value);
		}

		@Override
		public void failed(UnirestException exception) {
			if (callback == null)
				return;

			HiveException e = new HiveException(exception.getMessage());
			HiveResult<HiveFile> value = new HiveResult<HiveFile>(e);
			this.callback.onFailed(e);
			future.complete(value);
		}
	}
}
