package org.elastos.hive;

import java.util.concurrent.CompletableFuture;

public interface Directory extends BaseItem, FileItem<Directory>, DirectoryItem {
	public DirectoryInfo getLastInfo();

	public CompletableFuture<Result<DirectoryInfo>> getInfo();
	public CompletableFuture<Result<DirectoryInfo>> getInfo(Callback<DirectoryInfo> callback);

	public CompletableFuture<Result<Children>> getChildren();
	public CompletableFuture<Result<Children>> getChildren(Callback<Children> callback);
}
