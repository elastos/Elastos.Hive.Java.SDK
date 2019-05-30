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
	private volatile FileInfo fileInfo;

	OneDriveFile(FileInfo fileInfo, AuthHelper authHelper) {
		this.fileInfo = fileInfo;
		this.authHelper = authHelper;
	}

	@Override
	public String getId() {
		return fileInfo.getId();
	}

	@Override
	public String getPath() {
		return fileInfo.getPathName();
	}

	@Override
	public String getParentPath() {
		String pathName = fileInfo.getPathName();
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

		String url = String.format("%s/root:/%s", OneDriveURL.API, fileInfo.getPathName())
						   .replace(" ", "%20");
		Unirest.get(url)
			.header(OneDriveHttpHeader.Authorization, OneDriveHttpHeader.bearerValue(authHelper))
			.asJsonAsync(new GetFileInfoCallback(fileInfo.getPathName(), future, callback));

		return future;
	}

	@Override
	public CompletableFuture<Status> moveTo(String pathName) {
		return moveTo(pathName, new NullCallback<Status>());
	}

	@Override
	public CompletableFuture<Status> moveTo(String pathName, Callback<Status> callback) {
		CompletableFuture<Status> future = new CompletableFuture<Status>();

		if (callback == null)
			callback = new NullCallback<Status>();

		if (!pathName.startsWith("/")) {
			HiveException e = new HiveException("Neet a absolute path.");
			callback.onError(e);
			future.completeExceptionally(e);
			return future;
		}

		try {
			int LastPos = pathName.lastIndexOf("/");
			String name = pathName.substring(LastPos + 1);

			String url = String.format("%s/items/%s", OneDriveURL.API, getId());
			String body = String.format("{\"parentReference\": \"path\": \"%s\"name\":\"%s\"}", pathName, name)
								.replace(" ", "%20");

			String newPathName = String.format("%s/%s", pathName, name);
			Unirest.patch(url)
				.header(OneDriveHttpHeader.Authorization, OneDriveHttpHeader.bearerValue(authHelper))
				.header("Content-Type", "application/json")
				.body(body)
				.asJsonAsync(new MoveToCallback(newPathName, future, callback));
		} catch (Exception ex) {
			HiveException e = new HiveException("Unirest exception: " + ex.getMessage());
			callback.onError(e);
			future.completeExceptionally(e);
		}

		return future;
	}

	@Override
	public CompletableFuture<Status> copyTo(String pathName) {
		return copyTo(pathName, new NullCallback<Status>());
	}

	@Override
	public CompletableFuture<Status> copyTo(String pathName, Callback<Status> callback) {
		CompletableFuture<Status> future = new CompletableFuture<Status>();

		if (callback == null)
			callback = new NullCallback<Status>();

		if (!pathName.startsWith("/")) {
			HiveException e = new HiveException("Neet a absolute path.");
			callback.onError(e);
			future.completeExceptionally(e);
			return future;
		}

		try {
			OneDriveDrive drive = (OneDriveDrive) OneDriveClient.getInstance().getDefaultDrive().get();
			OneDriveDirectory directory = (OneDriveDirectory)drive.getDirectory(pathName).get();
			int LastPos = pathName.lastIndexOf("/");
			String name = pathName.substring(LastPos + 1);

			String url  = String.format("%s/items/%s/copy", OneDriveURL.API, getId())
							    .replace(" ", "%20");
			String body = String.format("{\"parentReference\": {\"driveId\": \"%s\",\"id\": \"%s\"},\"name\": \"%s\"}"
					, drive.getId(), directory.getId(), name)
								.replace(" ", "%20");

			Unirest.post(url)
				.header(OneDriveHttpHeader.Authorization,
						OneDriveHttpHeader.bearerValue(authHelper))
				.header("Content-Type", "application/json")
				.body(body)
				.asJsonAsync(new CopyToCallback(future, callback));
		} catch (Exception ex) {
			HiveException e = new HiveException("Unirest exception: " + ex.getMessage());
			callback.onError(e);
			future.completeExceptionally(e);
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

		String url = String.format("%s/items/%s",  OneDriveURL.API, getId())
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
		private final String pathName;
		private final CompletableFuture<FileInfo> future;
		private final Callback<FileInfo> callback;

		private GetFileInfoCallback(String pathName, CompletableFuture<FileInfo> future, Callback<FileInfo> callback) {
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
			fileInfo = new FileInfo(jsonObject.getString("id"));
			fileInfo.setPathName(pathName);
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

			fileInfo.setPathName(pathName);
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
			if (response.getStatus() != 202) {
				HiveException e = new HiveException("Server Error: " + response.getStatusText());
				this.callback.onError(e);
				future.completeExceptionally(e);
				return;
			}

			Status status = new Status(1);
			this.callback.onSuccess(status);
			future.complete(status);
		}

		@Override
		public void failed(UnirestException exception) {
			HiveException e = new HiveException(exception.getMessage());
			this.callback.onError(e);
			future.completeExceptionally(e);
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
				HiveException e = new HiveException("Server Error: " + response.getStatusText());
				this.callback.onError(e);
				future.completeExceptionally(e);
				return;
			}

			Status status = new Status(1);
			this.callback.onSuccess(status);
			future.complete(status);
		}

		@Override
		public void failed(UnirestException exception) {
			HiveException e = new HiveException(exception.getMessage());
			this.callback.onError(e);
			future.completeExceptionally(e);
		}
	}
}
