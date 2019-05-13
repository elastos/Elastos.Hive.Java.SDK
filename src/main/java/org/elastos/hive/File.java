package org.elastos.hive;

import java.util.concurrent.CompletableFuture;

public interface File extends BaseItem, FileItem<File> {
	public FileInfo getLastInfo();

	public CompletableFuture<Result<FileInfo>> getInfo();
	public CompletableFuture<Result<FileInfo>> getInfo(Callback<FileInfo> callback);
}
