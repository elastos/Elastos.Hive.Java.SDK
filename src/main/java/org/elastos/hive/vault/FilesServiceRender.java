package org.elastos.hive.vault;

import org.elastos.hive.Vault;
import org.elastos.hive.connection.ConnectionManager;
import org.elastos.hive.exception.HiveException;
import org.elastos.hive.network.FilesApi;
import org.elastos.hive.network.model.FileInfo;
import org.elastos.hive.network.request.FilesCopyRequestBody;
import org.elastos.hive.network.request.FilesDeleteRequestBody;
import org.elastos.hive.network.request.FilesMoveRequestBody;
import org.elastos.hive.network.response.HiveResponseBody;
import org.elastos.hive.service.FilesService;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;

class FilesServiceRender implements FilesService {
	private ConnectionManager connectionManager;

	public FilesServiceRender(Vault vault) {
		this.connectionManager = vault.getAppContext().getConnectionManager();
	}

	@Override
	public <T> CompletableFuture<T> upload(String path, Class<T> resultType) {
		return CompletableFuture.supplyAsync(() -> {
			try {
				return HiveResponseBody.getRequestStream(
						connectionManager.openConnection(FilesApi.API_UPLOAD + "/" + path),
						resultType);
			} catch (IOException e) {
				throw new CompletionException(e);
			}
		});
	}

	@Override
	public CompletableFuture<List<FileInfo>> list(String path) {
		return CompletableFuture.supplyAsync(() -> {
			try {
				return HiveResponseBody.validateBody(
						connectionManager.getFilesApi()
								.list(path)
								.execute()
								.body()).getFileInfoList();
			} catch (HiveException | IOException e) {
				throw new CompletionException(new HiveException(e.getMessage()));
			}
		});
	}

	@Override
	public CompletableFuture<FileInfo> stat(String path) {
		return CompletableFuture.supplyAsync(() -> {
			try {
				return HiveResponseBody.validateBody(
						connectionManager.getFilesApi()
								.properties(path)
								.execute().body()).getFileInfo();
			} catch (Exception e) {
				throw new CompletionException(new HiveException(e.getMessage()));
			}
		});
	}

	@Override
	public <T> CompletableFuture<T> download(String path, Class<T> resultType) {
		return CompletableFuture.supplyAsync(() -> {
			try {
				return HiveResponseBody.getResponseStream(
						connectionManager.getFilesApi()
								.download(path)
								.execute(), resultType);
			} catch (HiveException|IOException e) {
				throw new CompletionException(new HiveException(e.getMessage()));
			}
		});
	}

	@Override
	public CompletableFuture<Boolean> delete(String path) {
		return CompletableFuture.supplyAsync(() -> {
			try {
				HiveResponseBody.validateBody(connectionManager.getFilesApi()
						.delete(new FilesDeleteRequestBody(path))
						.execute().body());
				return true;
			} catch (Exception e) {
				throw new CompletionException(new HiveException(e.getMessage()));
			}
		});
	}

	@Override
	public CompletableFuture<Boolean> move(String source, String target) {
		return CompletableFuture.supplyAsync(() -> {
			try {
				HiveResponseBody.validateBody(
						connectionManager.getFilesApi()
								.move(new FilesMoveRequestBody(source, target))
								.execute().body());
				return true;
			} catch (Exception e) {
				throw new CompletionException(new HiveException(e.getMessage()));
			}
		});
	}

	@Override
	public CompletableFuture<Boolean> copy(String source, String target) {
		return CompletableFuture.supplyAsync(() -> {
			try {
				HiveResponseBody.validateBody(
						connectionManager.getFilesApi()
						.copy(new FilesCopyRequestBody(source, target))
						.execute().body());
				return true;
			} catch (Exception e) {
				throw new CompletionException(new HiveException(e.getMessage()));
			}
		});
	}

	@Override
	public CompletableFuture<String> hash(String path) {
		return CompletableFuture.supplyAsync(() -> {
			try {
				return HiveResponseBody.validateBody(
						connectionManager.getFilesApi()
								.hash(path)
								.execute()
								.body()).getSha256();
			} catch (HiveException | IOException e) {
				throw new CompletionException(new HiveException(e.getMessage()));
			}
		});
	}

}
