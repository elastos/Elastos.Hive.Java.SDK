package org.elastos.hive;

import java.util.concurrent.CompletableFuture;

public interface Drive extends BaseItem, DirectoryItem {
	public DriveType getType();

	public DriveInfo getLastInfo();

	public CompletableFuture<Result<DriveInfo>> getInfo();
	public CompletableFuture<Result<DriveInfo>> getInfo(Callback<DriveInfo> callback);

	public CompletableFuture<Result<Directory>> getRootDir();
	public CompletableFuture<Result<Directory>> getRootDir(Callback<Directory> callback);
}