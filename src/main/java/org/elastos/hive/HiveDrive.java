package org.elastos.hive;

import java.util.concurrent.CompletableFuture;

public interface HiveDrive extends HiveItem, DirectoryItem {
	public DriveType getType();

	public CompletableFuture<HiveResult<HiveDirectory>> getRootDir();
	public CompletableFuture<HiveResult<HiveDirectory>> getRootDir(HiveCallback<HiveDirectory, HiveException> callback);

	public CompletableFuture<HiveResult<DriveInfo>> getInfo();
	public CompletableFuture<HiveResult<DriveInfo>> getInfo(HiveCallback<DriveInfo, HiveException> callback);
}