package org.elastos.hive.vault;

import org.elastos.hive.Vault;
import org.elastos.hive.connection.ConnectionManager;
import org.elastos.hive.exception.HiveException;
import org.elastos.hive.network.FilesApi;
import org.elastos.hive.network.model.FileInfo;
import org.elastos.hive.network.model.UploadOutputStream;
import org.elastos.hive.network.request.FilesDeleteRequestBody;
import org.elastos.hive.network.response.FilesHashResponseBody;
import org.elastos.hive.network.response.FilesListResponseBody;
import org.elastos.hive.network.response.FilesPropertiesResponseBody;
import org.elastos.hive.network.response.ResponseBodyBase;
import org.elastos.hive.service.FilesService;
import org.elastos.hive.utils.ResponseHelper;
import retrofit2.Response;

import java.io.*;
import java.net.HttpURLConnection;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;

class FilesServiceRender implements FilesService {
	private ConnectionManager connectionManager;

	public FilesServiceRender(Vault vault) {
		this.connectionManager = vault.getConnectionManager();
	}

	@Override
	public <T> CompletableFuture<T> upload(String path, Class<T> resultType) {
		return CompletableFuture.supplyAsync(() -> uploadImpl(path, resultType));
	}

	private <T> T uploadImpl(String path, Class<T> resultType) {
		try {
			HttpURLConnection connection = this.connectionManager.openConnection(FilesApi.API_UPLOAD + "/" + path);
			OutputStream outputStream = connection.getOutputStream();

			if (resultType.isAssignableFrom(OutputStream.class)) {
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
	public CompletableFuture<List<FileInfo>> list(String path) {
		return CompletableFuture.supplyAsync(() -> listImpl(path));
	}

	private List<FileInfo> listImpl(String path) {
		try {
			Response<FilesListResponseBody> response = connectionManager.getFilesApi().list(path).execute();
			FilesListResponseBody body = ResponseBodyBase.validateBody(response);
			return body.getFileInfoList();
		} catch (HiveException | IOException e) {
			throw new CompletionException(new HiveException(e.getMessage()));
		}
	}

	@Override
	public CompletableFuture<FileInfo> stat(String path) {
		return CompletableFuture.supplyAsync(() -> statImpl(path));
	}

	private FileInfo statImpl(String path) {
		try {
			Response<FilesPropertiesResponseBody> response = this.connectionManager.getFilesApi()
					.properties(path).execute();
			FilesPropertiesResponseBody body = ResponseBodyBase.validateBody(response);
			return body.getFileInfo();
		} catch (Exception e) {
			throw new CompletionException(new HiveException(e.getMessage()));
		}
	}

	@Override
	public <T> CompletableFuture<T> download(String path, Class<T> resultType) {
		return CompletableFuture.supplyAsync(() -> downloadImpl(path, resultType));
	}

	private <T> T downloadImpl(String remoteFile, Class<T> resultType) {
		try {
			Response response = this.connectionManager.getFilesApi()
					.download(remoteFile)
					.execute();
			if(resultType.isAssignableFrom(Reader.class)) {
				Reader reader = ResponseHelper.getToReader(response);
				return resultType.cast(reader);
			}
			if (resultType.isAssignableFrom(InputStream.class)){
				InputStream inputStream = ResponseHelper.getInputStream(response);
				return resultType.cast(inputStream);
			}
			throw new HiveException("Not supported result type");
		} catch (HiveException|IOException e) {
			throw new CompletionException(new HiveException(e.getMessage()));
		}
	}

	@Override
	public CompletableFuture<Boolean> delete(String path) {
		return CompletableFuture.supplyAsync(() -> deleteImpl(path));
	}

	private Boolean deleteImpl(String path) {
		try {
			FilesDeleteRequestBody reqBody = new FilesDeleteRequestBody();
			reqBody.setPath(path);
			Response<ResponseBodyBase> response = this.connectionManager.getFilesApi()
					.delete(reqBody)
					.execute();
			ResponseBodyBase.validateBody(response);
			return true;
		} catch (Exception e) {
			throw new CompletionException(new HiveException(e.getMessage()));
		}
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
			FilesHashResponseBody hashResponse = ResponseBodyBase.validateBody(response);
			return hashResponse.getSha256();
		} catch (HiveException | IOException e) {
			throw new CompletionException(new HiveException(e.getMessage()));
		}
	}
}
