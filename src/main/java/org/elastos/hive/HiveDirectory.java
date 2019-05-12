package org.elastos.hive;

import java.util.concurrent.CompletableFuture;

public interface HiveDirectory extends HiveItem, FileItem<HiveDirectory>, DirectoryItem {
	public CompletableFuture<HiveResult<DirectoryInfo>> getInfo();
	public CompletableFuture<HiveResult<DirectoryInfo>> getInfo(HiveCallback<DirectoryInfo, HiveException> callback);
}
