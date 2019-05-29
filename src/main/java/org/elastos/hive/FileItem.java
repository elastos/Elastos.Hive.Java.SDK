package org.elastos.hive;

import java.util.concurrent.CompletableFuture;

interface FileItem {
	public String getPath();
	public String getParentPath();

	public CompletableFuture<Status> moveTo(String path);
	public CompletableFuture<Status> moveTo(String path, Callback<Status> callback);

	public CompletableFuture<Status> copyTo(String path);
	public CompletableFuture<Status> copyTo(String path, Callback<Status> callback);

	public CompletableFuture<Status> deleteItem();
	public CompletableFuture<Status> deleteItem(Callback<Status> callback);

	public void close();
}
