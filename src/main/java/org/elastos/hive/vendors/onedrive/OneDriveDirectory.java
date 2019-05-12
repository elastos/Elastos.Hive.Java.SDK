package org.elastos.hive.vendors.onedrive;

import java.util.concurrent.CompletableFuture;

import org.elastos.hive.AuthHelper;
import org.elastos.hive.DirectoryInfo;
import org.elastos.hive.Callback;
import org.elastos.hive.Directory;
import org.elastos.hive.File;
import org.elastos.hive.Result;
import org.elastos.hive.Status;

class OneDriveDirectory implements Directory {
	private final AuthHelper authHelper;
	private final String dirId;
	private DirectoryInfo dirInfo;

	OneDriveDirectory(DirectoryInfo dirInfo, AuthHelper authHelper) {
		this.authHelper = authHelper;
		this.dirId = dirInfo.getId();
		this.dirInfo = dirInfo;
	}

	@Override
	public String getId() {
		return dirId;
	}

	@Override
	public String getPath() {
		// TODO
		return null;
	}

	@Override
	public String getParentPath() {
		// TODO
		return null;
	}

	@Override
	public DirectoryInfo getLastInfo() {
		return dirInfo;
	}

	@Override
	public CompletableFuture<Result<DirectoryInfo>> getInfo() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public CompletableFuture<Result<DirectoryInfo>> getInfo(Callback<DirectoryInfo> callback) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public CompletableFuture<Result<Directory>> moveTo(String pathName) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public CompletableFuture<Result<Directory>> moveTo(String pathName, Callback<Directory> callback) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public CompletableFuture<Result<Directory>> copyTo(String pathName) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public CompletableFuture<Result<Directory>> copyTo(String pathName, Callback<Directory> callback) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public CompletableFuture<Result<Status>> deleteItem() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public CompletableFuture<Result<Status>> deleteItem(Callback<Status> callback) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void close() {
		// TODO Auto-generated method stub

	}

	@Override
	public CompletableFuture<Result<Directory>> createDirectory(String pathName) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public CompletableFuture<Result<Directory>> createDirectory(String pathName,
			Callback<Directory> callback) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public CompletableFuture<Result<Directory>> getDirectory(String pathName) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public CompletableFuture<Result<Directory>> getDirectory(String pathName, Callback<Directory> callback) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public CompletableFuture<Result<File>> createFile(String pathName) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public CompletableFuture<Result<File>> createFile(String pathName, Callback<File> callback) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public CompletableFuture<Result<File>> getFile(String pathName) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public CompletableFuture<Result<File>> getFile(String pathName, Callback<File> callback) {
		// TODO Auto-generated method stub
		return null;
	}
}
