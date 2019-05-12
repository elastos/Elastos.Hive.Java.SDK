package org.elastos.hive;

import java.util.concurrent.CompletableFuture;

public interface DirectoryItem {
	public CompletableFuture<HiveResult<HiveDirectory>> createDirectory(String pathName);
	public CompletableFuture<HiveResult<HiveDirectory>> createDirectory(String pathName, HiveCallback<HiveDirectory, HiveException> callback);

	public CompletableFuture<HiveResult<HiveDirectory>> getDirectory(String pathName);
	public CompletableFuture<HiveResult<HiveDirectory>> getDirectory(String pathName, HiveCallback<HiveDirectory, HiveException> callback);

	public CompletableFuture<HiveResult<HiveFile>> createFile(String pathName);
	public CompletableFuture<HiveResult<HiveFile>> createFile(String pathName, HiveCallback<HiveFile, HiveException> callback);

	public CompletableFuture<HiveResult<HiveFile>> getFile(String pathName);
	public CompletableFuture<HiveResult<HiveFile>> getFile(String pathName, HiveCallback<HiveFile, HiveException> callback);
}
