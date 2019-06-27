package org.elastos.hive;

import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.concurrent.CompletableFuture;

public abstract class File extends Result implements ResourceItem<File.Info>, FileItem {
	public static class Info extends AttributeMap {
		public static final String itemId = "ItemId";
		public Info(HashMap<String, String> hash) {
			super(hash);
		}
	}

	public abstract CompletableFuture<Length> read(ByteBuffer dest);
	public abstract CompletableFuture<Length> read(ByteBuffer dest, Callback<Length> callback);

	public abstract CompletableFuture<Length> read(ByteBuffer dest, long position);
	public abstract CompletableFuture<Length> read(ByteBuffer dest, long position, Callback<Length> callback);

	public abstract CompletableFuture<Length> write(ByteBuffer dest);
	public abstract CompletableFuture<Length> write(ByteBuffer dest, Callback<Length> callback);

	public abstract CompletableFuture<Length> write(ByteBuffer dest, long position);
	public abstract CompletableFuture<Length> write(ByteBuffer dest, long position, Callback<Length> callback);

	public abstract CompletableFuture<Void> commit();
	public abstract CompletableFuture<Void> commit(Callback<Void> callback);

	public abstract void discard();
}
