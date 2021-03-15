package org.elastos.hive.vault;

import java.io.IOException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;

import org.elastos.hive.Vault;
import org.elastos.hive.connection.ConnectionManager;
import org.elastos.hive.exception.HiveException;
import org.elastos.hive.network.response.FilesHashResponse;
import org.elastos.hive.network.response.ResponseBase;
import org.elastos.hive.service.FilesService;
import retrofit2.Response;

class FilesServiceRender implements FilesService {
	private ConnectionManager connectionManager;

	public FilesServiceRender(Vault vault) {
		this.connectionManager = vault.getConnectionManager();
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
		return CompletableFuture.supplyAsync(() -> hashImp(path));
	}

	private String hashImp(String remoteFile) {
		try {
			Response<FilesHashResponse> response = connectionManager.getFilesApi().hash(remoteFile).execute();
			FilesHashResponse hashResponse = ResponseBase.validateBody(response);
			return hashResponse.getSha256();
		} catch (HiveException | IOException e) {
			throw new CompletionException(new HiveException(e.getMessage()));
		}
	}
}
