package org.elastos.hive.vendors.onedrive;

import org.elastos.hive.AuthHelper;
import org.elastos.hive.Callback;
import org.elastos.hive.Directory;
import org.elastos.hive.File;
import org.elastos.hive.HiveException;
import org.elastos.hive.Length;
import org.elastos.hive.NullCallback;
import org.elastos.hive.Void;
import org.elastos.hive.utils.LogUtil;
import org.elastos.hive.vendors.onedrive.Model.BaseServiceConfig;
import org.elastos.hive.vendors.onedrive.Model.DirOrFileInfoResponse;
import org.elastos.hive.vendors.onedrive.Model.MoveAndCopyReqest;
import org.elastos.hive.vendors.onedrive.network.Api;
import org.elastos.hive.vendors.onedrive.network.BaseServiceUtil;

import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.concurrent.CompletableFuture;

import retrofit2.Call;
import retrofit2.Response;

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

		try {
			BaseServiceConfig baseServiceConfig = new BaseServiceConfig(true,true,authHelper.getToken(),false);
			Api api = BaseServiceUtil.createService(Api.class, Constance.ONE_DRIVE_API_BASE_URL ,baseServiceConfig);
			Call call = api.getDirAndFileInfo(pathName);
			call.enqueue(new FileCallback(future , callback ,pathName, Type.GET_INFO));
		} catch (Exception e) {
			e.printStackTrace();
		}

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

			String newPathName = pathName + "/" +name ;

			MoveAndCopyReqest moveAndCopyReqest = new MoveAndCopyReqest(pathName,name);
			BaseServiceConfig baseServiceConfig = new BaseServiceConfig(true,true,authHelper.getToken(),false);
			Api api = BaseServiceUtil.createService(Api.class, Constance.ONE_DRIVE_API_BASE_URL ,baseServiceConfig);
			Call call = api.moveTo(this.pathName,moveAndCopyReqest);
			call.enqueue(new FileCallback(future , callback ,newPathName, Type.MOVE_TO));

		} catch (Exception ex) {
			HiveException e = new HiveException("connect exception: " + ex.getMessage());
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

			MoveAndCopyReqest moveAndCopyReqest = new MoveAndCopyReqest(pathName,name);
			BaseServiceConfig baseServiceConfig = new BaseServiceConfig(true,true,authHelper.getToken(),true);
			Api api = BaseServiceUtil.createService(Api.class, Constance.ONE_DRIVE_API_BASE_URL ,baseServiceConfig);
			Call call = api.copyTo(this.pathName , moveAndCopyReqest);
			call.enqueue(new FileCallback(future , callback ,pathName, Type.COPY_TO));

		} catch (Exception ex) {
			HiveException e = new HiveException("connect exception: " + ex.getMessage());
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

		try {
			BaseServiceConfig baseServiceConfig = new BaseServiceConfig(true,true,authHelper.getToken(),false);
			Api api = BaseServiceUtil.createService(Api.class, Constance.ONE_DRIVE_API_BASE_URL ,baseServiceConfig);
			Call call = api.deleteItem(this.pathName);
			call.enqueue(new FileCallback(future , callback ,pathName, Type.DELETE_ITEM));
		} catch (Exception e) {
			e.printStackTrace();
		}

		return future;
	}

	@Override
	public void close() {
		// TODO
	}

	@Override
	public CompletableFuture<Length> read(ByteBuffer dest) {
		// TODO
		return null;
	}

	@Override
	public CompletableFuture<Length> read(ByteBuffer dest, long position) {
		// TODO
		return null;
	}

	@Override
	public CompletableFuture<Length> read(ByteBuffer dest, long position, Callback<Length> callback) {
		// TODO
		return null;
	}

	@Override
	public CompletableFuture<Length> write(ByteBuffer dest) {
		// TODO
		return null;
	}

	@Override
	public CompletableFuture<Length> write(ByteBuffer dest, long position) {
		// TODO
		return null;
	}

	@Override
	public CompletableFuture<Length> write(ByteBuffer dest, long position, Callback<Length> callback) {
		// TODO
		return null;
	}

	@Override
	public CompletableFuture<Void> commit() {
		// TODO
		return null;
	}

	@Override
	public void discard() {
		// TODO
	}


	private class FileCallback implements retrofit2.Callback{
		private final String pathName;
		private final CompletableFuture future;
		private final Callback callback;
		private final Type type ;

		public FileCallback(CompletableFuture future , Callback callback ,String pathName , Type type) {
			this.future = future ;
			this.callback = callback ;
			this.pathName = pathName ;
			this.type = type ;
		}

		@Override
		public void onResponse(Call call, Response response) {
			if (response.code() == 401) {
				authHelper.getToken().expired();
				HiveException e = new HiveException("Server Error: " + response.message());
				this.callback.onError(e);
				future.completeExceptionally(e);
				return;
			}
			if (response.code() != 200 && response.code() != 201 && response.code() != 202 && response.code() != 204) {
				HiveException ex = new HiveException("Server Error: " + response.message());
				this.callback.onError(ex);
				future.completeExceptionally(ex);
				return;
			}

			switch (type){
				case GET_INFO:
					DirOrFileInfoResponse dirInfoResponse = (DirOrFileInfoResponse) response.body();
					HashMap<String, String> attrs = new HashMap<>();
					attrs.put(Directory.Info.itemId, dirInfoResponse.getId());

					File.Info info = new File.Info(attrs);
					this.callback.onSuccess(info);
					future.complete(info);
					break;
				case MOVE_TO:
					OneDriveFile.this.pathName = pathName;
				case COPY_TO:
				case DELETE_ITEM:
					Void placeHolder = new Void();
					this.callback.onSuccess(placeHolder);
					future.complete(placeHolder);
					break;
				default:
					break;

			}
		}

		@Override
		public void onFailure(Call call, Throwable t) {
			LogUtil.d("t = "+t.getMessage());
			t.printStackTrace();

			HiveException e = new HiveException(t.getMessage());
			this.callback.onError(e);
			future.completeExceptionally(e);
		}
	}

	private enum Type{
		GET_INFO , COPY_TO , MOVE_TO , DELETE_ITEM
	}
}
