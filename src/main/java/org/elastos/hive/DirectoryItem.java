package org.elastos.hive;

import java.util.concurrent.CompletableFuture;

public interface DirectoryItem {
	public CompletableFuture<Directory> createDirectory(String path);
	public CompletableFuture<Directory> createDirectory(String path, Callback<Directory> callback);

	public CompletableFuture<Directory> getDirectory(String path);
	public CompletableFuture<Directory> getDirectory(String path, Callback<Directory> callback);

	public CompletableFuture<File> createFile(String path);
	public CompletableFuture<File> createFile(String path, Callback<File> callback);

	public CompletableFuture<File> getFile(String path);
	public CompletableFuture<File> getFile(String path, Callback<File> callback);
}
