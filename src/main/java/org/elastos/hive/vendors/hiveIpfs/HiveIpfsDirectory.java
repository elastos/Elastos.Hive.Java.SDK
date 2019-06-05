package org.elastos.hive.vendors.hiveIpfs;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import org.elastos.hive.Callback;
import org.elastos.hive.Children;
import org.elastos.hive.Directory;
import org.elastos.hive.File;
import org.elastos.hive.HiveException;
import org.elastos.hive.NullCallback;
import org.elastos.hive.Status;
import org.elastos.hive.UnirestAsyncCallback;
import org.json.JSONObject;

import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;

class HiveIpfsDirectory extends Directory  {
	private String pathName;
	private volatile Directory.Info dirInfo;
		
	HiveIpfsDirectory(String pathName, Directory.Info dirInfo) {
		this.pathName = pathName;
		this.dirInfo = dirInfo;
	}

	@Override
	public String getId() {
		return dirInfo.getId();
	}

	@Override
	public Info getLastInfo() {
		return dirInfo;
	}

	@Override
	public CompletableFuture<Info> getInfo() {
		return getInfo(new NullCallback<Directory.Info>());
	}

	@Override
	public CompletableFuture<Info> getInfo(Callback<Info> callback) {
		CompletableFuture<Directory.Info> future = new CompletableFuture<Directory.Info>();

		if (callback == null)
			callback = new NullCallback<Directory.Info>();

		Unirest.get(HiveIpfsUtils.BASEURL)
			.header(HiveIpfsUtils.CONTENTTYPE, HiveIpfsUtils.TYPE_Json)
			.queryString(HiveIpfsUtils.UID, getId())
			.queryString(HiveIpfsUtils.PATH, pathName)
			.asJsonAsync(new GetDirInfoCallback(future, callback));

		return future;
	}

	@Override
	public CompletableFuture<Directory> createDirectory(String path) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public CompletableFuture<Directory> createDirectory(String path, Callback<Directory> callback) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public CompletableFuture<Directory> getDirectory(String path) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public CompletableFuture<Directory> getDirectory(String path, Callback<Directory> callback) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public CompletableFuture<File> createFile(String path) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public CompletableFuture<File> createFile(String path, Callback<File> callback) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public CompletableFuture<File> getFile(String path) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public CompletableFuture<File> getFile(String path, Callback<File> callback) {
		// TODO Auto-generated method stub
		return null;
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
	public CompletableFuture<Status> moveTo(String path) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public CompletableFuture<Status> moveTo(String path, Callback<Status> callback) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public CompletableFuture<Status> copyTo(String path) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public CompletableFuture<Status> copyTo(String path, Callback<Status> callback) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public CompletableFuture<Status> deleteItem() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public CompletableFuture<Status> deleteItem(Callback<Status> callback) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void close() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public CompletableFuture<Children> getChildren() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public CompletableFuture<Children> getChildren(Callback<Children> callback) {
		// TODO Auto-generated method stub
		return null;
	}

	private class GetDirInfoCallback implements UnirestAsyncCallback<JsonNode> {
		private final CompletableFuture<Directory.Info> future;
		private final Callback<Directory.Info> callback;

		GetDirInfoCallback(CompletableFuture<Directory.Info> future,
				   Callback<Directory.Info> callback) {
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
			dirInfo = new Directory.Info(jsonObject.getString("Hash"));
			this.callback.onSuccess(dirInfo);
			future.complete(dirInfo);
		}

		@Override
		public void failed(UnirestException exception) {
			HiveException ex = new HiveException(exception.getMessage());
			this.callback.onError(ex);
			future.completeExceptionally(ex);
		}
	}
}
