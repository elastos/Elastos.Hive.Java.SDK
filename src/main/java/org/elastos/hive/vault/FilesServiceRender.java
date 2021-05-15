package org.elastos.hive.vault;

import org.elastos.hive.Vault;
import org.elastos.hive.exception.FileDoesNotExistsException;
import org.elastos.hive.exception.HttpFailedException;
import org.elastos.hive.network.FilesAPI;
import org.elastos.hive.network.model.FileInfo;
import org.elastos.hive.network.request.FilesCopyRequestBody;
import org.elastos.hive.network.request.FilesDeleteRequestBody;
import org.elastos.hive.network.request.FilesMoveRequestBody;
import org.elastos.hive.network.response.HiveResponseBody;
import org.elastos.hive.service.FilesService;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;

class FilesServiceRender extends BaseServiceRender implements FilesService, ExceptionConvertor {
	public FilesServiceRender(Vault vault) {
		super(vault);
	}

	@Override
	public <T> CompletableFuture<T> upload(String path, Class<T> resultType) {
		return CompletableFuture.supplyAsync(() -> {
			try {
				return HiveResponseBody.getRequestStream(
						getConnectionManager().openConnection(FilesAPI.API_UPLOAD + "/" + path),
						resultType);
			} catch (Exception e) {
				throw new CompletionException(toHiveException(e));
			}
		});
	}

	@Override
	public CompletableFuture<List<FileInfo>> list(String path) {
		return CompletableFuture.supplyAsync(() -> {
			try {
				return HiveResponseBody.validateBody(
						getConnectionManager().getFilesApi()
								.list(path)
								.execute()
								.body()).getFileInfoList();
			} catch (Exception e) {
				throw new CompletionException(toHiveException(e));
			}
		});
	}

	@Override
	public CompletableFuture<FileInfo> stat(String path) {
		return CompletableFuture.supplyAsync(() -> {
			try {
				return HiveResponseBody.validateBody(
						getConnectionManager().getFilesApi()
								.properties(path)
								.execute().body()).getFileInfo();
			} catch (Exception e) {
				throw new CompletionException(toHiveException(e));
			}
		});
	}

	@Override
	public <T> CompletableFuture<T> download(String path, Class<T> resultType) {
		return CompletableFuture.supplyAsync(() -> {
			try {
				return HiveResponseBody.getResponseStream(
						getConnectionManager().getFilesApi()
								.download(path)
								.execute(), resultType);
			} catch (Exception e) {
				throw new CompletionException(toHiveException(e));
			}
		});
	}

	@Override
	public CompletableFuture<Boolean> delete(String path) {
		return CompletableFuture.supplyAsync(() -> {
			try {
				HiveResponseBody.validateBody(getConnectionManager().getFilesApi()
						.delete(new FilesDeleteRequestBody(path))
						.execute().body());
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
				HiveResponseBody.validateBody(
						getConnectionManager().getFilesApi()
								.move(new FilesMoveRequestBody(source, target))
								.execute().body());
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
				HiveResponseBody.validateBody(
						getConnectionManager().getFilesApi()
						.copy(new FilesCopyRequestBody(source, target))
						.execute().body());
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
				return HiveResponseBody.validateBody(
						getConnectionManager().getFilesApi()
								.hash(path)
								.execute()
								.body()).getSha256();
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
