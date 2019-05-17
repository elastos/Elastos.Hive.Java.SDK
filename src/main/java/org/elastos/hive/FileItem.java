package org.elastos.hive;

import java.util.concurrent.CompletableFuture;

interface FileItem <R extends ResourceItem<?>>{
	public String getPath();
	public String getParentPath();

	public CompletableFuture<Result<R>> moveTo(String pathName);
	public CompletableFuture<Result<R>> moveTo(String pathName, Callback<R> callback);

	public CompletableFuture<Result<R>> copyTo(String pathName);
	public CompletableFuture<Result<R>> copyTo(String pathName, Callback<R> callback);

	public CompletableFuture<Result<Status>> deleteItem();
	public CompletableFuture<Result<Status>> deleteItem(Callback<Status> callback);

	public void close();
}
