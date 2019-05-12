package org.elastos.hive;

import java.util.concurrent.CompletableFuture;

interface FileItem <T extends FileItem<?>>{
	public String getPath();
	public String getParentPath();

	public CompletableFuture<HiveResult<T>> moveTo(String pathName);
	public CompletableFuture<HiveResult<T>> moveTo(String pathName, HiveCallback<T, HiveException> callback);

	public CompletableFuture<HiveResult<T>> copyTo(String pathName);
	public CompletableFuture<HiveResult<T>> copyTo(String pathName, HiveCallback<T, HiveException> callback);

	public CompletableFuture<HiveResult<Status>> deleteItem();
	public CompletableFuture<HiveResult<Status>> deleteItem(HiveCallback<Status, HiveException> callback);

	public void close();
}
