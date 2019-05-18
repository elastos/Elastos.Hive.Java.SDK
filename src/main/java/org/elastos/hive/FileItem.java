package org.elastos.hive;

import java.util.concurrent.CompletableFuture;

interface FileItem <R extends ResourceItem<?>>{
	public String getPath();
	public String getParentPath();

	public CompletableFuture<R> moveTo(String path);
	public CompletableFuture<R> moveTo(String path, Callback<R> callback);

	public CompletableFuture<R> copyTo(String path);
	public CompletableFuture<R> copyTo(String path, Callback<R> callback);

	public CompletableFuture<Status> deleteItem();
	public CompletableFuture<Status> deleteItem(Callback<Status> callback);

	public void close();
}
