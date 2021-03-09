package org.elastos.hive.vault;

import java.util.concurrent.CompletableFuture;

import org.elastos.hive.Vault;
import org.elastos.hive.service.FilesService;

class FilesServiceRender implements FilesService {

	public FilesServiceRender(Vault vault) {
		// TODO;
	}

	@Override
	public <T> CompletableFuture<T> upload(String path, Class<T> resultType) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public <T> CompletableFuture<T> download(String path, Class<T> resultType) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public CompletableFuture<Boolean> delete(String path) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public CompletableFuture<Boolean> move(String source, String target) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public CompletableFuture<Boolean> copy(String source, String target) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public CompletableFuture<String> hash(String path) {
		// TODO Auto-generated method stub
		return null;
	}
}
