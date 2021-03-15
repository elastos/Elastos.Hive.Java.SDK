package org.elastos.hive.vault;

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;

import org.elastos.hive.Vault;
import org.elastos.hive.connection.ConnectionManager;
import org.elastos.hive.exception.HiveException;
import org.elastos.hive.network.model.UploadOutputStream;
import org.elastos.hive.network.response.FilesHashResponseBody;
import org.elastos.hive.service.FilesService;
import retrofit2.Response;

class FilesServiceRender implements FilesService {
	private ConnectionManager connectionManager;

	public FilesServiceRender(Vault vault) {
		this.connectionManager = vault.getConnectionManager();
	}

	@Override
	public <T> CompletableFuture<T> upload(String path, Class<T> resultType) {
		return CompletableFuture.supplyAsync(() -> uploadImpl(path, resultType));
	}

	private <T>  T uploadImpl(String path, Class<T> resultType) {
		try {
			HttpURLConnection connection = this.connectionManager.openURLConnection("/files/upload/" + path);
			OutputStream outputStream = connection.getOutputStream();

			if(resultType.isAssignableFrom(OutputStream.class)) {
				UploadOutputStream uploader = new UploadOutputStream(connection, outputStream);
				return resultType.cast(uploader);
			} else if (resultType.isAssignableFrom(OutputStreamWriter.class)) {
				OutputStreamWriter writer = new OutputStreamWriter(outputStream);
				return resultType.cast(writer);
			} else {
				throw new HiveException("Not supported result type: " + resultType.getName());
			}
		} catch (HiveException|IOException e) {
			throw new CompletionException(e);
		}
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
			Response<FilesHashResponseBody> response = connectionManager.getFilesApi().hash(remoteFile).execute();
			FilesHashResponseBody hashResponse = FilesHashResponseBody.validateBody(response);
			return hashResponse.getSha256();
		} catch (HiveException | IOException e) {
			throw new CompletionException(new HiveException(e.getMessage()));
		}
	}
}
