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
import org.elastos.hive.File;
import org.elastos.hive.HiveException;
import org.elastos.hive.Length;
import org.elastos.hive.NullCallback;
import org.elastos.hive.Void;
import org.elastos.hive.utils.CacheHelper;
import org.elastos.hive.utils.HeaderUtil;
import org.elastos.hive.vendors.connection.ConnectionManager;
import org.elastos.hive.vendors.onedrive.network.model.FileOrDirPropResponse;
import org.elastos.hive.vendors.onedrive.network.model.MoveAndCopyReqest;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.util.HashMap;
import java.util.concurrent.CompletableFuture;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Response;


final class OneDriveFile extends File {
	private final AuthHelper authHelper;
	private String pathName;
	private volatile File.Info fileInfo;
	private boolean needDeleteCache;

	OneDriveFile(String pathName, File.Info fileInfo, AuthHelper authHelper) {
		this.fileInfo = fileInfo;
		this.pathName = pathName;
		this.authHelper = authHelper;
		this.needDeleteCache = true;
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
				.thenCompose(padding -> getInfo(padding, callback));
	}

	private CompletableFuture<File.Info> getInfo(Void padding, Callback<File.Info> callback) {
		CompletableFuture<File.Info> future = new CompletableFuture<File.Info>();

		if (callback == null)
			callback = new NullCallback<File.Info>();

		try {
			ConnectionManager.getOnedriveApi()
					.getDirAndFileInfo(pathName)
					.enqueue(new FileCallback(future , callback ,pathName, Type.GET_INFO));
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

		if (!parentPath.startsWith("/")) {
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

		if (this.pathName.equals(parentPath)) {
			HiveException e = new HiveException("Can't move to same path name");
			callback.onError(e);
			future.completeExceptionally(e);
			return future;
		}

		try {

			int LastPos = this.pathName.lastIndexOf("/");
			String name = this.pathName.substring(LastPos + 1);

			String newPathName = parentPath + "/" +name ;

			MoveAndCopyReqest request = new MoveAndCopyReqest(parentPath, name);
			ConnectionManager.getOnedriveApi()
					.moveTo(this.pathName, request)
					.enqueue(new FileCallback(future , callback ,newPathName, Type.MOVE_TO));
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
				.thenCompose(padding -> copyTo(padding, pathName, callback));
	}

	private CompletableFuture<Void> copyTo(Void padding, String parentPath, Callback<Void> callback) {
		CompletableFuture<Void> future = new CompletableFuture<Void>();

		if (callback == null)
			callback = new NullCallback<Void>();

		if (!parentPath.startsWith("/")) {
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

		if (this.pathName.equals(parentPath)) {
			HiveException e = new HiveException("Can't copy to same path name");
			callback.onError(e);
			future.completeExceptionally(e);
			return future;
		}

		try {
			int LastPos = this.pathName.lastIndexOf("/");
			String name = this.pathName.substring(LastPos + 1);

			MoveAndCopyReqest request = new MoveAndCopyReqest(parentPath, name);
			ConnectionManager.getOnedriveApi()
					.copyTo(this.pathName, request)
					.enqueue(new FileCallback(future, callback ,pathName, Type.COPY_TO));
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
				.thenCompose(padding -> deleteItem(padding, callback));
	}

	private CompletableFuture<Void> deleteItem(Void padding, Callback<Void> callback) {
		CompletableFuture<Void> future = new CompletableFuture<Void>();

		if (callback == null)
			callback = new NullCallback<Void>();

		try {
			ConnectionManager.getOnedriveApi()
					.deleteItem(this.pathName)
					.enqueue(new FileCallback(future , callback ,pathName, Type.DELETE_ITEM));
		} catch (Exception ex) {
			HiveException e = new HiveException(ex.getMessage());
			callback.onError(e);
			future.completeExceptionally(e);
		}

		return future;
	}

	private long readCursor = 0;
	@Override
	public CompletableFuture<Length> read(ByteBuffer dest) {
		return read(dest, new NullCallback<Length>());
	}

	@Override
	public CompletableFuture<Length> read(ByteBuffer dest, Callback<Length> callback) {
		return authHelper.checkExpired()
				.thenCompose(padding -> checkAndCache(dest))
				.thenCompose(length -> read(length, dest, -1, callback));
	}

	@Override
	public CompletableFuture<Length> read(ByteBuffer dest, long position) {
		return read(dest, position, new NullCallback<Length>());
	}

	@Override
	public CompletableFuture<Length> read(ByteBuffer dest, long position, Callback<Length> callback) {
		if (position < 0) {
			CompletableFuture<Length> future = new CompletableFuture<Length>();
			HiveException e = new HiveException("the position must be non-negative");
			callback.onError(e);
			future.completeExceptionally(e);
			return future;
		}

		return authHelper.checkExpired()
				.thenCompose(padding -> checkAndCache(dest))
				.thenCompose(length -> read(length, dest, position, callback));
	}

	private CompletableFuture<Length> checkAndCache(ByteBuffer dest) {
		CompletableFuture<Length> future = new CompletableFuture<Length>();
		if (dest == null || dest.capacity() <= 0) {
			future.completeExceptionally(new HiveException("the dest buffer is invalid"));
			return future;
		}

		//at the first read, if the cache file exists, delete it, and get a new one from the remote.
		java.io.File cacheFile = CacheHelper.getCacheFile(pathName);
		if (needDeleteCache) {
			if (cacheFile.exists()) {
				cacheFile.delete();
			}

			needDeleteCache = false;
		}

		if (!cacheFile.exists() || cacheFile.length() <= 0) {
			//get the file from the remote.
			try {
				ConnectionManager.getOnedriveApi()
						.read("identity",pathName)
						.enqueue(new FileCallback(future , null, CacheHelper.getCacheFileName(pathName), Type.READ));
			} catch (Exception ex) {
				HiveException e = new HiveException(ex.getMessage());
				future.completeExceptionally(e);
			}

			return future;
		}

		Length length = new Length(cacheFile.length());
		future.complete(length);
		return future;
	}

	private CompletableFuture<Length> read(Length length, ByteBuffer dest, long position, Callback<Length> callback) {
		CompletableFuture<Length> future = new CompletableFuture<Length>();
		if (length.getLength() == 0) {
			Length zero = new Length(0);
			callback.onSuccess(zero);
			future.complete(zero);
			return future;
		}

		if (length.getLength() < 0) {
			HiveException e = new HiveException("the file length must be non-negative");
			callback.onError(e);
			future.completeExceptionally(e);
			return future;
		}

		//1. clear the buffer, and read
		dest.clear();
		FileInputStream fileInputStream = null;
		FileChannel inChannel = null;
		try {
			java.io.File cacheFile = CacheHelper.getCacheFile(pathName);
			if (!cacheFile.exists()) {
				cacheFile.createNewFile();
			}
			fileInputStream = new FileInputStream(cacheFile);
			inChannel = fileInputStream.getChannel();
			long len = 0;
			//2. read by inner readCursor or position
			if (position < 0) {
				//read, non-position
				len = inChannel.read(dest, readCursor);
				if (len == -1) {
					//reset the readCursor
					readCursor = 0;
				}
				else {
					//change the readCursor
					readCursor += len;
				}
			}
			else {
				//read, by-position
				len = inChannel.read(dest, position);
			}

			Length readLen = new Length(len);
			callback.onSuccess(readLen);
			future.complete(readLen);
		}
		catch (Exception ex) {
			HiveException e = new HiveException(ex.getMessage());
			callback.onError(e);
			future.completeExceptionally(e);
		}
		finally {
			try {
				if (inChannel != null) {
					inChannel.close();
				}

				if (fileInputStream != null) {
					fileInputStream.close();
				}
			} catch (Exception ex) {
				HiveException e = new HiveException(ex.getMessage());
				callback.onError(e);
				future.completeExceptionally(e);
			}
		}

		return future;
	}

	@Override
	public CompletableFuture<Length> write(ByteBuffer dest) {
		return write(dest, new NullCallback<Length>());
	}

	@Override
	public CompletableFuture<Length> write(ByteBuffer dest, Callback<Length> callback) {
		return authHelper.checkExpired()
				.thenCompose(padding -> checkAndCache(dest))
				.thenCompose(length -> localWrite(dest, -1, callback));
	}

	@Override
	public CompletableFuture<Length> write(ByteBuffer dest, long position) {
		return write(dest, position, new NullCallback<Length>());
	}

	@Override
	public CompletableFuture<Length> write(ByteBuffer dest, long position, Callback<Length> callback) {
		if (position < 0) {
			CompletableFuture<Length> future = new CompletableFuture<Length>();
			HiveException e = new HiveException("the position must be non-negative");
			callback.onError(e);
			future.completeExceptionally(e);
			return future;
		}

		return authHelper.checkExpired()
				.thenCompose(padding -> checkAndCache(dest))
				.thenCompose(length -> localWrite(dest, position, callback));
	}

	private long writeCursor = 0;
	private CompletableFuture<Length> localWrite(ByteBuffer dest, long position, Callback<Length> callback) {
		CompletableFuture<Length> future = CompletableFuture.supplyAsync(() -> {
			if (dest == null || dest.capacity() <= 0) {
				callback.onError(new HiveException("the dest buffer is invalid"));
				return new Length(0);
			}

			//using writeCursor to write
			FileChannel outputChannel = null;
			FileOutputStream outputStream = null;
			long len = 0;
			try {
				java.io.File cacheFile = new java.io.File(CacheHelper.getCacheFileName(this.pathName));
				if (!cacheFile.exists()) {
					cacheFile.createNewFile();						
				}

				outputStream = new FileOutputStream(cacheFile, true);
				outputChannel = outputStream.getChannel();

				if (position == -1) {
					len = outputChannel.write(dest, writeCursor);
					writeCursor += len;
				}
				else {
					len = outputChannel.write(dest, position);
				}
			}
			catch (Exception e) {
				e.printStackTrace();
				callback.onError(new HiveException("write the buffer to cache file failed."));
				return new Length(0);
			}
			finally {
				try {
					if (outputChannel != null) {
						outputChannel.close();
					}

					if (outputStream != null) {
						outputStream.close();
					}
				}
				catch (Exception e) {
					e.printStackTrace();
					callback.onError(new HiveException(e.getMessage()));
					return new Length(0);
				}
			}

			Length writeLen = new Length(len);
			callback.onSuccess(writeLen);
			return writeLen;
		});

		return future;
	}

	@Override
	public CompletableFuture<Void> commit() {
		return commit(new NullCallback<Void>());
	}

	@Override
	public CompletableFuture<Void> commit(Callback<Void> callback) {
		return authHelper.checkExpired()
				.thenCompose(padding -> commit(padding, callback));
	}

	private CompletableFuture<Void> commit(Void padding, Callback<Void> callback) {
		CompletableFuture<Void> future = new CompletableFuture<Void>();

		java.io.File cacheFile = new java.io.File(CacheHelper.getCacheFileName(this.pathName));
		if (cacheFile.length() <= 0) {
			HiveException e = new HiveException("the file to upload is invalid");
			callback.onError(e);
			future.completeExceptionally(e);
			return future;
		}

		final long limitSize = 4 * 1024 * 1024; //4M
		if (cacheFile.length() > limitSize) {
			HiveException e = new HiveException("the file size is too large");
			callback.onError(e);
			future.completeExceptionally(e);
			return future;
		}

		try {
			RequestBody requestBody = RequestBody.create(MediaType.parse("multipart/form-data"), cacheFile);
			ConnectionManager.getOnedriveApi()
					.write(pathName, requestBody)
					.enqueue(new FileCallback(future , callback , pathName, Type.WRITE));
		} catch (Exception ex) {
			HiveException e = new HiveException(ex.getMessage());
			callback.onError(e);
			future.completeExceptionally(e);
		}

		return future;
	}

	@Override
	public void discard() {
		needDeleteCache = true;
		writeCursor = 0;
		CacheHelper.deleteCache(this.pathName);
	}

	private class FileCallback implements retrofit2.Callback{
		private final String pathName;
		private final CompletableFuture future;
		private final Callback callback;
		private final Type type ;

		FileCallback(CompletableFuture future , Callback callback ,String pathName , Type type) {
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
				case GET_INFO:
					FileOrDirPropResponse fileInfoResponse = (FileOrDirPropResponse) response.body();
					HashMap<String, String> attrs = new HashMap<>();
					attrs.put(Info.itemId, fileInfoResponse.getId());
					attrs.put(Info.name, fileInfoResponse.getName());
					attrs.put(Info.size, Integer.toString(fileInfoResponse.getSize()));

					File.Info info = new File.Info(attrs);
					fileInfo = info;
					this.callback.onSuccess(info);
					future.complete(info);
					break;

				case MOVE_TO:
					OneDriveFile.this.pathName = pathName;
				case COPY_TO:
				case DELETE_ITEM: {
					Void padding = new Void();
					this.callback.onSuccess(padding);
					future.complete(padding);
					break;
				}
				case READ: {
					ResponseBody body = (ResponseBody) response.body();
					Length lengthObj ;
					if (body == null) {
						lengthObj = new Length(0);
						future.complete(lengthObj);
						return;
					}

					if (HeaderUtil.getContentLength(response) == -1
							&& !HeaderUtil.isTrunced(response)){
						lengthObj = new Length(0);
						future.complete(lengthObj);
						break;
					}

					FileOutputStream cacheStream = null;
					long total = 0;
					try {
						//write the data to the cache file.
						InputStream data = body.byteStream();

						cacheStream = new FileOutputStream(pathName);
						byte[] b = new byte[1024];
						int length = 0;

						while((length = data.read(b)) > 0){
							cacheStream.write(b, 0, length);
							total += length;
						}

						data.close();
					} catch (Exception e) {
						future.completeExceptionally(new HiveException(e.getMessage()));
						return;
					}
					finally {
						try {
							if (cacheStream != null) {
								cacheStream.close();
							}
						} catch (Exception e) {
							future.completeExceptionally(new HiveException(e.getMessage()));
							return;
						}
					}

					lengthObj = new Length(total);
					future.complete(lengthObj);
					break;
				}
				case WRITE: {
					Void padding = new Void();
					this.callback.onSuccess(padding);
					future.complete(padding);
					needDeleteCache = true;
					CacheHelper.deleteCache(OneDriveFile.this.pathName);
					break;
				}

				default:
					break;
			}
		}

		@Override
		public void onFailure(Call call, Throwable t) {
			t.printStackTrace();

			HiveException e = new HiveException(t.getMessage());
			if (this.callback != null) {
				this.callback.onError(e);
			}

			future.completeExceptionally(e);
		}
	}

	private enum Type{
		GET_INFO , COPY_TO , MOVE_TO , DELETE_ITEM, READ, WRITE
	}
}
