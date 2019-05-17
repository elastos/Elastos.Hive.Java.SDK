package org.elastos.hive;

import java.util.concurrent.CompletableFuture;

public interface Directory extends ResourceItem<DirectoryInfo>, DirectoryItem,  FileItem<Directory> {
	public CompletableFuture<Result<Children>> getChildren();
	public CompletableFuture<Result<Children>> getChildren(Callback<Children> callback);
}
