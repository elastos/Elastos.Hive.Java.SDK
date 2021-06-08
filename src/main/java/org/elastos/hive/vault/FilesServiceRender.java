package org.elastos.hive.vault;

import org.elastos.hive.ServiceEndpoint;
import org.elastos.hive.exception.HiveException;
import org.elastos.hive.service.FilesService;
import org.elastos.hive.vault.files.FileInfo;
import org.elastos.hive.vault.files.FilesController;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;

import java.io.OutputStream;
import java.io.Writer;
import java.io.InputStream;
import java.io.Reader;

class FilesServiceRender implements FilesService {
	private FilesController controller;

	public FilesServiceRender(ServiceEndpoint serviceEndpoint) {
		this.controller = new FilesController(serviceEndpoint.getConnectionManager());
	}

	@Override
	public CompletableFuture<OutputStream> getUploadStream(String path) {
		return CompletableFuture.supplyAsync(() ->  {
			try {
				return controller.getUploadStream(path);
			} catch (HiveException | RuntimeException e) {
				throw new CompletionException(e);
			}
		});
	}

	@Override
	public CompletableFuture<Writer> getUploadWriter(String path) {
		return CompletableFuture.supplyAsync(() ->  {
			try {
				return controller.getUploadWriter(path);
			} catch (HiveException | RuntimeException e) {
				throw new CompletionException(e);
			}
		});
	}

	@Override
	public CompletableFuture<List<FileInfo>> list(String path) {
		return CompletableFuture.supplyAsync(() -> {
			try {
				return controller.listChildren(path);
			} catch (HiveException | RuntimeException e) {
				throw new CompletionException(e);
			}
		});
	}

	@Override
	public CompletableFuture<FileInfo> stat(String path) {
		return CompletableFuture.supplyAsync(() -> {
			try {
				return controller.getProperty(path);
			} catch (HiveException | RuntimeException e) {
				throw new CompletionException(e);
			}
		});
	}

	@Override
	public CompletableFuture<InputStream> getDownloadStream(String path) {
		return CompletableFuture.supplyAsync(() -> {
			try {
				return controller.getDownloadStream(path);
			} catch (HiveException | RuntimeException e) {
				throw new CompletionException(e);
			}
		});
	}

	@Override
	public CompletableFuture<Reader> getDownloadReader(String path) {
		return CompletableFuture.supplyAsync(() -> {
			try {
				return controller.getDownloadReader(path);
			} catch (HiveException | RuntimeException e) {
				throw new CompletionException(e);
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
				throw new CompletionException(e);
			}
		});
	}

	@Override
	public CompletableFuture<Boolean> move(String source, String target) {
		return CompletableFuture.supplyAsync(() -> {
			try {
				controller.moveFile(source, target);
				return true;
			} catch (HiveException | RuntimeException e) {
				throw new CompletionException(e);
			}
		});
	}

	@Override
	public CompletableFuture<Boolean> copy(String source, String target) {
		return CompletableFuture.supplyAsync(() -> {
			try {
				controller.copyFile(source, target);
				return true;
			} catch (HiveException | RuntimeException e) {
				throw new CompletionException(e);
			}
		});
	}

	@Override
	public CompletableFuture<String> hash(String path) {
		return CompletableFuture.supplyAsync(() -> {
			try {
				return controller.getHash(path);
			} catch (HiveException | RuntimeException e) {
				throw new CompletionException(e);
			}
		});
	}
}
