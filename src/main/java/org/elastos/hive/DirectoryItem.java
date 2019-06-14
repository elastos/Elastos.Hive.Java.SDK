package org.elastos.hive;

import java.util.concurrent.CompletableFuture;

public interface DirectoryItem {
	CompletableFuture<Directory> createDirectory(String path);
	CompletableFuture<Directory> createDirectory(String path, Callback<Directory> callback);

	CompletableFuture<Directory> getDirectory(String path);
	CompletableFuture<Directory> getDirectory(String path, Callback<Directory> callback);

	CompletableFuture<File> createFile(String path);
	CompletableFuture<File> createFile(String path, Callback<File> callback);

	CompletableFuture<File> getFile(String path);
	CompletableFuture<File> getFile(String path, Callback<File> callback);
}
