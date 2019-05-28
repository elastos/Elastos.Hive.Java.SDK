package org.elastos.hive.vendors.onedrive;

import java.util.concurrent.CompletableFuture;

import org.elastos.hive.AuthHelper;
import org.elastos.hive.Callback;
import org.elastos.hive.File;
import org.elastos.hive.FileInfo;
import org.elastos.hive.HiveException;
import org.elastos.hive.NullCallback;
import org.elastos.hive.Status;
import org.elastos.hive.UnirestAsyncCallback;
import org.json.JSONObject;

import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;

final class OneDriveFile implements File {
	private final AuthHelper authHelper;
	private final String fileId;
	private FileInfo fileInfo;
	private String pathName;

	OneDriveFile(FileInfo fileInfo, AuthHelper authHelper) {
		this.fileId = fileInfo.getId();
		this.fileInfo = fileInfo;
		this.authHelper = authHelper;
	}

	@Override
	public String getId() {
		return fileId;
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
	public FileInfo getLastInfo() {
		return fileInfo;
	}

	@Override
	public CompletableFuture<FileInfo> getInfo() {
		return getInfo(new NullCallback<FileInfo>());
	}

	@Override
	public CompletableFuture<FileInfo> getInfo(Callback<FileInfo> callback) {
		CompletableFuture<FileInfo> future = new CompletableFuture<FileInfo>();

		if (callback == null)
			callback = new NullCallback<FileInfo>();

		String url = String.format("%s/root:/%s", OneDriveURL.API, pathName)
						   .replace(" ", "%20");
		Unirest.get(url)
			.header(OneDriveHttpHeader.Authorization, OneDriveHttpHeader.bearerValue(authHelper))
			.asJsonAsync(new GetFileInfoCallback(future, callback));

		return future;
	}

	@Override
	public CompletableFuture<File> moveTo(String pathName) {
		return moveTo(pathName, new NullCallback<File>());
	}

	@Override
	public CompletableFuture<File> moveTo(String pathName, Callback<File> callback) {
		CompletableFuture<File> future = new CompletableFuture<File>();

		if (callback == null)
			callback = new NullCallback<File>();

		try {
			OneDriveDirectory parentDirectory = (OneDriveDirectory)OneDriveClient.getInstance().getDefaultDrive().get().getDirectory(pathName).get();
			int LastPos = pathName.lastIndexOf("/");
			String name = pathName.substring(LastPos + 1);

			String url = String.format("%s/items/%s", OneDriveURL.API, this.fileId);
			String body = "{\"parentReference\": \"id\": \"" + parentDirectory.getId() + "\"},\"name\": \"" + name + "\"}";

			Unirest.patch(url)
				.header(OneDriveHttpHeader.Authorization, OneDriveHttpHeader.bearerValue(authHelper))
				.header("Content-Type", "application/json")
				.body(body)
				.asJsonAsync(new MoveToCallback(future, callback));
		} catch (Exception e) {
			HiveException hiveException = new HiveException(String.format("Move the file %s to %s failed.", this.pathName, pathName));
			callback.onError(hiveException);
		}

		return future;
	}

	@Override
	public CompletableFuture<File> copyTo(String pathName) {
		return copyTo(pathName, new NullCallback<File>());
	}

	@Override
	public CompletableFuture<File> copyTo(String pathName, Callback<File> callback) {
		CompletableFuture<File> future = new CompletableFuture<File>();

		if (callback == null)
			callback = new NullCallback<File>();

		try {
			OneDriveDrive drive = (OneDriveDrive) OneDriveClient.getInstance().getDefaultDrive().get();
			OneDriveDirectory directory = (OneDriveDirectory)drive.getDirectory(pathName).get();
			int LastPos = pathName.lastIndexOf("/");
			String name = pathName.substring(LastPos + 1);

			String url  = String.format("%s/items/%s/copy", OneDriveURL.API, fileId)
							    .replace(" ", "%20");
			String body = "{\"parentReference\": {\"driveId\": \"" + drive.getId() +
					"\",\"id\": \"" + directory.getId() + "\"},\"name\": \"" + name + "\"}";

			Unirest.post(url)
				.header(OneDriveHttpHeader.Authorization,
						OneDriveHttpHeader.bearerValue(authHelper))
				.header("Content-Type", "application/json")
				.body(body)
				.asJsonAsync(new CopyToCallback(future, callback));
		} catch (Exception e) {
			HiveException ex = new HiveException("UNIrest error:" + e.getMessage());
			callback.onError(ex);
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

		String url = String.format("%s/items/%s",  OneDriveURL.API, this.fileId)
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

	private class GetFileInfoCallback implements UnirestAsyncCallback<JsonNode> {
		private final CompletableFuture<FileInfo> future;
		private final Callback<FileInfo> callback;

		private GetFileInfoCallback(CompletableFuture<FileInfo> future, Callback<FileInfo> callback) {
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
			fileInfo = new FileInfo(jsonObject.getString("id"));
			this.callback.onSuccess(fileInfo);
			future.complete(fileInfo);
		}

		@Override
		public void failed(UnirestException exception) {
			HiveException ex = new HiveException(exception.getMessage());
			this.callback.onError(ex);
			future.completeExceptionally(ex);
		}
	}

	private class MoveToCallback implements UnirestAsyncCallback<JsonNode> {
		private final CompletableFuture<File> future;
		private final Callback<File> callback;

		MoveToCallback(CompletableFuture<File> future, Callback<File> callback) {
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

	private class CopyToCallback implements UnirestAsyncCallback<JsonNode> {
		private final CompletableFuture<File> future;
		private final Callback<File> callback;

		CopyToCallback(CompletableFuture<File> future, Callback<File> callback) {
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

	private class DeleteItemCallback implements UnirestAsyncCallback<JsonNode> {
		private final CompletableFuture<Status> future;
		private final Callback<Status> callback;

		private DeleteItemCallback(CompletableFuture<Status> future, Callback<Status> callback) {
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
}
