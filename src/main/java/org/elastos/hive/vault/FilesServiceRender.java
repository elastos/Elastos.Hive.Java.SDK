package org.elastos.hive.vault;

import org.elastos.hive.Vault;
import org.elastos.hive.exception.FileDoesNotExistsException;
import org.elastos.hive.exception.HttpFailedException;
import org.elastos.hive.service.FilesService;
import org.elastos.hive.vault.files.FileInfo;
import org.elastos.hive.vault.files.FilesController;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;

class FilesServiceRender implements FilesService, ExceptionConvertor {
	private FilesController controller;

	public FilesServiceRender(Vault vault) {
		this.controller = new FilesController(vault);
	}

	@Override
	public <T> CompletableFuture<T> upload(String path, Class<T> resultType) {
		return CompletableFuture.supplyAsync(() -> {
			try {
				return controller.upload(path, resultType);
			} catch (Exception e) {
				throw new CompletionException(toHiveException(e));
			}
		});
	}

	@Override
	public CompletableFuture<List<FileInfo>> list(String path) {
		return CompletableFuture.supplyAsync(() -> {
			try {
				return controller.listChildren(path);
			} catch (Exception e) {
				throw new CompletionException(toHiveException(e));
			}
		});
	}

	@Override
	public CompletableFuture<FileInfo> stat(String path) {
		return CompletableFuture.supplyAsync(() -> {
			try {
				return controller.getProperty(path);
			} catch (Exception e) {
				throw new CompletionException(toHiveException(e));
			}
		});
	}

	@Override
	public <T> CompletableFuture<T> download(String path, Class<T> resultType) {
		return CompletableFuture.supplyAsync(() -> {
			try {
				return controller.download(path, resultType);
			} catch (Exception e) {
				throw new CompletionException(toHiveException(e));
			}
		});
	}

	@Override
	public CompletableFuture<Boolean> delete(String path) {
		return CompletableFuture.supplyAsync(() -> {
			try {
				controller.delete(path);
				return true;
			} catch (Exception e) {
				throw new CompletionException(toHiveException(e));
			}
		});
	}

	@Override
	public CompletableFuture<Boolean> move(String source, String target) {
		return CompletableFuture.supplyAsync(() -> {
			try {
				controller.moveFile(source, target);
				return true;
			} catch (Exception e) {
				throw new CompletionException(toHiveException(e));
			}
		});
	}

	@Override
	public CompletableFuture<Boolean> copy(String source, String target) {
		return CompletableFuture.supplyAsync(() -> {
			try {
				controller.copyFile(source, target);
				return true;
			} catch (Exception e) {
				throw new CompletionException(toHiveException(e));
			}
		});
	}

	@Override
	public CompletableFuture<String> hash(String path) {
		return CompletableFuture.supplyAsync(() -> {
			try {
				return controller.getHash(path);
			} catch (Exception e) {
				throw new CompletionException(toHiveException(e));
			}
		});
	}

	@Override
	public Exception toHiveException(Exception e) {
		if (e instanceof HttpFailedException) {
			HttpFailedException ex = (HttpFailedException) e;
			if (ex.getCode() == 404)
				return new FileDoesNotExistsException();
		}
		return ExceptionConvertor.super.toHiveException(e);
	}
}
