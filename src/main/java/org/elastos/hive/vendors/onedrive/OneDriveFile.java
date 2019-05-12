package org.elastos.hive.vendors.onedrive;

import java.util.concurrent.CompletableFuture;

import org.elastos.hive.AuthHelper;
import org.elastos.hive.FileInfo;
import org.elastos.hive.HiveCallback;
import org.elastos.hive.HiveException;
import org.elastos.hive.HiveFile;
import org.elastos.hive.HiveResult;
import org.elastos.hive.Status;
import org.json.JSONObject;

import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.async.Callback;
import com.mashape.unirest.http.exceptions.UnirestException;

final class OneDriveFile implements HiveFile {
	private final AuthHelper authHelper;
	private final String fileId;
	private FileInfo fileInfo;

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
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getParentPath() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public CompletableFuture<HiveResult<FileInfo>> getInfo() {
		return getInfo(null);
	}

	@Override
	public CompletableFuture<HiveResult<FileInfo>> getInfo(HiveCallback<FileInfo, HiveException> callback) {
		CompletableFuture<HiveResult<FileInfo>> future = new CompletableFuture<HiveResult<FileInfo>>();

		Unirest.get(OneDriveURL.API)
			.header("Authorization",  "bearer " + authHelper.getAuthToken().getAccessToken())
			.asJsonAsync(new GetFileInfoCallback(future, callback));

		return future;
	}

	@Override
	public CompletableFuture<HiveResult<HiveFile>> moveTo(String pathName) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public CompletableFuture<HiveResult<HiveFile>> moveTo(String pathName, HiveCallback<HiveFile, HiveException> callback) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public CompletableFuture<HiveResult<HiveFile>> copyTo(String pathName) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public CompletableFuture<HiveResult<HiveFile>> copyTo(String pathName, HiveCallback<HiveFile, HiveException> callback) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public CompletableFuture<HiveResult<Status>> deleteItem() {
		return deleteItem(null);
	}

	@Override
	public CompletableFuture<HiveResult<Status>> deleteItem(HiveCallback<Status, HiveException> callback) {
		CompletableFuture<HiveResult<Status>> future = new CompletableFuture<HiveResult<Status>>();
		String url = String.format("%s/items/%s",  OneDriveURL.API, this.fileId)
				.replace(" ", "%20");

		Unirest.delete(url)
			.header("Authorization",  "bearer " + authHelper.getAuthToken().getAccessToken())
			.asJsonAsync(new DeleteItemCallback(future, callback));

		return future;
	}

	@Override
	public void close() {
		// TODO
	}

	private class GetFileInfoCallback implements Callback<JsonNode> {
		private final CompletableFuture<HiveResult<FileInfo>> future;
		private final HiveCallback<FileInfo, HiveException> callback;

		private GetFileInfoCallback(CompletableFuture<HiveResult<FileInfo>> future, HiveCallback<FileInfo, HiveException> callback) {
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
				HiveResult<FileInfo> value = new HiveResult<FileInfo>(e);
				this.callback.onFailed(e);
				future.complete(value);
				return;
			}

			JSONObject jsonObject = response.getBody().getObject();
			fileInfo = new FileInfo(jsonObject.getString("id"));
			HiveResult<FileInfo> value = new HiveResult<FileInfo>(fileInfo);
			this.callback.onSuccess(fileInfo);
			future.complete(value);
		}

		@Override
		public void failed(UnirestException exception) {
			if (callback == null)
				return;

			HiveException e = new HiveException(exception.getMessage());
			HiveResult<FileInfo> value = new HiveResult<FileInfo>(e);
			this.callback.onFailed(e);
			future.complete(value);
		}
	}

	private class DeleteItemCallback implements Callback<JsonNode> {
		private final CompletableFuture<HiveResult<Status>> future;
		private final HiveCallback<Status, HiveException> callback;

		private DeleteItemCallback(CompletableFuture<HiveResult<Status>> future, HiveCallback<Status, HiveException> callback) {
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

			if (response.getStatus() != 204) {
				HiveException e = new HiveException("Server Error: " + response.getStatusText());
				HiveResult<Status> value = new HiveResult<Status>(e);
				this.callback.onFailed(e);
				future.complete(value);
				return;
			}

			Status status = new Status(1);
			HiveResult<Status> value = new HiveResult<Status>(status);
			this.callback.onSuccess(status);
			future.complete(value);
		}

		@Override
		public void failed(UnirestException exception) {
			if (callback == null)
				return;

			HiveException e = new HiveException(exception.getMessage());
			HiveResult<Status> value = new HiveResult<Status>(e);
			this.callback.onFailed(e);
			future.complete(value);
		}
	}
}
