package org.elastos.hive;

import java.util.concurrent.CompletableFuture;

interface FileItem {
	String getPath();
	String getParentPath();

	CompletableFuture<Void> moveTo(String path);
	CompletableFuture<Void> moveTo(String path, Callback<Void> callback);

	CompletableFuture<Void> copyTo(String path);
	CompletableFuture<Void> copyTo(String path, Callback<Void> callback);

	CompletableFuture<Void> deleteItem();
	CompletableFuture<Void> deleteItem(Callback<Void> callback);

	void close();
}
