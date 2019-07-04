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

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
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
				.thenCompose(padding -> getInfo(padding, callback));
	}

	private CompletableFuture<File.Info> getInfo(Void padding, Callback<File.Info> callback) {
		CompletableFuture<File.Info> future = new CompletableFuture<File.Info>();

		if (callback == null)
			callback = new NullCallback<File.Info>();

		try {
			BaseServiceConfig config = new BaseServiceConfig.Builder(authHelper.getToken()).build();
			Api api = BaseServiceUtil.createService(Api.class, Constance.ONE_DRIVE_API_BASE_URL, config);
			Call call = api.getDirAndFileInfo(pathName);
			call.enqueue(new FileCallback(future , callback ,pathName, Type.GET_INFO));
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

	private CompletableFuture<Void> moveTo(Void padding, String pathName, Callback<Void> callback) {
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

			MoveAndCopyReqest request = new MoveAndCopyReqest(pathName,name);
			BaseServiceConfig config  = new BaseServiceConfig.Builder(authHelper.getToken()).build();
			Api api = BaseServiceUtil.createService(Api.class, Constance.ONE_DRIVE_API_BASE_URL, config);
			Call call = api.moveTo(this.pathName, request);
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
				.thenCompose(padding -> copyTo(padding, pathName, callback));
	}

	private CompletableFuture<Void> copyTo(Void padding, String pathName, Callback<Void> callback) {
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

			MoveAndCopyReqest request = new MoveAndCopyReqest(pathName,name);
			BaseServiceConfig config  = new BaseServiceConfig.Builder(authHelper.getToken())
					.ignoreReturnBody(true)
					.build();
			Api api = BaseServiceUtil.createService(Api.class, Constance.ONE_DRIVE_API_BASE_URL, config);
			Call call = api.copyTo(this.pathName, request);
			call.enqueue(new FileCallback(future, callback ,pathName, Type.COPY_TO));

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
			BaseServiceConfig config = new BaseServiceConfig.Builder(authHelper.getToken())
					.ignoreReturnBody(true).build();
			Api api = BaseServiceUtil.createService(Api.class, Constance.ONE_DRIVE_API_BASE_URL, config);
			Call call = api.deleteItem(this.pathName);
			call.enqueue(new FileCallback(future , callback ,pathName, Type.DELETE_ITEM));
		} catch (Exception ex) {
			HiveException e = new HiveException(ex.getMessage());
			callback.onError(e);
			future.completeExceptionally(e);
		}

		return future;
	}

	@Override
	public void close() {
		// TODO
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
			future.completeExceptionally(e);
			callback.onError(e);
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

		if (!cacheFileIsExist(this.pathName)) {
			//get the file from the remote.
			try {
				BaseServiceConfig config = new BaseServiceConfig.Builder(authHelper.getToken())
						.useGsonConverter(false).build();
				Api api = BaseServiceUtil.createService(Api.class, Constance.ONE_DRIVE_API_BASE_URL, config);
				Call call = api.read(pathName);
				call.enqueue(new FileCallback(future , null, getCacheFileName(pathName), Type.READ));
			} catch (Exception ex) {
				HiveException e = new HiveException(ex.getMessage());
				future.completeExceptionally(e);
			}

			return future;
		}

		java.io.File file = getCacheFile(this.pathName);
		Length length = new Length(file.length());
		future.complete(length);
		return future;
	}

	private CompletableFuture<Length> read(Length length, ByteBuffer dest, long position, Callback<Length> callback) {
		CompletableFuture<Length> future = new CompletableFuture<Length>();
		if (length.getLength() <= 0) {
			future.complete(length);
			return future;
		}

		//1. clear the buffer, and read
		dest.clear();
		FileInputStream fileInputStream = null;
		FileChannel inChannel = null;
		try {
			java.io.File cacheFile = new java.io.File(getCacheFileName(pathName));
			if (!cacheFile.exists()) {
				cacheFile.createNewFile();
			}
			fileInputStream = new FileInputStream(getCacheFileName(pathName));
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

	@Override
	public CompletableFuture<Length> write(ByteBuffer dest) {
		return write(dest, new NullCallback<Length>());
	}

	@Override
	public CompletableFuture<Length> write(ByteBuffer dest, Callback<Length> callback) {
		return localWrite(dest, -1, callback);
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

		return localWrite(dest, position, callback);
	}

	private long writeCursor = 0;
	private CompletableFuture<Length> localWrite(ByteBuffer dest, long position, Callback<Length> callback) {
		CompletableFuture<Length> future = CompletableFuture.supplyAsync(() -> {
			if (dest == null || dest.capacity() <= 0) {
				callback.onError(new HiveException("the dest buffer is invalid"));
				return new Length(0);
			}

			checkAndBackup(this.pathName);
			//using writeCursor to write
			FileChannel outputChannel = null;
			FileOutputStream outputStream = null;
			long len = 0;
			try {
				java.io.File backupCacheFile = new java.io.File(getCacheFileName(this.pathName, backupPrefix));
				if (!backupCacheFile.exists()) {
					backupCacheFile.createNewFile();						
				}

				outputStream = new FileOutputStream(backupCacheFile, true);
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

			return new Length(len);
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

		java.io.File backupCacheFile = new java.io.File(getCacheFileName(this.pathName, backupPrefix));
		if (backupCacheFile.length() <= 0) {
			HiveException e = new HiveException("the file to upload is invalid");
			callback.onError(e);
			future.completeExceptionally(e);
			return future;
		}

		final long limitSize = 4 * 1024 * 1024; //4M
		if (backupCacheFile.length() > limitSize) {
			HiveException e = new HiveException("the file size is too large");
			callback.onError(e);
			future.completeExceptionally(e);
			return future;
		}

		try {
			BaseServiceConfig config = new BaseServiceConfig.Builder(authHelper.getToken()).build();
			Api api = BaseServiceUtil.createService(Api.class, Constance.ONE_DRIVE_API_BASE_URL, config);

			RequestBody requestBody = RequestBody.create(MediaType.parse("multipart/form-data"), backupCacheFile);

			Call call = api.write(pathName, requestBody);
			call.enqueue(new FileCallback(future , callback , pathName, Type.WRITE));
		} catch (Exception ex) {
			HiveException e = new HiveException(ex.getMessage());
			callback.onError(e);
			future.completeExceptionally(e);
		}

		return future;
	}

	@Override
	public void discard() {
		writeCursor = 0;
		deleteBackup();
	}

	private static final String backupPrefix = "tmp_";
	private void checkAndBackup(String path) {
		FileChannel inputChannel = null;
		FileChannel outputChannel = null;
		try {
			java.io.File backupCacheFile = new java.io.File(getCacheFileName(path, backupPrefix));
			if (backupCacheFile.exists()) {
				//if the backup file exists, return.
				return;
			}

			//create a new backup file.
			backupCacheFile.createNewFile();

			String fileName = getCacheFileName(path);
			java.io.File file = new java.io.File(fileName);
			if (file.exists()) {
				//backup the cache file, and delete the backup file if commit successfully
				inputChannel = new FileInputStream(file).getChannel();
				outputChannel = new FileOutputStream(backupCacheFile).getChannel();
				outputChannel.transferFrom(inputChannel, 0, inputChannel.size());
			}
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		finally {
			try {
				if (inputChannel != null) {
					inputChannel.close();
				}

				if (outputChannel != null) {
					outputChannel.close();
				}
			}
			catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	//if discard or commit successfully, delete the cache file.
	private void deleteBackup() {
		try {
			java.io.File backupCacheFile = new java.io.File(getCacheFileName(this.pathName, backupPrefix));
			if (backupCacheFile.exists()) {
				backupCacheFile.delete();
			}
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}

	private boolean cacheFileIsExist(String path) {
		java.io.File file = getCacheFile(path);
		return file != null && file.exists();
	}

	private java.io.File getCacheFile(String path) {
		java.io.File file = null;
		try {
			file = new java.io.File(getCacheFileName(path));
		} catch (Exception e) {
			e.printStackTrace();
		}

		return file;
	}

	private String getCacheFileName(String path) {
		return getCacheFileName(path, null);
	}
	
	private String getCacheFileName(String path, String prefix) {
		String cacheFileName = null;
		try {
			MessageDigest md;
			md = MessageDigest.getInstance("MD5");
			md.update(path.getBytes());
			String md5Name =  bytes2Hex(md.digest());
			String cachePath = CacheHelper.getCachePath();
			if (prefix != null) {
				cacheFileName = String.format("%s/%s%s", cachePath, prefix, md5Name);
			}
			else {
				cacheFileName = String.format("%s/%s", cachePath, md5Name);				
			}
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}

		return cacheFileName;
	}
	
	private String bytes2Hex(byte[] content) {
    	final char[] HEX = "0123456789abcdef".toCharArray();
        char[] chs = new char[content.length * 2];
        for(int i = 0, offset = 0; i < content.length; i++) {
            chs[offset++] = HEX[content[i] >> 4 & 0xf];
            chs[offset++] = HEX[content[i] & 0xf];
        }
        return new String(chs);
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
					DirOrFileInfoResponse dirInfoResponse = (DirOrFileInfoResponse) response.body();
					HashMap<String, String> attrs = new HashMap<>();
					attrs.put(Directory.Info.itemId, dirInfoResponse.getId());
					// TODO:

					File.Info info = new File.Info(attrs);
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
					if (body.contentLength() <= 0) {
						future.complete(new Length(body.contentLength()));
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

					Length lengthObj = new Length(total);
					future.complete(lengthObj);
					break;
				}
				case WRITE: {
					Void padding = new Void();
					this.callback.onSuccess(padding);
					future.complete(padding);
					deleteBackup();
					break;
				}

				default:
					break;

			}
		}

		@Override
		public void onFailure(Call call, Throwable t) {
			LogUtil.d("t = "+t.getMessage());
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
