package org.elastos.hive.vendors.onedrive;

import java.util.concurrent.CompletableFuture;

import org.elastos.hive.AuthHelper;
import org.elastos.hive.Callback;
import org.elastos.hive.File;
import org.elastos.hive.FileInfo;
import org.elastos.hive.HiveException;
import org.elastos.hive.NullCallback;
import org.elastos.hive.Result;
import org.elastos.hive.Status;
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
		// TODO
		return null;
	}

	@Override
	public FileInfo getLastInfo() {
		return fileInfo;
	}

	@Override
	public CompletableFuture<Result<FileInfo>> getInfo() {
		return getInfo(new NullCallback<FileInfo>());
	}

	@Override
	public CompletableFuture<Result<FileInfo>> getInfo(Callback<FileInfo> callback) {
		CompletableFuture<Result<FileInfo>> future = new CompletableFuture<Result<FileInfo>>();

		Unirest.get(OneDriveURL.API)
			.header(OneDriveHttpHeader.Authorization, OneDriveHttpHeader.bearerValue(authHelper))
			.asJsonAsync(new GetFileInfoCallback(future, callback));

		return future;
	}

	@Override
	public CompletableFuture<Result<File>> moveTo(String pathName) {
		return moveTo(pathName, new NullCallback<File>());
	}

	@Override
	public CompletableFuture<Result<File>> moveTo(String pathName, Callback<File> callback) {
		// TODO
		return null;
	}

	@Override
	public CompletableFuture<Result<File>> copyTo(String pathName) {
		return copyTo(pathName, new NullCallback<File>());
	}

	@Override
	public CompletableFuture<Result<File>> copyTo(String pathName, Callback<File> callback) {
		// TODO
		return null;
	}

	@Override
	public CompletableFuture<Result<Status>> deleteItem() {
		return deleteItem(new NullCallback<Status>());
	}

	@Override
	public CompletableFuture<Result<Status>> deleteItem(Callback<Status> callback) {
		CompletableFuture<Result<Status>> future = new CompletableFuture<Result<Status>>();
		String url = String.format("%s/items/%s",  OneDriveURL.API, this.fileId)
			.replace(" ", "%20");

		Unirest.delete(url)
			.header(OneDriveHttpHeader.Authorization, OneDriveHttpHeader.bearerValue(authHelper))
			.asJsonAsync(new DeleteItemCallback(future, callback));

		return future;
	}

	@Override
	public void close() {
		// TODO
	}

	private class GetFileInfoCallback implements com.mashape.unirest.http.async.Callback<JsonNode> {
		private final CompletableFuture<Result<FileInfo>> future;
		private final Callback<FileInfo> callback;

		private GetFileInfoCallback(CompletableFuture<Result<FileInfo>> future, Callback<FileInfo> callback) {
			this.future = future;
			this.callback = callback;
		}
		@Override
		public void cancelled() {
		}

		@Override
		public void completed(HttpResponse<JsonNode> response) {
			if (response.getStatus() != 200) {
				HiveException e = new HiveException("Server Error: " + response.getStatusText());
				this.callback.onError(e);
				future.complete(new Result<FileInfo>(e));
				return;
			}

			JSONObject jsonObject = response.getBody().getObject();
			fileInfo = new FileInfo(jsonObject.getString("id"));
			this.callback.onSuccess(fileInfo);
			future.complete(new Result<FileInfo>(fileInfo));
		}

		@Override
		public void failed(UnirestException exception) {
			HiveException e = new HiveException(exception.getMessage());
			this.callback.onError(e);
			future.complete(new Result<FileInfo>(e));
		}
	}

	private class DeleteItemCallback implements com.mashape.unirest.http.async.Callback<JsonNode> {
		private final CompletableFuture<Result<Status>> future;
		private final Callback<Status> callback;

		private DeleteItemCallback(CompletableFuture<Result<Status>> future, Callback<Status> callback) {
			this.future = future;
			this.callback = callback;
		}
		@Override
		public void cancelled() {
		}

		@Override
		public void completed(HttpResponse<JsonNode> response) {
			if (response.getStatus() != 204) {
				HiveException e = new HiveException("Server Error: " + response.getStatusText());
				this.callback.onError(e);
				future.complete(new Result<Status>(e));
				return;
			}

			Status status = new Status(1);
			this.callback.onSuccess(status);
			future.complete(new Result<Status>(status));
		}

		@Override
		public void failed(UnirestException exception) {
			HiveException e = new HiveException(exception.getMessage());
			this.callback.onError(e);
			future.complete(new Result<Status>(e));
		}
	}
}
