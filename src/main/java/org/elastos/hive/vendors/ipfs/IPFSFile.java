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

package org.elastos.hive.vendors.ipfs;

import org.elastos.hive.Callback;
import org.elastos.hive.File;
import org.elastos.hive.HiveException;
import org.elastos.hive.Length;
import org.elastos.hive.NullCallback;
import org.elastos.hive.Void;
import org.elastos.hive.utils.CacheHelper;
import org.elastos.hive.vendors.ipfs.network.IPFSApi;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;

import org.elastos.hive.vendors.connection.BaseServiceUtil;
import org.elastos.hive.vendors.connection.Model.BaseServiceConfig;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.util.HashMap;
import java.util.concurrent.CompletableFuture;

import org.elastos.hive.vendors.ipfs.network.model.StatResponse;
import retrofit2.Call;
import retrofit2.Response;

final class IPFSFile extends File {
	private String pathName;
	private volatile File.Info fileInfo;
	private IPFSRpcHelper rpcHelper;
	private boolean needDeleteCache;

	IPFSFile(String pathName, File.Info fileInfo, IPFSRpcHelper rpcHelper) {
		this.fileInfo = fileInfo;
		this.pathName = pathName;
		this.rpcHelper = rpcHelper;
		this.needDeleteCache = true;
	}

	@Override
	public String getId() {
		return fileInfo.get(File.Info.itemId);
	}

	@Override
	public Info getLastInfo() {
		return fileInfo;
	}

	@Override
	public CompletableFuture<Info> getInfo() {
		return getInfo(new NullCallback<File.Info>());
	}

	@Override
	public CompletableFuture<Info> getInfo(Callback<Info> callback) {
		return rpcHelper.checkExpiredNew()
				.thenCompose(value -> getInfo(value, callback));
	}

	private CompletableFuture<Info> getInfo(PackValue value, Callback<Info> callback) {
		CompletableFuture<File.Info> future = new CompletableFuture<File.Info>();

		if (callback == null)
			callback = new NullCallback<Info>();

		if (value.getException() != null) {
			callback.onError(value.getException());
			future.completeExceptionally(value.getException());
			return future;
		}

		try {
			BaseServiceConfig config = new BaseServiceConfig.Builder().ignoreReturnBody(true).build();
			IPFSApi ipfsApi = BaseServiceUtil.createService(IPFSApi.class, rpcHelper.getBaseUrl(), config);
			Call call = ipfsApi.getStat(getId(), pathName);
			call.enqueue(new IPFSFileCallback(future, callback, IPFSConstance.Type.GET_INFO));
		} catch (Exception e) {
			e.printStackTrace();
		}
		return future;
	}

	@Override
	public String getPath() {
		return pathName;
	}

	@Override
	public String getParentPath() {
		if (pathName.equals("/"))
			return pathName;

		return pathName.substring(0, pathName.lastIndexOf("/") + 1);
	}

	@Override
	public CompletableFuture<Void> moveTo(String path) {
		return moveTo(path, new NullCallback<Void>());
	}

	@Override
	public CompletableFuture<Void> moveTo(String path, Callback<Void> callback) {
		return rpcHelper.checkExpiredNew()
				.thenCompose(value -> moveTo(value, path, callback))
				.thenCompose(value -> rpcHelper.getRootHash(value))
				.thenCompose(value -> rpcHelper.publishHash(value))
				.thenCompose(value -> rpcHelper.invokeVoidCallback(value));
	}

	private CompletableFuture<PackValue> moveTo(PackValue value, String path, Callback<Void> callback) {
		CompletableFuture<PackValue> future = new CompletableFuture<PackValue>();

		if (callback == null)
			callback = new NullCallback<Void>();

		value.setCallback(callback);

		if (value.getException() != null) {
			future.completeExceptionally(value.getException());
			return future;
		}

		if (path == null || path.isEmpty()) {
			HiveException e = new HiveException("The path is invalid");
			value.setException(e);
			future.completeExceptionally(e);
			return future;
		}

		if (!path.startsWith("/")) {
			HiveException e = new HiveException("Path name must be a abosulte path");
			value.setException(e);
			future.completeExceptionally(e);
			return future;
		}

		if (path.equals(this.pathName)) {
			HiveException e = new HiveException("Can't move to the oneself");
			value.setException(e);
			future.completeExceptionally(e);
			return future;
		}

		int LastPos = this.pathName.lastIndexOf("/");
		String name = this.pathName.substring(LastPos + 1);

		final String newPath = String.format("%s/%s", path, name);

		try {
			BaseServiceConfig config = new BaseServiceConfig.Builder().ignoreReturnBody(true).build();
			IPFSApi ipfsApi = BaseServiceUtil.createService(IPFSApi.class, rpcHelper.getBaseUrl(), config);
			Call call = ipfsApi.moveTo(rpcHelper.getIpfsEntry().getUid(), pathName, newPath);
			call.enqueue(new IPFSFileForResultCallback(future, value, newPath, IPFSConstance.Type.MOVE_TO));
		} catch (Exception e) {
			e.printStackTrace();
		}

		return future;
	}

	@Override
	public CompletableFuture<Void> copyTo(String path) {
		return copyTo(path, new NullCallback<Void>());
	}

	@Override
	public CompletableFuture<Void> copyTo(String path, Callback<Void> callback) {
		return rpcHelper.checkExpiredNew()
				.thenCompose(value -> rpcHelper.getPathHash(value, this.pathName))
				.thenCompose(value -> copyTo(value, path, callback))
				.thenCompose(value -> rpcHelper.getRootHash(value))
				.thenCompose(value -> rpcHelper.publishHash(value))
				.thenCompose(value -> rpcHelper.invokeVoidCallback(value));
	}

	private CompletableFuture<PackValue> copyTo(PackValue value, String path, Callback<Void> callback) {
		CompletableFuture<PackValue> future = new CompletableFuture<PackValue>();

		if (callback == null)
			callback = new NullCallback<Void>();

		value.setCallback(callback);

		if (value.getException() != null) {
			future.completeExceptionally(value.getException());
			return future;
		}

		if (path == null || path.isEmpty()) {
			HiveException e = new HiveException("The path is invalid");
			value.setException(e);
			future.completeExceptionally(e);
			return future;
		}

		if (!path.startsWith("/")) {
			HiveException e = new HiveException("Path name must be a abosulte path");
			value.setException(e);
			future.completeExceptionally(e);
			return future;
		}

		if (path.equals(this.pathName)) {
			HiveException e = new HiveException("Can't copy to the oneself directory");
			value.setException(e);
			future.completeExceptionally(e);
			return future;
		}

		String hash = value.getHash().getValue();
		if (hash == null || hash.isEmpty()) {
			HiveException e = new HiveException("The hash is invalid");
			future.completeExceptionally(e);
			return future;
		}

		int LastPos = this.pathName.lastIndexOf("/");
		String name = this.pathName.substring(LastPos + 1);
		final String newPath = String.format("%s/%s", path, name);

		try {
			BaseServiceConfig config = new BaseServiceConfig.Builder().ignoreReturnBody(true).build();
			IPFSApi ipfsApi = BaseServiceUtil.createService(IPFSApi.class, rpcHelper.getBaseUrl(), config);
			Call call = ipfsApi.copyTo(rpcHelper.getIpfsEntry().getUid(), IPFSConstance.PREFIX + hash, newPath);
			call.enqueue(new IPFSFileForResultCallback(future, value, null, IPFSConstance.Type.COPY_TO));
		} catch (Exception e) {
			e.printStackTrace();
		}

		return future;
	}

	@Override
	public CompletableFuture<Void> deleteItem() {
		return deleteItem(new NullCallback<Void>());
	}

	@Override
	public CompletableFuture<Void> deleteItem(Callback<Void> callback) {
		return rpcHelper.checkExpiredNew()
				.thenCompose(value -> deleteItem(value, callback))
				.thenCompose(value -> rpcHelper.getRootHash(value))
				.thenCompose(value -> rpcHelper.publishHash(value))
				.thenCompose(value -> rpcHelper.invokeVoidCallback(value));
	}

	private CompletableFuture<PackValue> deleteItem(PackValue value, Callback<Void> callback) {
		CompletableFuture<PackValue> future = new CompletableFuture<PackValue>();

		if (callback == null)
			callback = new NullCallback<Void>();

		value.setCallback(callback);

		if (value.getException() != null) {
			future.completeExceptionally(value.getException());
			return future;
		}

		if (pathName.equals("/")) {
			HiveException e = new HiveException("Can't delete the root.");
			value.setException(e);
			future.completeExceptionally(e);
			return future;
		}

		try {
			BaseServiceConfig config = new BaseServiceConfig.Builder().ignoreReturnBody(true).build();
			IPFSApi ipfsApi = BaseServiceUtil.createService(IPFSApi.class, rpcHelper.getBaseUrl(), config);
			Call call = ipfsApi.deleteItem(rpcHelper.getIpfsEntry().getUid(), pathName, "true");
			call.enqueue(new IPFSFileForResultCallback(future, value, null, IPFSConstance.Type.DELETE_ITEM));
		} catch (Exception e) {
			e.printStackTrace();
		}

		return future;
	}

	@Override
	public CompletableFuture<Length> read(ByteBuffer dest) {
		return read(dest, new NullCallback<Length>());
	}

	@Override
	public CompletableFuture<Length> read(ByteBuffer dest, Callback<Length> callback) {
		return rpcHelper.checkExpiredNew()
				.thenCompose(value -> checkAndCache(value, dest))
				.thenCompose(value -> read(value, dest, -1, callback));
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
			future.completeExceptionally(e);
			callback.onError(e);
			return future;
		}

		return rpcHelper.checkExpiredNew()
				.thenCompose(value -> checkAndCache(value, dest))
				.thenCompose(value -> read(value, dest, position, callback));
	}

	@Override
	public CompletableFuture<Length> write(ByteBuffer dest) {
		return write(dest, new NullCallback<Length>());
	}

	@Override
	public CompletableFuture<Length> write(ByteBuffer dest, Callback<Length> callback) {
		return rpcHelper.checkExpiredNew()
				.thenCompose(value -> checkAndCache(value, dest))
				.thenCompose(value -> localWrite(value, dest, -1, callback))
				.thenCompose(value -> rpcHelper.getRootHash(value))
				.thenCompose(value -> rpcHelper.publishHash(value))
				.thenCompose(value -> rpcHelper.invokeLengthCallback(value));
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
			future.completeExceptionally(e);
			callback.onError(e);
			return future;
		}

		return rpcHelper.checkExpiredNew()
				.thenCompose(value -> checkAndCache(value, dest))
				.thenCompose(value -> localWrite(value, dest, position, callback))
				.thenCompose(value -> rpcHelper.getRootHash(value))
				.thenCompose(value -> rpcHelper.publishHash(value))
				.thenCompose(value -> rpcHelper.invokeLengthCallback(value));
	}

	@Override
	public CompletableFuture<Void> commit() {
		return commit(new NullCallback<Void>());
	}

	@Override
	public CompletableFuture<Void> commit(Callback<Void> callback) {
		return rpcHelper.checkExpiredNew()
				.thenCompose(value -> commit(value, callback));
	}

	@Override
	public void discard() {
		writeCursor = 0;
		CacheHelper.deleteCache(this.pathName);
	}

	private CompletableFuture<PackValue> checkAndCache(PackValue value, ByteBuffer dest) {
		CompletableFuture<PackValue> future = new CompletableFuture<PackValue>();
		
		if (value.getException() != null) {
			future.completeExceptionally(value.getException());
			return future;
		}

		if (dest == null || dest.capacity() <= 0) {
			HiveException e = new HiveException("the dest buffer is invalid");
			future.completeExceptionally(e);
			value.setException(e);
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

		if (!cacheFile.exists()) {
			//get the file from the remote.
			try {
				BaseServiceConfig config = new BaseServiceConfig.Builder()
						.useGsonConverter(false)
						.build();

				IPFSApi ipfsApi = BaseServiceUtil.createService(IPFSApi.class, rpcHelper.getBaseUrl(), config);
				Call call = ipfsApi.read(rpcHelper.getIpfsEntry().getUid(), pathName);
				call.enqueue(new IPFSFileForResultCallback(future, value, 
						CacheHelper.getCacheFileName(pathName), IPFSConstance.Type.READ));
			} catch (Exception ex) {
				ex.printStackTrace();
				HiveException e = new HiveException(ex.getMessage());
				future.completeExceptionally(e);
			}

			return future;
		}

		java.io.File file = CacheHelper.getCacheFile(this.pathName);
		Length length = new Length(file.length());
		value.setValue(length);
		future.complete(value);
		return future;
	}
	
	private long readCursor = 0;
	private CompletableFuture<Length> read(PackValue value, ByteBuffer dest, long position, Callback<Length> callback) {
		CompletableFuture<Length> future = new CompletableFuture<Length>();
		if (callback == null)
			callback = new NullCallback<Length>();

		value.setCallback(callback);

		if (value.getException() != null) {
			future.completeExceptionally(value.getException());
			return future;
		}

		Length length = (Length) value.getValue();
		if (length.getLength() <= 0) {
			future.complete(length);
			return future;
		}

		//1. clear the buffer, and read
		dest.clear();
		FileInputStream fileInputStream = null;
		FileChannel inChannel = null;
		try {
			java.io.File cacheFile = new java.io.File(CacheHelper.getCacheFileName(pathName));
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

			future.complete(new Length(len));
		}
		catch (FileNotFoundException e) {
			future.completeExceptionally(new HiveException(e.getMessage()));
			return future;
		}
		catch (IOException e) {
			future.completeExceptionally(new HiveException(e.getMessage()));
			return future;
		}
		finally {
			try {
				if (inChannel != null) {
					inChannel.close();
				}

				if (fileInputStream != null) {
					fileInputStream.close();
				}
			} catch (Exception e) {
				future.completeExceptionally(new HiveException(e.getMessage()));
			}
		}

		return future;
	}
	
	private long writeCursor = 0;
	private CompletableFuture<PackValue> localWrite(PackValue value, ByteBuffer dest, long position, Callback<Length> callback) {
		if (callback == null)
			callback = new NullCallback<Length>();

		final Callback<Length> finalCallback = callback; 

		CompletableFuture<PackValue> future = CompletableFuture.supplyAsync(() -> {
			value.setCallback(finalCallback);

			if (value.getException() != null) {
				finalCallback.onError(value.getException());
				return value;
			}

			if (dest == null || dest.capacity() <= 0) {
				HiveException e = new HiveException("the dest buffer is invalid");
				finalCallback.onError(e);
				value.setException(e);
				return value;
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
				finalCallback.onError(new HiveException("write the buffer to cache file failed."));
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
				}
			}

			value.setValue(new Length(len));
			return value;
		});

		return future;
	}
	
	private CompletableFuture<Void> commit(PackValue value, Callback<Void> callback) {
		CompletableFuture<Void> future = new CompletableFuture<Void>();

		if (value.getException() != null) {
			callback.onError(value.getException());
			future.completeExceptionally(value.getException());
			return future;
		}

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
			BaseServiceConfig config = new BaseServiceConfig.Builder().ignoreReturnBody(true).build();

			IPFSApi ipfsApi = BaseServiceUtil.createService(IPFSApi.class, rpcHelper.getBaseUrl(), config);

			RequestBody fileBody = RequestBody.create(MediaType.parse("multipart/form-data"), cacheFile);
			RequestBody requestBody = new MultipartBody.Builder()
				.setType(MultipartBody.FORM)
				.addFormDataPart("file", this.pathName, fileBody)
				.build();
			
			Call call = ipfsApi.write(rpcHelper.getIpfsEntry().getUid(), pathName, true, requestBody);
			call.enqueue(new IPFSFileForResultCallback(future, value, null, IPFSConstance.Type.WRITE));
		} catch (Exception ex) {
			ex.printStackTrace();
			HiveException e = new HiveException(ex.getMessage());
			future.completeExceptionally(e);
		}
		
		return future;
	}
	
	private class IPFSFileForResultCallback implements retrofit2.Callback{
		private final String pathName ;
		private final CompletableFuture future;
		private final PackValue value;
		private final IPFSConstance.Type type ;

		IPFSFileForResultCallback(CompletableFuture future , PackValue value , String pathName , IPFSConstance.Type type){
			this.future = future ;
			this.value = value ;
			this.pathName = pathName ;
			this.type = type ;
		}

		@Override
		public void onResponse(Call call, Response response) {
			if (response.code() != 200) {
				HiveException e = new HiveException("Server Error: " + response.message());
				value.setException(e);
				future.completeExceptionally(e);
				return;
			}

			switch (type){
				case MOVE_TO:
					IPFSFile.this.pathName = pathName;
					break;
				case DELETE_ITEM:
				case COPY_TO:
					break;
				case READ: {
					ResponseBody body = (ResponseBody) response.body();
					Length lengthObj = new Length(0);
					if (body == null) {
						value.setValue(lengthObj);
						future.complete(value);
						return;
					}

					if (body.contentLength() <= 0) {
						lengthObj = new Length(body.contentLength());
						value.setValue(lengthObj);
						future.complete(value);
						return;
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
					value.setValue(lengthObj);
					future.complete(value);
					break;
				}
				case WRITE: {
					Void padding = new Void();
					if (value != null && value.getCallback() != null) {
						Callback<Void> callback = (Callback<Void>) value.getCallback();
						callback.onSuccess(padding);
					}

					future.complete(padding);
					CacheHelper.deleteCache(IPFSFile.this.pathName);
					break;
				}
			}

			Void padding = new Void();
			value.setValue(padding);
			future.complete(value);
		}

		@Override
		public void onFailure(Call call, Throwable t) {
			HiveException e = new HiveException(t.getMessage());
			value.setException(e);
			future.completeExceptionally(e);
		}
	}

	private class IPFSFileCallback implements retrofit2.Callback{
		private final CompletableFuture future;
		private final Callback callback;
		private final IPFSConstance.Type type;

		IPFSFileCallback(CompletableFuture future , Callback callback , IPFSConstance.Type type) {
			this.future = future;
			this.callback = callback;
			this.type = type;
		}

		@Override
		public void onResponse(Call call, Response response) {
			if (response.code() != 200) {
				HiveException ex = new HiveException("Server Error: " + response.message());
				if (callback != null) {
					this.callback.onError(ex);
				}

				if (future != null) {
					future.completeExceptionally(ex);
				}
				return;
			}

			switch (type) {
				case GET_INFO:
					StatResponse statResponse = (StatResponse) response.body();
					HashMap<String, String> attrs = new HashMap<>();
					attrs.put(Info.itemId, getId());

					int LastPos = IPFSFile.this.pathName.lastIndexOf("/");
					String name = IPFSFile.this.pathName.substring(LastPos + 1);
					attrs.put(Info.name, name);
					attrs.put(Info.size, Integer.toString(statResponse.getSize()));

					fileInfo = new File.Info(attrs);

					this.callback.onSuccess(fileInfo);
					future.complete(fileInfo);
					break;
			}
		}

		@Override
		public void onFailure(Call call, Throwable t) {
			HiveException e = new HiveException(t.getMessage());
			this.callback.onError(e);
			future.completeExceptionally(e);
		}
	}
}
