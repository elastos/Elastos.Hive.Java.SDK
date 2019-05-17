package org.elastos.hive;

import java.util.concurrent.CompletableFuture;

public interface Drive extends ResourceItem<DriveInfo>, DirectoryItem {
	public DriveType getType();

	public CompletableFuture<Result<Directory>> getRootDir();
	public CompletableFuture<Result<Directory>> getRootDir(Callback<Directory> callback);
}
