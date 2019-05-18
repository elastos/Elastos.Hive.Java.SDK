package org.elastos.hive;

import java.util.concurrent.CompletableFuture;

public interface Directory extends ResourceItem<DirectoryInfo>, DirectoryItem,  FileItem<Directory> {
	public CompletableFuture<Children> getChildren();
	public CompletableFuture<Children> getChildren(Callback<Children> callback);
}
