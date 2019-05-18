package org.elastos.hive.vendors.onedrive;

import java.util.concurrent.CompletableFuture;

import org.elastos.hive.AuthHelper;
import org.elastos.hive.Callback;
import org.elastos.hive.Children;
import org.elastos.hive.Directory;
import org.elastos.hive.DirectoryInfo;
import org.elastos.hive.File;
import org.elastos.hive.NullCallback;
import org.elastos.hive.Status;
import org.elastos.hive.UnirestAsyncCallback;

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
	public CompletableFuture<DirectoryInfo> getInfo() {
		return getInfo(new NullCallback<DirectoryInfo>());
	}

	@Override
	public CompletableFuture<DirectoryInfo> getInfo(Callback<DirectoryInfo> callback) {
		CompletableFuture<DirectoryInfo> future = new CompletableFuture<DirectoryInfo>();

		Unirest.get(OneDriveURL.API)
			.header(OneDriveHttpHeader.Authorization,
					OneDriveHttpHeader.bearerValue(authHelper))
			.asJsonAsync(new GetDirInfoCallback(future, callback));

		return future;
	}

	@Override
	public CompletableFuture<Directory> moveTo(String pathName) {
		return moveTo(pathName, new NullCallback<Directory>());
	}

	@Override
	public CompletableFuture<Directory> moveTo(String pathName, Callback<Directory> callback) {
		CompletableFuture<Directory> future = new CompletableFuture<Directory>();

		Unirest.get(OneDriveURL.API)
			.header(OneDriveHttpHeader.Authorization,
					OneDriveHttpHeader.bearerValue(authHelper))
			.asJsonAsync(new MoveToCallback(future, callback));

		return future;
	}

	@Override
	public CompletableFuture<Directory> copyTo(String pathName) {
		return copyTo(pathName, new NullCallback<Directory>());
	}

	@Override
	public CompletableFuture<Directory> copyTo(String pathName, Callback<Directory> callback) {
		CompletableFuture<Directory> future = new CompletableFuture<Directory>();

		Unirest.get(OneDriveURL.API)
			.header(OneDriveHttpHeader.Authorization,
					OneDriveHttpHeader.bearerValue(authHelper))
			.asJsonAsync(new MoveToCallback(future, callback));

		return future;
	}

	@Override
	public CompletableFuture<Status> deleteItem() {
		return deleteItem(new NullCallback<Status>());
	}

	@Override
	public CompletableFuture<Status> deleteItem(Callback<Status> callback) {
		CompletableFuture<Status> future = new CompletableFuture<Status>();

		Unirest.get(OneDriveURL.API)
			.header(OneDriveHttpHeader.Authorization,
					OneDriveHttpHeader.bearerValue(authHelper))
			.asJsonAsync(new DeleteItemCallback(future, callback));

		return future;
	}

	@Override
	public void close() {
		// TODO
	}

	@Override
	public CompletableFuture<Directory> createDirectory(String name) {
		return  createDirectory(name, new NullCallback<Directory>());
	}

	@Override
	public CompletableFuture<Directory> createDirectory(String name,
			Callback<Directory> callback) {
		CompletableFuture<Directory> future = new CompletableFuture<Directory>();

		Unirest.get(OneDriveURL.API)
			.header(OneDriveHttpHeader.Authorization,
					OneDriveHttpHeader.bearerValue(authHelper))
			.asJsonAsync(new CreateDirCallback(future, callback));

		return future;
	}

	@Override
	public CompletableFuture<Directory> getDirectory(String name) {
		return getDirectory(name, new NullCallback<Directory>());
	}

	@Override
	public CompletableFuture<Directory> getDirectory(String name, Callback<Directory> callback) {
		CompletableFuture<Directory> future = new CompletableFuture<Directory>();

		Unirest.get(OneDriveURL.API)
			.header(OneDriveHttpHeader.Authorization,
					OneDriveHttpHeader.bearerValue(authHelper))
			.asJsonAsync(new GetDirCallback(future, callback));

		return future;
	}

	@Override
	public CompletableFuture<File> createFile(String name) {
		return createFile(name, new NullCallback<File>());
	}

	@Override
	public CompletableFuture<File> createFile(String name, Callback<File> callback) {
		CompletableFuture<File> future = new CompletableFuture<File>();

		Unirest.get(OneDriveURL.API)
			.header(OneDriveHttpHeader.Authorization,
					OneDriveHttpHeader.bearerValue(authHelper))
			.asJsonAsync(new CreateFileCallback(future, callback));

		return future;
	}

	@Override
	public CompletableFuture<File> getFile(String name) {
		return getFile(name, new NullCallback<File>());
	}

	@Override
	public CompletableFuture<File> getFile(String name, Callback<File> callback) {
		CompletableFuture<File> future = new CompletableFuture<File>();

		Unirest.get(OneDriveURL.API)
			.header(OneDriveHttpHeader.Authorization,
					OneDriveHttpHeader.bearerValue(authHelper))
			.asJsonAsync(new GetFileCallback(future, callback));

		return future;
	}

	@Override
	public CompletableFuture<Children> getChildren() {
		return getChildren(new NullCallback<Children>());
	}

	@Override
	public CompletableFuture<Children> getChildren(Callback<Children> callback) {
		CompletableFuture<Children> future = new CompletableFuture<Children>();

		Unirest.get(OneDriveURL.API)
			.header(OneDriveHttpHeader.Authorization,
					OneDriveHttpHeader.bearerValue(authHelper))
			.asJsonAsync(new GetChildrenCallback(future, callback));

		return future;
	}

	private class GetDirInfoCallback implements UnirestAsyncCallback<JsonNode> {
		private final CompletableFuture<DirectoryInfo> future;
		private final Callback<DirectoryInfo> callback;

		GetDirInfoCallback(CompletableFuture<DirectoryInfo> future,
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

	private class MoveToCallback implements UnirestAsyncCallback<JsonNode> {
		private final CompletableFuture<Directory> future;
		private final Callback<Directory> callback;

		MoveToCallback(CompletableFuture<Directory> future, Callback<Directory> callback) {
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

	private class CopyToCallback implements UnirestAsyncCallback<JsonNode> {
		private final CompletableFuture<Directory> future;
		private final Callback<Directory> callback;

		CopyToCallback(CompletableFuture<Directory> future, Callback<Directory> callback) {
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

	private class DeleteItemCallback implements UnirestAsyncCallback<JsonNode> {
		private final CompletableFuture<Status> future;
		private final Callback<Status> callback;

		DeleteItemCallback(CompletableFuture<Status> future, Callback<Status> callback) {
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

	private class CreateDirCallback implements UnirestAsyncCallback<JsonNode> {
		private final CompletableFuture<Directory> future;
		private final Callback<Directory> callback;

		CreateDirCallback(CompletableFuture<Directory> future, Callback<Directory> callback) {
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

	private class GetDirCallback implements UnirestAsyncCallback<JsonNode> {
		private final CompletableFuture<Directory> future;
		private final Callback<Directory> callback;

		GetDirCallback(CompletableFuture<Directory> future, Callback<Directory> callback) {
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

	private class CreateFileCallback implements UnirestAsyncCallback<JsonNode> {
		private final CompletableFuture<File> future;
		private final Callback<File> callback;

		CreateFileCallback(CompletableFuture<File> future, Callback<File> callback) {
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

	private class GetFileCallback implements UnirestAsyncCallback<JsonNode> {
		private final CompletableFuture<File> future;
		private final Callback<File> callback;

		GetFileCallback(CompletableFuture<File> future, Callback<File> callback) {
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

	private class GetChildrenCallback implements UnirestAsyncCallback<JsonNode> {
		private final CompletableFuture<Children> future;
		private final Callback<Children> callback;

		GetChildrenCallback(CompletableFuture<Children> future, Callback<Children> callback) {
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
