package org.elastos.hive;

import java.util.concurrent.CompletableFuture;

public interface Drive extends ResourceItem<DriveInfo>, DirectoryItem {
	public DriveType getType();

	public CompletableFuture<Directory> getRootDir();
	public CompletableFuture<Directory> getRootDir(Callback<Directory> callback);
}
