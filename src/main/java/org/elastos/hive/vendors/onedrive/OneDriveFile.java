package org.elastos.hive.vendors.onedrive;

import java.util.HashMap;
import java.util.concurrent.CompletableFuture;

import org.elastos.hive.AuthHelper;
import org.elastos.hive.Callback;
import org.elastos.hive.File;
import org.elastos.hive.HiveException;
import org.elastos.hive.NullCallback;
import org.elastos.hive.UnirestAsyncCallback;
import org.elastos.hive.Void;
import org.json.JSONObject;

import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;

final class OneDriveFile extends File {
	private final AuthHelper authHelper;
	private String pathName;
	private volatile File.Info fileInfo;

	OneDriveFile(String pathName, File.Info fileInfo, AuthHelper authHelper) {
		this.fileInfo = fileInfo;
		this.pathName = pathName;
		this.authHelper = authHelper;
	}

	@Override
	public String getId() {
		return fileInfo.get(File.Info.itemId);
	}

	@Override
	public String getPath() {
		return pathName;
	}

	@Override
	public String getParentPath() {
		if (pathName.equals("/"))
			return pathName;

		return pathName.substring(0, pathName.lastIndexOf("/"));
	}

	@Override
	public File.Info getLastInfo() {
		return fileInfo;
	}

	@Override
	public CompletableFuture<File.Info> getInfo() {
		return getInfo(new NullCallback<File.Info>());
	}

	@Override
	public CompletableFuture<File.Info> getInfo(Callback<File.Info> callback)  {
		return authHelper.checkExpired()
				.thenCompose(placeHolder -> getInfo(placeHolder, callback));
	}

	private CompletableFuture<File.Info> getInfo(Void placeHolder, Callback<File.Info> callback) {
		CompletableFuture<File.Info> future = new CompletableFuture<File.Info>();

		if (callback == null)
			callback = new NullCallback<File.Info>();

		String url = String.format("%s/root:/%s", OneDriveURL.API, pathName)
						   .replace(" ", "%20");
		Unirest.get(url)
			.header(OneDriveHttpHeader.Authorization,
					OneDriveHttpHeader.bearerValue(authHelper))
			.asJsonAsync(new GetFileInfoCallback(future, callback));

		return future;
	}

	@Override
	public CompletableFuture<Void> moveTo(String pathName) {
		return moveTo(pathName, new NullCallback<Void>());
	}

	@Override
	public CompletableFuture<Void> moveTo(String pathName, Callback<Void> callback) {
		return authHelper.checkExpired()
				.thenCompose(placeHolder -> moveTo(placeHolder, pathName, callback));
	}

	private CompletableFuture<Void> moveTo(Void placeHolder, String pathName, Callback<Void> callback) {
		CompletableFuture<Void> future = new CompletableFuture<Void>();

		if (callback == null)
			callback = new NullCallback<Void>();

		if (!pathName.startsWith("/")) {
			HiveException e = new HiveException("Neet a absolute path.");
			callback.onError(e);
			future.completeExceptionally(e);
			return future;
		}

		if (this.pathName.equals("/")) {
			HiveException e = new HiveException("Can't move the root.");
			callback.onError(e);
			future.completeExceptionally(e);
			return future;
		}

		if (this.pathName.equals(pathName)) {
			HiveException e = new HiveException("Can't move to same path name");
			callback.onError(e);
			future.completeExceptionally(e);
			return future;
		}

		try {
			int LastPos = pathName.lastIndexOf("/");
			String name = pathName.substring(LastPos + 1);

			String url  = String.format("%s/root:%s", OneDriveURL.API, this.pathName)
								.replace(" ", "%20");
			String body = String.format("{\"parentReference\":{\"path\":\"/drive/root:%s\"},\"name\":\"%s\"}", pathName, name)
								.replace(" ", "%20");
			String newPathName = String.format("%s/%s", pathName, name);

			Unirest.patch(url)
				.header(OneDriveHttpHeader.Authorization,
						OneDriveHttpHeader.bearerValue(authHelper))
				.header(OneDriveHttpHeader.ContentType, OneDriveHttpHeader.Json)
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
	public CompletableFuture<Void> copyTo(String pathName) {
		return copyTo(pathName, new NullCallback<Void>());
	}

	@Override
	public CompletableFuture<Void> copyTo(String pathName, Callback<Void> callback) {
		return authHelper.checkExpired()
				.thenCompose(placeHolder -> copyTo(placeHolder, pathName, callback));
	}

	private CompletableFuture<Void> copyTo(Void placeHolder, String pathName, Callback<Void> callback) {
		CompletableFuture<Void> future = new CompletableFuture<Void>();

		if (callback == null)
			callback = new NullCallback<Void>();

		if (!pathName.startsWith("/")) {
			HiveException e = new HiveException("Neet a absolute path.");
			callback.onError(e);
			future.completeExceptionally(e);
			return future;
		}

		if (this.pathName.equals("/")) {
			HiveException e = new HiveException("Can't copy the root");
			callback.onError(e);
			future.completeExceptionally(e);
			return future;
		}

		if (this.pathName.equals(pathName)) {
			HiveException e = new HiveException("Can't copy to same path name");
			callback.onError(e);
			future.completeExceptionally(e);
			return future;
		}

		try {
			int LastPos = pathName.lastIndexOf("/");
			String name = pathName.substring(LastPos + 1);

			String url  = String.format("%s/root:%s:/copy", OneDriveURL.API, this.pathName)
							    .replace(" ", "%20");
			String body = String.format("{\"parentReference\":{\"path\":\"/drive/root:%s\"},\"name\":\"%s\"}", pathName, name)
								.replace(" ", "%20");

			Unirest.post(url)
				.header(OneDriveHttpHeader.Authorization,
						OneDriveHttpHeader.bearerValue(authHelper))
				.header(OneDriveHttpHeader.ContentType, OneDriveHttpHeader.Json)
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
	public CompletableFuture<Void> deleteItem() {
		return deleteItem(new NullCallback<Void>());
	}

	@Override
	public CompletableFuture<Void> deleteItem(Callback<Void> callback) {
		return authHelper.checkExpired()
				.thenCompose(placeHolder -> deleteItem(placeHolder, callback));
	}

	private CompletableFuture<Void> deleteItem(Void placeHolder, Callback<Void> callback) {
		CompletableFuture<Void> future = new CompletableFuture<Void>();

		if (callback == null)
			callback = new NullCallback<Void>();

		String url = String.format("%s/root:%s", OneDriveURL.API, this.pathName)
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
		private final CompletableFuture<File.Info> future;
		private final Callback<File.Info> callback;

		private GetFileInfoCallback(CompletableFuture<File.Info> future, Callback<File.Info> callback) {
			this.future = future;
			this.callback = callback;
		}
		@Override
		public void cancelled() {}

		@Override
		public void completed(HttpResponse<JsonNode> response) {
			if (response.getStatus() == 401) {
				authHelper.getToken().expired();
				HiveException e = new HiveException("Server Error: " + response.getStatusText());
				this.callback.onError(e);
				future.completeExceptionally(e);
				return;
			}

			if (response.getStatus() != 200) {
				HiveException e = new HiveException("Server Error: " + response.getStatusText());
				this.callback.onError(e);
				future.completeExceptionally(e);
				return;
			}

			JSONObject jsonObject = response.getBody().getObject();
			HashMap<String, String> attrs = new HashMap<>();
			attrs.put(File.Info.itemId, jsonObject.getString("id"));
			// TODO;

			File.Info info = new File.Info(attrs);
			this.callback.onSuccess(info);
			future.complete(fileInfo);
		}

		@Override
		public void failed(UnirestException exception) {
			HiveException e = new HiveException(exception.getMessage());
			this.callback.onError(e);
			future.completeExceptionally(e);
		}
	}

	private class MoveToCallback implements UnirestAsyncCallback<JsonNode> {
		private final String pathName;
		private final CompletableFuture<Void> future;
		private final Callback<Void> callback;

		MoveToCallback(String pathName, CompletableFuture<Void> future, Callback<Void> callback) {
			this.pathName = pathName;
			this.future = future;
			this.callback = callback;
		}

		@Override
		public void cancelled() {}

		@Override
		public void completed(HttpResponse<JsonNode> response) {
			if (response.getStatus() == 401) {
				authHelper.getToken().expired();
				HiveException e = new HiveException("Server Error: " + response.getStatusText());
				this.callback.onError(e);
				future.completeExceptionally(e);
				return;
			}

			if (response.getStatus() != 200) {
				HiveException e = new HiveException("Server Error: " + response.getStatusText());
				this.callback.onError(e);
				future.completeExceptionally(e);
				return;
			}

			OneDriveFile.this.pathName = pathName;
			Void placeHolder = new Void();
			this.callback.onSuccess(placeHolder);
			future.complete(placeHolder);
		}

		@Override
		public void failed(UnirestException exception) {
			HiveException e = new HiveException(exception.getMessage());
			this.callback.onError(e);
			future.completeExceptionally(e);
		}
	}

	private class CopyToCallback implements UnirestAsyncCallback<JsonNode> {
		private final CompletableFuture<Void> future;
		private final Callback<Void> callback;

		CopyToCallback(CompletableFuture<Void> future, Callback<Void> callback) {
			this.future = future;
			this.callback = callback;
		}
		@Override
		public void cancelled() {}

		@Override
		public void completed(HttpResponse<JsonNode> response) {
			if (response.getStatus() == 401) {
				authHelper.getToken().expired();
				HiveException e = new HiveException("Server Error: " + response.getStatusText());
				this.callback.onError(e);
				future.completeExceptionally(e);
				return;
			}

			if (response.getStatus() != 202) {
				HiveException e = new HiveException("Server Error: " + response.getStatusText());
				this.callback.onError(e);
				future.completeExceptionally(e);
				return;
			}

			Void placeHolder = new Void();
			this.callback.onSuccess(placeHolder);
			future.complete(placeHolder);
		}

		@Override
		public void failed(UnirestException exception) {
			HiveException e = new HiveException(exception.getMessage());
			this.callback.onError(e);
			future.completeExceptionally(e);
		}
	}

	private class DeleteItemCallback implements UnirestAsyncCallback<JsonNode> {
		private final CompletableFuture<Void> future;
		private final Callback<Void> callback;

		private DeleteItemCallback(CompletableFuture<Void> future, Callback<Void> callback) {
			this.future = future;
			this.callback = callback;
		}
		@Override
		public void cancelled() {}

		@Override
		public void completed(HttpResponse<JsonNode> response) {
			if (response.getStatus() == 401) {
				authHelper.getToken().expired();
				HiveException e = new HiveException("Server Error: " + response.getStatusText());
				this.callback.onError(e);
				future.completeExceptionally(e);
				return;
			}

			if (response.getStatus() != 204) {
				HiveException e = new HiveException("Server Error: " + response.getStatusText());
				this.callback.onError(e);
				future.completeExceptionally(e);
				return;
			}

			Void placeHolder = new Void();
			this.callback.onSuccess(placeHolder);
			future.complete(placeHolder);
		}

		@Override
		public void failed(UnirestException exception) {
			HiveException e = new HiveException(exception.getMessage());
			this.callback.onError(e);
			future.completeExceptionally(e);
		}
	}
}
