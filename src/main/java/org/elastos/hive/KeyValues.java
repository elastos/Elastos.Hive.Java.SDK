package org.elastos.hive;

import java.util.ArrayList;
import java.util.concurrent.CompletableFuture;

public interface KeyValues {
	CompletableFuture<Void> putValue(String key, String value);

	CompletableFuture<Void> putValue(String key, byte[] value);

	CompletableFuture<Void> setValue(String key, String value);

	CompletableFuture<Void> setValue(String key, byte[] value);

	CompletableFuture<ArrayList<byte[]>> getValues(String key);

	CompletableFuture<Void> deleteKey(String key);
}
