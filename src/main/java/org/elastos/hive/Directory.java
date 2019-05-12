package org.elastos.hive;

import java.util.concurrent.CompletableFuture;

public interface Directory extends HiveItem, FileItem<Directory>, DirectoryItem {
	public DirectoryInfo getLastInfo();

	public CompletableFuture<Result<DirectoryInfo>> getInfo();
	public CompletableFuture<Result<DirectoryInfo>> getInfo(Callback<DirectoryInfo> callback);
}
