package org.elastos.hive.vault;

import org.elastos.hive.ServiceEndpoint;
import org.elastos.hive.Vault;
import org.elastos.hive.connection.ConnectionManager;
import org.elastos.hive.exception.FileDoesNotExistsException;
import org.elastos.hive.exception.HttpFailedException;
import org.elastos.hive.network.CallAPI;
import org.elastos.hive.network.model.FileInfo;
import org.elastos.hive.network.request.FilesCopyRequestBody;
import org.elastos.hive.network.request.FilesDeleteRequestBody;
import org.elastos.hive.network.request.FilesMoveRequestBody;
import org.elastos.hive.network.response.HiveResponseBody;
import org.elastos.hive.service.FilesService;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;

class FilesServiceRender implements FilesService, ExceptionConvertor {
	private ServiceEndpoint serviceEndpoint;

	public FilesServiceRender(Vault vault) {
		this.serviceEndpoint = vault;
	}

	@Override
	public <T> CompletableFuture<T> upload(String path, Class<T> resultType) {
		return CompletableFuture.supplyAsync(() -> {
			try {
				return HiveResponseBody.getRequestStream(
						serviceEndpoint.getConnectionManager().openConnection(CallAPI.API_UPLOAD + "/" + path),
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
						serviceEndpoint.getConnectionManager().getCallAPI()
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
						serviceEndpoint.getConnectionManager().getCallAPI()
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
						serviceEndpoint.getConnectionManager().getCallAPI()
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
				HiveResponseBody.validateBody(serviceEndpoint.getConnectionManager().getCallAPI()
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
						serviceEndpoint.getConnectionManager().getCallAPI()
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
						serviceEndpoint.getConnectionManager().getCallAPI()
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
						serviceEndpoint.getConnectionManager().getCallAPI()
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
