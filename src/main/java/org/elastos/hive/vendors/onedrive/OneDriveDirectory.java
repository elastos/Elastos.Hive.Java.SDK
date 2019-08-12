/*
 * Copyright (c) 2019 Elastos Foundation
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

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
import org.elastos.hive.vendors.connection.ConnectionManager;
import org.elastos.hive.vendors.onedrive.network.OneDriveApi;
import org.elastos.hive.vendors.onedrive.network.model.CreateDirRequest;
import org.elastos.hive.vendors.onedrive.network.model.DirChildrenResponse;
import org.elastos.hive.vendors.onedrive.network.model.FileOrDirPropResponse;
import org.elastos.hive.vendors.onedrive.network.model.MoveAndCopyReqest;

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
				.thenCompose(padding -> getInfo(padding, callback));
	}

	private CompletableFuture<Directory.Info> getInfo(Void padding,  Callback<Directory.Info> callback) {
		CompletableFuture<Directory.Info> future = new CompletableFuture<Directory.Info>();

		if (callback == null)
			callback = new NullCallback<Directory.Info>();

		try {
			OneDriveApi api = ConnectionManager.getOnedriveApi();
			Call<FileOrDirPropResponse> call;
			if (this.pathName.equals("/")) {
				//Get the root directory's info
				call = api.getRootDirectoryInfo();
			}
			else {
				//Get the other directory's info
				call = api.getDirAndFileInfo(this.pathName);
			}

			call.enqueue(new DirectoryCallback(future , callback ,pathName, Type.GET_INFO));
		} catch (Exception ex) {
			HiveException e = new HiveException(ex.getMessage());
			callback.onError(e);
			future.completeExceptionally(e);
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
				.thenCompose(padding -> moveTo(padding, pathName, callback));
	}

	private CompletableFuture<Void> moveTo(Void padding, String parentPath, Callback<Void> callback) {
		CompletableFuture<Void> future = new CompletableFuture<Void>();

		if (callback == null)
			callback = new NullCallback<Void>();

		// the pathname must be a absolute path name
		if (!parentPath.startsWith("/")) {
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

		if (this.pathName.equals(parentPath)) {
			HiveException e = new HiveException("Can't move to same path name");
			callback.onError(e);
			future.completeExceptionally(e);
			return future;
		}

		try {
			int LastPos = this.pathName.lastIndexOf("/");
			String name = this.pathName.substring(LastPos + 1);

			ConnectionManager.getOnedriveApi()
					.moveTo(this.pathName , new MoveAndCopyReqest(parentPath, name))
					.enqueue(new DirectoryCallback(future , callback ,parentPath + "/" + name, Type.MOVE_TO));
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
				.thenCompose(padding -> copyTo(padding, pathName, callback));
	}

	private CompletableFuture<Void> copyTo(Void padding, String parentPath, Callback<Void> callback) {
		CompletableFuture<Void> future = new CompletableFuture<Void>();

		if (callback == null)
			callback = new NullCallback<Void>();

		if (!parentPath.startsWith("/")) {
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

		if (this.pathName.equals(parentPath)) {
			HiveException e = new HiveException("Can't copy to same path name");
			callback.onError(e);
			future.completeExceptionally(e);
			return future;
		}

		try {
			int LastPos = this.pathName.lastIndexOf("/");
			String name = this.pathName.substring(LastPos + 1);

			ConnectionManager.getOnedriveApi()
					.copyTo(this.pathName , new MoveAndCopyReqest(parentPath, name))
					.enqueue(new DirectoryCallback(future , callback ,pathName, Type.COPY_TO));
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
				.thenCompose(padding -> deleteItem(padding, callback));
	}

	private CompletableFuture<Void> deleteItem(Void padding, Callback<Void> callback) {
		CompletableFuture<Void> future = new CompletableFuture<Void>();

		if (callback == null)
			callback = new NullCallback<Void>();

		if (pathName.equals("/")) {
			HiveException e = new HiveException("Can't delete the root directory");
			callback.onError(e);
			future.completeExceptionally(e);
			return future;
		}

		try {
			ConnectionManager.getOnedriveApi()
					.deleteItem(this.pathName)
					.enqueue(new DirectoryCallback(future , callback ,pathName, Type.DELETE_ITEM));
		} catch (Exception ex) {
			HiveException e = new HiveException(ex.getMessage());
			callback.onError(e);
			future.completeExceptionally(e);
		}

		return future;
	}

	@Override
	public CompletableFuture<Directory> createDirectory(String name) {
		return  createDirectory(name, new NullCallback<Directory>());
	}

	@Override
	public CompletableFuture<Directory> createDirectory(String name, Callback<Directory> callback) {
		return authHelper.checkExpired()
				.thenCompose(padding -> createDirectory(padding, name, callback));
	}

	private CompletableFuture<Directory> createDirectory(Void padding, String name, Callback<Directory> callback) {
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
    		String urlPath;
    		if (pathName.equals("/")) {
    			urlPath = "/root/children";
    		}
    		else {
    			urlPath = "/root:/"+pathName+":/children";
    		}

			CreateDirRequest createDirRequest = new CreateDirRequest(name);
			ConnectionManager.getOnedriveApi()
					.createDir(urlPath, createDirRequest)
					.enqueue(new DirectoryCallback(future , callback ,this.pathName + "/"+name, Type.CREATE_DIR));
        } catch (Exception ex) {
        	HiveException e = new HiveException(ex.getMessage());
			callback.onError(e);
			future.completeExceptionally(e);
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
				.thenCompose(padding -> getDirectory(padding, name, callback));
	}

	private CompletableFuture<Directory> getDirectory(Void padding, String name, Callback<Directory> callback) {
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
			ConnectionManager.getOnedriveApi()
					.getDirFromDir(path)
					.enqueue(new DirectoryCallback(future , callback ,path, Type.GET_DIR));
		} catch (Exception ex) {
			HiveException e = new HiveException(ex.getMessage());
			callback.onError(e);
			future.completeExceptionally(e);
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
				.thenCompose(padding -> createFile(padding, name, callback));
	}

	private CompletableFuture<File> createFile(Void padding, String name, Callback<File> callback) {
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
			ConnectionManager.getOnedriveApi()
					.createFile(path)
					.enqueue(new DirectoryCallback(future , callback , path , Type.CREATE_FILE));
		} catch (Exception ex) {
			HiveException e = new HiveException(ex.getMessage());
			callback.onError(e);
			future.completeExceptionally(e);
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
				.thenCompose(padding -> getFile(padding, name, callback));
	}

	private CompletableFuture<File> getFile(Void padding, String name, Callback<File> callback) {
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
			ConnectionManager.getOnedriveApi()
					.getFileFromDir(path)
					.enqueue(new DirectoryCallback(future , callback , path , Type.GET_FILE));
		} catch (Exception ex) {
			HiveException e = new HiveException(ex.getMessage());
			callback.onError(e);
			future.completeExceptionally(e);
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
				.thenCompose(padding -> getChildren(padding,callback));
	}

	private CompletableFuture<Children> getChildren(Void padding, Callback<Children> callback) {
		CompletableFuture<Children> future = new CompletableFuture<Children>();

		if (callback == null)
			callback = new NullCallback<Children>();

		try {
			OneDriveApi api = ConnectionManager.getOnedriveApi();
			Call<DirChildrenResponse> call;
			if (this.pathName.equals("/")) {
				//Get the root directory's children
				call = api.getRootChildren();
			}
			else {
				//Get the other directory's children
				call = api.getChildren(this.pathName);
			}

			call.enqueue(new DirectoryCallback(future , callback , this.pathName , Type.GET_CHILDREN));
		} catch (Exception ex) {
			HiveException e = new HiveException(ex.getMessage());
			callback.onError(e);
			future.completeExceptionally(e);
		}

		return future;
	}

	private class DirectoryCallback implements retrofit2.Callback{
		private final String pathName;
		private final CompletableFuture future;
		private final Callback callback;
		private final Type type ;

		DirectoryCallback(CompletableFuture future , Callback callback ,String pathName , Type type) {
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
			if (response.code() != 200 &&
				response.code() != 201 &&
				response.code() != 202 &&
				response.code() != 204) {
				HiveException ex = new HiveException("Server Error: " + response.message());
				this.callback.onError(ex);
				future.completeExceptionally(ex);
				return;
			}


			switch (type){
				case GET_INFO: {
					FileOrDirPropResponse dirInfoResponse = (FileOrDirPropResponse) response.body();
					HashMap<String, String> attrs = new HashMap<>();
					attrs.put(Info.itemId, dirInfoResponse.getId());
					attrs.put(Info.name, dirInfoResponse.getName());
					attrs.put(Info.childCount, Integer.toString(dirInfoResponse.getFolder().getChildCount()));

					Directory.Info info = new Directory.Info(attrs);
					this.callback.onSuccess(info);
					future.complete(dirInfo);
					break;
				}
				case MOVE_TO:
					OneDriveDirectory.this.pathName = pathName;
				case COPY_TO:
				case DELETE_ITEM:
					Void padding = new Void();
					this.callback.onSuccess(padding);
					future.complete(padding);
					break;

				case CREATE_DIR:
				case GET_DIR:
					FileOrDirPropResponse dirResponse= (FileOrDirPropResponse) response.body();

					if (dirResponse == null || dirResponse.getFolder() == null) {
						HiveException e = new HiveException("This is not a folder");
						this.callback.onError(e);
						future.completeExceptionally(e);
						return;
					}

					HashMap<String, String> dirAttrs = new HashMap<>();
					dirAttrs.put(Info.itemId, dirResponse.getId());
					dirAttrs.put(Info.name, dirResponse.getName());
					if (type == Type.CREATE_DIR) {
						dirAttrs.put(Info.childCount, "0");
					}
					else {
						dirAttrs.put(Info.childCount, Integer.toString(dirResponse.getFolder().getChildCount()));
					}

					Directory.Info dirInfo = new Directory.Info(dirAttrs);
					OneDriveDirectory directory = new OneDriveDirectory(pathName, dirInfo, authHelper);
					this.callback.onSuccess(directory);
					future.complete(directory);
					break;

				case CREATE_FILE:
				case GET_FILE:
					FileOrDirPropResponse filePropResponse= (FileOrDirPropResponse) response.body();

					if (filePropResponse == null || filePropResponse.getFolder() != null) {
						HiveException e = new HiveException("This is not a file");
						this.callback.onError(e);
						future.completeExceptionally(e);
						return;
					}

					HashMap<String, String> fileAttrs = new HashMap<>();
					fileAttrs.put(File.Info.itemId, filePropResponse.getId());
					fileAttrs.put(File.Info.name, filePropResponse.getName());
					fileAttrs.put(File.Info.size, Integer.toString(filePropResponse.getSize()));

					File.Info fileInfo = new File.Info(fileAttrs);
					OneDriveFile file = new OneDriveFile(pathName, fileInfo, authHelper);
					this.callback.onSuccess(file);
					future.complete(file);

					break;
				case GET_CHILDREN:
					DirChildrenResponse dirChildrenResponse = (DirChildrenResponse) response.body();

					List<DirChildrenResponse.ValueBean> list = dirChildrenResponse.getValue();
					ArrayList<ItemInfo> itemInfos = new ArrayList<>(list.size());
					for (DirChildrenResponse.ValueBean value : list){
						HashMap<String , String> childrenAttrs = new HashMap<>();
						childrenAttrs.put(ItemInfo.itemId, value.getId());
						childrenAttrs.put(ItemInfo.name, value.getName());

						boolean isFolder = value.getFolder() != null;
						childrenAttrs.put(ItemInfo.type, isFolder ? "directory": "file");
						if (isFolder) {
							childrenAttrs.put(ItemInfo.size, "0");
						}
						else {
							childrenAttrs.put(ItemInfo.size, Integer.toString(value.getSize()));
						}

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
			t.printStackTrace();

			HiveException e = new HiveException(t.getMessage());
			this.callback.onError(e);
			future.completeExceptionally(e);
		}
	}

	private enum Type{
		GET_INFO, MOVE_TO, COPY_TO, DELETE_ITEM, CREATE_DIR,
		GET_DIR, CREATE_FILE, GET_FILE, GET_CHILDREN
	}
}
