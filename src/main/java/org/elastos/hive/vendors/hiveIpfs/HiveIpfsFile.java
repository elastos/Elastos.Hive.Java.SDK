package org.elastos.hive.vendors.hiveIpfs;

import java.util.concurrent.CompletableFuture;

import org.elastos.hive.Callback;
import org.elastos.hive.File;
import org.elastos.hive.Status;

final class HiveIpfsFile extends File {

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
	public String getPath() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getParentPath() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public CompletableFuture<Status> moveTo(String path) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public CompletableFuture<Status> moveTo(String path, Callback<Status> callback) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public CompletableFuture<Status> copyTo(String path) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public CompletableFuture<Status> copyTo(String path, Callback<Status> callback) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public CompletableFuture<Status> deleteItem() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public CompletableFuture<Status> deleteItem(Callback<Status> callback) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void close() {
		// TODO Auto-generated method stub
		
	}

}
