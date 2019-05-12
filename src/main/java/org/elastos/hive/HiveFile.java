package org.elastos.hive;

import java.util.concurrent.CompletableFuture;

public interface HiveFile extends HiveItem, FileItem<HiveFile> {
	public CompletableFuture<HiveResult<FileInfo>> getInfo();
	public CompletableFuture<HiveResult<FileInfo>> getInfo(HiveCallback<FileInfo, HiveException> callback);
}
