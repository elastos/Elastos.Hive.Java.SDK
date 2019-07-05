package org.elastos.hive.vendors.ipfs;

import org.elastos.hive.Callback;
import org.elastos.hive.File;
import org.elastos.hive.HiveException;
import org.elastos.hive.Length;
import org.elastos.hive.NullCallback;
import org.elastos.hive.Void;
import org.elastos.hive.vendors.ipfs.network.IPFSApi;
import org.elastos.hive.vendors.connection.BaseServiceUtil;
import org.elastos.hive.vendors.connection.Model.BaseServiceConfig;

import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.concurrent.CompletableFuture;

import retrofit2.Call;
import retrofit2.Response;

final class IPFSFile extends File {
	private String pathName;
	private volatile File.Info fileInfo;
	private IPFSRpcHelper rpcHelper;

	IPFSFile(String pathName, File.Info fileInfo, IPFSRpcHelper rpcHelper) {
		this.fileInfo = fileInfo;
		this.pathName = pathName;
		this.rpcHelper = rpcHelper;
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
			call.enqueue(new IPFSFileCallback(future,callback, IPFSConstance.Type.GET_INFO));
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
			call.enqueue(new IPFSFileForResultCallback(future,value,newPath, IPFSConstance.Type.MOVE_TO));
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
			call.enqueue(new IPFSFileForResultCallback(future,value,null, IPFSConstance.Type.COPY_TO));
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
			call.enqueue(new IPFSFileForResultCallback(future,value,null, IPFSConstance.Type.DELETE_ITEM));
		} catch (Exception e) {
			e.printStackTrace();
		}

		return future;
	}

	@Override
	public void close() {
		// TODO Auto-generated method stub

	}


	@Override
	public CompletableFuture<Length> read(ByteBuffer dest) {
		// TODO
		return null;
	}

	@Override
	public CompletableFuture<Length> read(ByteBuffer dest, Callback<Length> callback) {
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
	public CompletableFuture<Length> write(ByteBuffer dest, Callback<Length> callback) {
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
	public CompletableFuture<Void> commit(Callback<Void> callback) {
		// TODO
		return null;
	}

	@Override
	public void discard() {
		// TODO

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
					HashMap<String, String> attrs = new HashMap<>();
					attrs.put(File.Info.itemId, getId());
					// TODO:

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
