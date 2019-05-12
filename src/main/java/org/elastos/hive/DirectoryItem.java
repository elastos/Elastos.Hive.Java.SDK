package org.elastos.hive;

import java.util.concurrent.CompletableFuture;

public interface DirectoryItem {
	public CompletableFuture<Result<Directory>> createDirectory(String pathName);
	public CompletableFuture<Result<Directory>> createDirectory(String pathName, Callback<Directory> callback);

	public CompletableFuture<Result<Directory>> getDirectory(String pathName);
	public CompletableFuture<Result<Directory>> getDirectory(String pathName, Callback<Directory> callback);

	public CompletableFuture<Result<File>> createFile(String pathName);
	public CompletableFuture<Result<File>> createFile(String pathName, Callback<File> callback);

	public CompletableFuture<Result<File>> getFile(String pathName);
	public CompletableFuture<Result<File>> getFile(String pathName, Callback<File> callback);
}
