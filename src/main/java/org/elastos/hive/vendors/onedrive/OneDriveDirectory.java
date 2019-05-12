package org.elastos.hive.vendors.onedrive;

import java.util.concurrent.CompletableFuture;

import org.elastos.hive.AuthHelper;
import org.elastos.hive.DirectoryInfo;
import org.elastos.hive.HiveCallback;
import org.elastos.hive.HiveDirectory;
import org.elastos.hive.HiveException;
import org.elastos.hive.HiveFile;
import org.elastos.hive.HiveResult;
import org.elastos.hive.Status;

class OneDriveDirectory implements HiveDirectory {
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
	public CompletableFuture<HiveResult<DirectoryInfo>> getInfo() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public CompletableFuture<HiveResult<DirectoryInfo>> getInfo(HiveCallback<DirectoryInfo, HiveException> callback) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public CompletableFuture<HiveResult<HiveDirectory>> moveTo(String pathName) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public CompletableFuture<HiveResult<HiveDirectory>> moveTo(String pathName,
			HiveCallback<HiveDirectory, HiveException> callback) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public CompletableFuture<HiveResult<HiveDirectory>> copyTo(String pathName) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public CompletableFuture<HiveResult<HiveDirectory>> copyTo(String pathName,
			HiveCallback<HiveDirectory, HiveException> callback) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public CompletableFuture<HiveResult<Status>> deleteItem() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public CompletableFuture<HiveResult<Status>> deleteItem(HiveCallback<Status, HiveException> callback) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void close() {
		// TODO Auto-generated method stub

	}

	@Override
	public CompletableFuture<HiveResult<HiveDirectory>> createDirectory(String pathName) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public CompletableFuture<HiveResult<HiveDirectory>> createDirectory(String pathName,
			HiveCallback<HiveDirectory, HiveException> callback) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public CompletableFuture<HiveResult<HiveDirectory>> getDirectory(String pathName) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public CompletableFuture<HiveResult<HiveDirectory>> getDirectory(String pathName,
			HiveCallback<HiveDirectory, HiveException> callback) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public CompletableFuture<HiveResult<HiveFile>> createFile(String pathName) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public CompletableFuture<HiveResult<HiveFile>> createFile(String pathName,
			HiveCallback<HiveFile, HiveException> callback) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public CompletableFuture<HiveResult<HiveFile>> getFile(String pathName) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public CompletableFuture<HiveResult<HiveFile>> getFile(String pathName,
			HiveCallback<HiveFile, HiveException> callback) {
		// TODO Auto-generated method stub
		return null;
	}
}
