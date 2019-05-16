package org.elastos.hive.vendors.onedrive;

import java.util.concurrent.CompletableFuture;

import org.elastos.hive.AuthHelper;
import org.elastos.hive.Callback;
import org.elastos.hive.Children;
import org.elastos.hive.Directory;
import org.elastos.hive.DirectoryInfo;
import org.elastos.hive.File;
import org.elastos.hive.NullCallback;
import org.elastos.hive.Result;
import org.elastos.hive.Status;

import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;

class OneDriveDirectory implements Directory {
	private final AuthHelper authHelper;
	private final String dirId;
	private DirectoryInfo dirInfo;
	private String pathName;

	OneDriveDirectory(DirectoryInfo dirInfo, AuthHelper authHelper) {
		this.authHelper = authHelper;
		this.dirId = dirInfo.getId();
		this.dirInfo = dirInfo;
	}

	@Override
	public String getId() {
		return dirId;
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
	public DirectoryInfo getLastInfo() {
		return dirInfo;
	}

	@Override
	public CompletableFuture<Result<DirectoryInfo>> getInfo() {
		return getInfo(new NullCallback<DirectoryInfo>());
	}

	@Override
	public CompletableFuture<Result<DirectoryInfo>> getInfo(Callback<DirectoryInfo> callback) {
		CompletableFuture<Result<DirectoryInfo>> future = new CompletableFuture<Result<DirectoryInfo>>();

		Unirest.get(OneDriveURL.API)
			.header(OneDriveHttpHeader.Authorization, OneDriveHttpHeader.bearerValue(authHelper))
			.asJsonAsync(new GetDirectoryInfoCallback(future, callback));

		return future;
	}

	@Override
	public CompletableFuture<Result<Directory>> moveTo(String pathName) {
		return moveTo(pathName, new NullCallback<Directory>());
	}

	@Override
	public CompletableFuture<Result<Directory>> moveTo(String pathName, Callback<Directory> callback) {
		CompletableFuture<Result<Directory>> future = new CompletableFuture<Result<Directory>>();

		Unirest.get(OneDriveURL.API)
			.header(OneDriveHttpHeader.Authorization, OneDriveHttpHeader.bearerValue(authHelper))
			.asJsonAsync(new MoveToCallback(future, callback));

		return future;
	}

	@Override
	public CompletableFuture<Result<Directory>> copyTo(String pathName) {
		return copyTo(pathName, new NullCallback<Directory>());
	}

	@Override
	public CompletableFuture<Result<Directory>> copyTo(String pathName, Callback<Directory> callback) {
		CompletableFuture<Result<Directory>> future = new CompletableFuture<Result<Directory>>();

		Unirest.get(OneDriveURL.API)
			.header(OneDriveHttpHeader.Authorization, OneDriveHttpHeader.bearerValue(authHelper))
			.asJsonAsync(new MoveToCallback(future, callback));

		return future;
	}

	@Override
	public CompletableFuture<Result<Status>> deleteItem() {
		return deleteItem(new NullCallback<Status>());
	}

	@Override
	public CompletableFuture<Result<Status>> deleteItem(Callback<Status> callback) {
		CompletableFuture<Result<Status>> future = new CompletableFuture<Result<Status>>();

		Unirest.get(OneDriveURL.API)
			.header(OneDriveHttpHeader.Authorization, OneDriveHttpHeader.bearerValue(authHelper))
			.asJsonAsync(new DeleteItemCallback(future, callback));

		return future;
	}

	@Override
	public void close() {
		// TODO Auto-generated method stub

	}

	@Override
	public CompletableFuture<Result<Directory>> createDirectory(String pathName) {
		return  createDirectory(pathName, new NullCallback<Directory>());
	}

	@Override
	public CompletableFuture<Result<Directory>> createDirectory(String pathName,
			Callback<Directory> callback) {
		CompletableFuture<Result<Directory>> future = new CompletableFuture<Result<Directory>>();

		Unirest.get(OneDriveURL.API)
			.header(OneDriveHttpHeader.Authorization, OneDriveHttpHeader.bearerValue(authHelper))
			.asJsonAsync(new CreateDirectoryCallback(future, callback));

		return future;
	}

	@Override
	public CompletableFuture<Result<Directory>> getDirectory(String pathName) {
		return getDirectory(pathName, new NullCallback<Directory>());
	}

	@Override
	public CompletableFuture<Result<Directory>> getDirectory(String name, Callback<Directory> callback) {
		CompletableFuture<Result<Directory>> future = new CompletableFuture<Result<Directory>>();

		Unirest.get(OneDriveURL.API)
			.header(OneDriveHttpHeader.Authorization, OneDriveHttpHeader.bearerValue(authHelper))
			.asJsonAsync(new GetDirectoryCallback(future, callback));

		return future;
	}

	@Override
	public CompletableFuture<Result<File>> createFile(String pathName) {
		return createFile(pathName, new NullCallback<File>());
	}

	@Override
	public CompletableFuture<Result<File>> createFile(String pathName, Callback<File> callback) {
		CompletableFuture<Result<File>> future = new CompletableFuture<Result<File>>();

		Unirest.get(OneDriveURL.API)
			.header(OneDriveHttpHeader.Authorization, OneDriveHttpHeader.bearerValue(authHelper))
			.asJsonAsync(new CreateFileCallback(future, callback));

		return future;
	}

	@Override
	public CompletableFuture<Result<File>> getFile(String pathName) {
		return getFile(pathName, new NullCallback<File>());
	}

	@Override
	public CompletableFuture<Result<File>> getFile(String pathName, Callback<File> callback) {
		CompletableFuture<Result<File>> future = new CompletableFuture<Result<File>>();

		Unirest.get(OneDriveURL.API)
			.header(OneDriveHttpHeader.Authorization, OneDriveHttpHeader.bearerValue(authHelper))
			.asJsonAsync(new GetFileCallback(future, callback));

		return future;
	}

	@Override
	public CompletableFuture<Result<Children>> getChildren() {
		return getChildren(new NullCallback<Children>());
	}

	@Override
	public CompletableFuture<Result<Children>> getChildren(Callback<Children> callback) {
		CompletableFuture<Result<Children>> future = new CompletableFuture<Result<Children>>();

		Unirest.get(OneDriveURL.API)
			.header(OneDriveHttpHeader.Authorization, OneDriveHttpHeader.bearerValue(authHelper))
			.asJsonAsync(new GetChildrenCallback(future, callback));

		return future;
	}

	private class GetDirectoryInfoCallback implements com.mashape.unirest.http.async.Callback<JsonNode> {
		private final CompletableFuture<Result<DirectoryInfo>> future;
		private final Callback<DirectoryInfo> callback;

		GetDirectoryInfoCallback(CompletableFuture<Result<DirectoryInfo>> future,
								 Callback<DirectoryInfo> callback) {
			this.future = future;
			this.callback = callback;
		}
		@Override
		public void cancelled() {}
		@Override
		public void completed(HttpResponse<JsonNode> response) {
			// TODO
		}

		@Override
		public void failed(UnirestException exception) {
			// TODO
		}
	}

	private class MoveToCallback implements com.mashape.unirest.http.async.Callback<JsonNode> {
		private final CompletableFuture<Result<Directory>> future;
		private final Callback<Directory> callback;

		MoveToCallback(CompletableFuture<Result<Directory>> future, Callback<Directory> callback) {
			this.future = future;
			this.callback = callback;
		}
		@Override
		public void cancelled() {}
		@Override
		public void completed(HttpResponse<JsonNode> response) {
			// TODO
		}

		@Override
		public void failed(UnirestException exception) {
			// TODO
		}
	}

	private class CopyToCallback implements com.mashape.unirest.http.async.Callback<JsonNode> {
		private final CompletableFuture<Result<Directory>> future;
		private final Callback<Directory> callback;

		CopyToCallback(CompletableFuture<Result<Directory>> future, Callback<Directory> callback) {
			this.future = future;
			this.callback = callback;
		}
		@Override
		public void cancelled() {}
		@Override
		public void completed(HttpResponse<JsonNode> response) {
			// TODO
		}

		@Override
		public void failed(UnirestException exception) {
			// TODO
		}
	}

	private class DeleteItemCallback implements com.mashape.unirest.http.async.Callback<JsonNode> {
		private final CompletableFuture<Result<Status>> future;
		private final Callback<Status> callback;

		DeleteItemCallback(CompletableFuture<Result<Status>> future, Callback<Status> callback) {
			this.future = future;
			this.callback = callback;
		}
		@Override
		public void cancelled() {}
		@Override
		public void completed(HttpResponse<JsonNode> response) {
			// TODO
		}

		@Override
		public void failed(UnirestException exception) {
			// TODO
		}
	}

	private class CreateDirectoryCallback implements com.mashape.unirest.http.async.Callback<JsonNode> {
		private final CompletableFuture<Result<Directory>> future;
		private final Callback<Directory> callback;

		CreateDirectoryCallback(CompletableFuture<Result<Directory>> future, Callback<Directory> callback) {
			this.future = future;
			this.callback = callback;
		}
		@Override
		public void cancelled() {}
		@Override
		public void completed(HttpResponse<JsonNode> response) {
			// TODO
		}

		@Override
		public void failed(UnirestException exception) {
			// TODO
		}
	}

	private class GetDirectoryCallback implements com.mashape.unirest.http.async.Callback<JsonNode> {
		private final CompletableFuture<Result<Directory>> future;
		private final Callback<Directory> callback;

		GetDirectoryCallback(CompletableFuture<Result<Directory>> future, Callback<Directory> callback) {
			this.future = future;
			this.callback = callback;
		}
		@Override
		public void cancelled() {}
		@Override
		public void completed(HttpResponse<JsonNode> response) {
			// TODO
		}

		@Override
		public void failed(UnirestException exception) {
			// TODO
		}
	}

	private class CreateFileCallback implements com.mashape.unirest.http.async.Callback<JsonNode> {
		private final CompletableFuture<Result<File>> future;
		private final Callback<File> callback;

		CreateFileCallback(CompletableFuture<Result<File>> future, Callback<File> callback) {
			this.future = future;
			this.callback = callback;
		}
		@Override
		public void cancelled() {}
		@Override
		public void completed(HttpResponse<JsonNode> response) {
			// TODO
		}

		@Override
		public void failed(UnirestException exception) {
			// TODO
		}
	}

	private class GetFileCallback implements com.mashape.unirest.http.async.Callback<JsonNode> {
		private final CompletableFuture<Result<File>> future;
		private final Callback<File> callback;

		GetFileCallback(CompletableFuture<Result<File>> future, Callback<File> callback) {
			this.future = future;
			this.callback = callback;
		}
		@Override
		public void cancelled() {}
		@Override
		public void completed(HttpResponse<JsonNode> response) {
			// TODO
		}

		@Override
		public void failed(UnirestException exception) {
			// TODO
		}
	}

	private class GetChildrenCallback implements com.mashape.unirest.http.async.Callback<JsonNode> {
		private final CompletableFuture<Result<Children>> future;
		private final Callback<Children> callback;

		GetChildrenCallback(CompletableFuture<Result<Children>> future, Callback<Children> callback) {
			this.future = future;
			this.callback = callback;
		}
		@Override
		public void cancelled() {}
		@Override
		public void completed(HttpResponse<JsonNode> response) {
			// TODO
		}

		@Override
		public void failed(UnirestException exception) {
			// TODO
		}
	}
}
