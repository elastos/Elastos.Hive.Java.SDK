package org.elastos.hive.vendors.hiveIpfs;

import java.util.concurrent.CompletableFuture;

import org.elastos.hive.Callback;
import org.elastos.hive.Directory;
import org.elastos.hive.Drive;
import org.elastos.hive.DriveType;
import org.elastos.hive.File;

final class HiveIpfsDrive extends Drive{
	private final String uid;

	HiveIpfsDrive(String uid) {
		this.uid = uid;
	}

	@Override
	public String getId() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Info getLastInfo() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public CompletableFuture<Info> getInfo() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public CompletableFuture<Info> getInfo(Callback<Info> callback) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public CompletableFuture<Directory> createDirectory(String path) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public CompletableFuture<Directory> createDirectory(String path, Callback<Directory> callback) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public CompletableFuture<Directory> getDirectory(String path) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public CompletableFuture<Directory> getDirectory(String path, Callback<Directory> callback) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public CompletableFuture<File> createFile(String path) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public CompletableFuture<File> createFile(String path, Callback<File> callback) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public CompletableFuture<File> getFile(String path) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public CompletableFuture<File> getFile(String path, Callback<File> callback) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public DriveType getType() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public CompletableFuture<Directory> getRootDir() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public CompletableFuture<Directory> getRootDir(Callback<Directory> callback) {
		// TODO Auto-generated method stub
		return null;
	}

}
