package org.elastos.hive;

import java.util.concurrent.CompletableFuture;

interface FileItem {
	String getPath();
	String getParentPath();

	CompletableFuture<Status> moveTo(String path);
	CompletableFuture<Status> moveTo(String path, Callback<Status> callback);

	CompletableFuture<Status> copyTo(String path);
	CompletableFuture<Status> copyTo(String path, Callback<Status> callback);

	CompletableFuture<Status> deleteItem();
	CompletableFuture<Status> deleteItem(Callback<Status> callback);

	void close();
}
