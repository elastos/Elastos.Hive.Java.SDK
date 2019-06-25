package org.elastos.hive.vendors.onedrive;

import org.elastos.hive.AuthHelper;
import org.elastos.hive.Callback;
import org.elastos.hive.Children;
import org.elastos.hive.Directory;
import org.elastos.hive.File;
import org.elastos.hive.HiveException;
import org.elastos.hive.ItemInfo;
import org.elastos.hive.NullCallback;
import org.elastos.hive.Void;
import org.elastos.hive.utils.LogUtil;
import org.elastos.hive.vendors.onedrive.Model.BaseServiceConfig;
import org.elastos.hive.vendors.onedrive.Model.CreateDirRequest;
import org.elastos.hive.vendors.onedrive.Model.DirChildrenResponse;
import org.elastos.hive.vendors.onedrive.Model.DirOrFileInfoResponse;
import org.elastos.hive.vendors.onedrive.Model.MoveAndCopyReqest;
import org.elastos.hive.vendors.onedrive.Model.FileOrDirPropResponse;
import org.elastos.hive.vendors.onedrive.network.Api;
import org.elastos.hive.vendors.onedrive.network.BaseServiceUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import retrofit2.Call;
import retrofit2.Response;

class OneDriveDirectory extends Directory {
	private final AuthHelper authHelper;
	private String pathName;
	private Directory.Info dirInfo;

	OneDriveDirectory(String pathName, Directory.Info dirInfo, AuthHelper authHelper) {
		this.authHelper = authHelper;
		this.pathName = pathName;
		this.dirInfo = dirInfo;
	}

	@Override
	public String getId() {
		return dirInfo.get(Directory.Info.itemId);
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
	public Directory.Info getLastInfo() {
		return dirInfo;
	}

	@Override
	public CompletableFuture<Directory.Info> getInfo() {
		return getInfo(new NullCallback<Directory.Info>());
	}

	@Override
	public CompletableFuture<Directory.Info> getInfo(Callback<Directory.Info> callback) {
		return authHelper.checkExpired()
				.thenCompose(placeHolder -> getInfo(placeHolder, callback));
	}

	private CompletableFuture<Directory.Info> getInfo(Void placeHolder,  Callback<Directory.Info> callback) {
		CompletableFuture<Directory.Info> future = new CompletableFuture<Directory.Info>();

		if (callback == null)
			callback = new NullCallback<Directory.Info>();

		try {
			BaseServiceConfig baseServiceConfig = new BaseServiceConfig(true,true,authHelper.getToken(),false);
			Api api = BaseServiceUtil.createService(Api.class, Constance.ONE_DRIVE_API_BASE_URL ,baseServiceConfig);
			Call call = api.getDirAndFileInfo(pathName);
			call.enqueue(new DirectoryCallback(future , callback ,pathName, Type.GET_INFO));
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

		// the pathname must be a absolute path name
		if (!pathName.startsWith("/")) {
			HiveException e = new HiveException("Need a absolute path to moveTo");
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
			int LastPos = this.pathName.lastIndexOf("/");
			String name = this.pathName.substring(LastPos + 1);

			try {
				BaseServiceConfig baseServiceConfig = new BaseServiceConfig(true,true,authHelper.getToken(),true);
				Api api = BaseServiceUtil.createService(Api.class, Constance.ONE_DRIVE_API_BASE_URL ,baseServiceConfig);
				Call call = api.moveTo(this.pathName , new MoveAndCopyReqest(pathName,name));
				call.enqueue(new DirectoryCallback(future , callback ,pathName+"/"+name, Type.MOVE_TO));
			} catch (Exception e) {
				e.printStackTrace();
			}
		} catch (Exception ex) {
			HiveException e = new HiveException(ex.getMessage());
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
			HiveException e = new HiveException("Need a absolute path to copyTo");
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
			int LastPos = this.pathName.lastIndexOf("/");
			String name = this.pathName.substring(LastPos + 1);

			try {
				BaseServiceConfig baseServiceConfig = new BaseServiceConfig(true,true,authHelper.getToken(),true);
				Api api = BaseServiceUtil.createService(Api.class, Constance.ONE_DRIVE_API_BASE_URL ,baseServiceConfig);
				Call call = api.copyTo(this.pathName , new MoveAndCopyReqest(pathName,name));
				call.enqueue(new DirectoryCallback(future , callback ,pathName, Type.COPY_TO));
			} catch (Exception e) {
				e.printStackTrace();
			}

		} catch (Exception ex) {
			HiveException e = new HiveException(ex.getMessage());
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

		if (pathName.equals("/")) {
			HiveException e = new HiveException("Can't delete the root.");
			callback.onError(e);
			future.completeExceptionally(e);
			return future;
		}

		try {
			BaseServiceConfig baseServiceConfig = new BaseServiceConfig(true,true,authHelper.getToken(),true);
			Api api = BaseServiceUtil.createService(Api.class, Constance.ONE_DRIVE_API_BASE_URL ,baseServiceConfig);
			Call call = api.deleteItem(this.pathName);
			call.enqueue(new DirectoryCallback(future , callback ,pathName, Type.DELETE_ITEM));
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
	public CompletableFuture<Directory> createDirectory(String name) {
		return  createDirectory(name, new NullCallback<Directory>());
	}

	@Override
	public CompletableFuture<Directory> createDirectory(String name, Callback<Directory> callback) {
		return authHelper.checkExpired()
				.thenCompose(placeHolder -> createDirectory(placeHolder, name, callback));
	}

	private CompletableFuture<Directory> createDirectory(Void placeHolder, String name, Callback<Directory> callback) {
		CompletableFuture<Directory> future = new CompletableFuture<Directory>();

		if (callback == null)
			callback = new NullCallback<Directory>();

		if (name.contains("/")) {
			HiveException e = new HiveException("Only need the last part of directory name");
			callback.onError(e);
			future.completeExceptionally(e);
			return future;
		}

        try {
            BaseServiceConfig baseServiceConfig = new BaseServiceConfig(true,true,authHelper.getToken(),true);
            Api api = BaseServiceUtil.createService(Api.class, Constance.ONE_DRIVE_API_BASE_URL ,baseServiceConfig);

			CreateDirRequest createDirRequest = new CreateDirRequest(name);
            Call call = api.createDirFromDir(this.pathName, createDirRequest);

            call.enqueue(new DirectoryCallback(future , callback ,this.pathName+"/"+name, Type.CREATE_DIR));
        } catch (Exception e) {
            e.printStackTrace();
        }

		return future;
	}

	@Override
	public CompletableFuture<Directory> getDirectory(String name) {
		return getDirectory(name, new NullCallback<Directory>());
	}

	@Override
	public CompletableFuture<Directory> getDirectory(String name, Callback<Directory> callback) {
		return authHelper.checkExpired()
				.thenCompose(status -> getDirectory(status, name, callback));
	}

	private CompletableFuture<Directory> getDirectory(Void status, String name, Callback<Directory> callback) {
		CompletableFuture<Directory> future = new CompletableFuture<Directory>();

		if (callback == null)
			callback = new NullCallback<Directory>();

		if (name.contains("/")) {
			HiveException e = new HiveException("Only need the the last part of directory name");
			callback.onError(e);
			future.completeExceptionally(e);
			return future;
		}
		String path = this.pathName + "/" +name ;

		try {
			BaseServiceConfig baseServiceConfig = new BaseServiceConfig(true,true,authHelper.getToken(),true);
			Api api = BaseServiceUtil.createService(Api.class, Constance.ONE_DRIVE_API_BASE_URL ,baseServiceConfig);

			Call call = api.getDirFromDir(path);

			call.enqueue(new DirectoryCallback(future , callback ,path, Type.GET_DIR));
		} catch (Exception e) {
			e.printStackTrace();
		}
		return future;
	}

	@Override
	public CompletableFuture<File> createFile(String name) {
		return createFile(name, new NullCallback<File>());
	}

	@Override
	public CompletableFuture<File> createFile(String name, Callback<File> callback) {
		return authHelper.checkExpired()
				.thenCompose(status -> createFile(status, name, callback));
	}

	private CompletableFuture<File> createFile(Void status, String name, Callback<File> callback) {
		CompletableFuture<File> future = new CompletableFuture<File>();

		if (callback == null)
			callback = new NullCallback<File>();

		if (name.contains("/")) {
			HiveException e = new HiveException("Only need the name of a file.");
			callback.onError(e);
			future.completeExceptionally(e);
			return future;
		}

		String path = this.pathName+"/"+name ;

		try {
			BaseServiceConfig baseServiceConfig = new BaseServiceConfig(true,true,authHelper.getToken(),false);
			Api api = BaseServiceUtil.createService(Api.class, Constance.ONE_DRIVE_API_BASE_URL ,baseServiceConfig);
			Call call = api.createFile(path);
			call.enqueue(new DirectoryCallback(future , callback , path , Type.CREATE_FILE));
		} catch (Exception e) {
			e.printStackTrace();
		}
		return future;
	}

	@Override
	public CompletableFuture<File> getFile(String name) {
		return getFile(name, new NullCallback<File>());
	}

	@Override
	public CompletableFuture<File> getFile(String name, Callback<File> callback) {
		return authHelper.checkExpired()
				.thenCompose(status -> getFile(status, name, callback));
	}

	private CompletableFuture<File> getFile(Void status, String name, Callback<File> callback) {
		CompletableFuture<File> future = new CompletableFuture<File>();

		if (callback == null)
			callback = new NullCallback<File>();

		if (name.contains("/")) {
			HiveException e = new HiveException("Only need the name of a file.");
			callback.onError(e);
			future.completeExceptionally(e);
			return future;
		}

		String path = this.pathName+"/"+name;

		try {
			BaseServiceConfig baseServiceConfig = new BaseServiceConfig(true,true,authHelper.getToken(),false);
			Api api = BaseServiceUtil.createService(Api.class, Constance.ONE_DRIVE_API_BASE_URL ,baseServiceConfig);
			Call call = api.getFileFromDir(path);
			call.enqueue(new DirectoryCallback(future , callback , path , Type.GET_FILE));
		} catch (Exception e) {
			e.printStackTrace();
		}

		return future;
	}

	@Override
	public CompletableFuture<Children> getChildren() {
		return getChildren(new NullCallback<Children>());
	}

	@Override
	public CompletableFuture<Children> getChildren(Callback<Children> callback) {
		return authHelper.checkExpired()
				.thenCompose(status -> getChildren(status,callback));
	}

	private CompletableFuture<Children> getChildren(Void status, Callback<Children> callback) {
		CompletableFuture<Children> future = new CompletableFuture<Children>();

		if (callback == null)
			callback = new NullCallback<Children>();

		try {
			BaseServiceConfig baseServiceConfig = new BaseServiceConfig(true,true,authHelper.getToken(),false);
			Api api = BaseServiceUtil.createService(Api.class, Constance.ONE_DRIVE_API_BASE_URL ,baseServiceConfig);
			Call call = api.getChildren(this.pathName);
			call.enqueue(new DirectoryCallback(future , callback , this.pathName , Type.GET_CHILDREN));
		} catch (Exception e) {
			e.printStackTrace();
		}

		return future;
	}

	private class DirectoryCallback implements retrofit2.Callback{
		private final String pathName;
		private final CompletableFuture future;
		private final Callback callback;
		private final Type type ;

		public DirectoryCallback(CompletableFuture future , Callback callback ,String pathName , Type type) {
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

					Directory.Info info = new Directory.Info(attrs);
					this.callback.onSuccess(info);
					future.complete(dirInfo);
					break;
				case MOVE_TO:
					OneDriveDirectory.this.pathName = pathName;
				case COPY_TO:
				case DELETE_ITEM:
					Void placeHolder = new Void();
					this.callback.onSuccess(placeHolder);
					future.complete(placeHolder);
					break;
				case CREATE_DIR:
				case GET_DIR:

					FileOrDirPropResponse dirResponse= (FileOrDirPropResponse) response.body();

					if (dirResponse.getFolder() == null) {
						HiveException e = new HiveException("This is not a folder");
						this.callback.onError(e);
						future.completeExceptionally(e);
						return;
					}

					HashMap<String, String> dirAttrs = new HashMap<>();
					dirAttrs.put(Directory.Info.itemId, dirResponse.getId());

					Directory.Info dirInfo_ = new Directory.Info(dirAttrs);
					OneDriveDirectory directory = new OneDriveDirectory(pathName,dirInfo_,authHelper);
					this.callback.onSuccess(directory);
					future.complete(directory);
					break;
				case CREATE_FILE:
				case GET_FILE:

					FileOrDirPropResponse filePropResponse= (FileOrDirPropResponse) response.body();

					if (filePropResponse.getFolder()!=null) {
						HiveException e = new HiveException("This is not a file");
						this.callback.onError(e);
						future.completeExceptionally(e);
						return;
					}

					HashMap<String, String> fileAttrs = new HashMap<>();
					fileAttrs.put(File.Info.itemId, filePropResponse.getId());

					File.Info fileInfo = new File.Info(fileAttrs);
					OneDriveFile file = new OneDriveFile(pathName, fileInfo, authHelper);
					this.callback.onSuccess(file);
					future.complete(file);

					break;
				case GET_CHILDREN:
					DirChildrenResponse dirChildrenResponse = (DirChildrenResponse) response.body();

					LogUtil.d("dirChildrenResponse.toString() = "+dirChildrenResponse.toString());

					List<DirChildrenResponse.ValueBean> list = dirChildrenResponse.getValue();
					ArrayList<ItemInfo> itemInfos = new ArrayList<>(list.size());
					for (DirChildrenResponse.ValueBean value : list){
						HashMap<String , String> childrenAttrs = new HashMap<>();
						childrenAttrs.put(ItemInfo.itemId,value.getId());

						childrenAttrs.put(ItemInfo.type, value.getFolder()!=null ? "Folder": "File");

						itemInfos.add(new ItemInfo(childrenAttrs));
					}

					Children children = new Children(itemInfos);
					this.callback.onSuccess(children);
					future.complete(children);

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
		GET_INFO, MOVE_TO , COPY_TO , DELETE_ITEM , CREATE_DIR ,
		GET_DIR , CREATE_FILE , GET_FILE , GET_CHILDREN
	}
}
