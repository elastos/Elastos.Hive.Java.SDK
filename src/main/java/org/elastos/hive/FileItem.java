package org.elastos.hive;

import java.util.concurrent.CompletableFuture;

interface FileItem <T extends HiveItem>{
	public String getPath();
	public String getParentPath();

	public CompletableFuture<Result<T>> moveTo(String pathName);
	public CompletableFuture<Result<T>> moveTo(String pathName, Callback<T> callback);

	public CompletableFuture<Result<T>> copyTo(String pathName);
	public CompletableFuture<Result<T>> copyTo(String pathName, Callback<T> callback);

	public CompletableFuture<Result<Status>> deleteItem();
	public CompletableFuture<Result<Status>> deleteItem(Callback<Status> callback);

	public void close();
}
