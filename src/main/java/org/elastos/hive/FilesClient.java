package org.elastos.hive;

import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.net.HttpURLConnection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;

import org.elastos.hive.connection.ConnectionManager;
import org.elastos.hive.exception.HiveException;
import org.elastos.hive.files.FileInfo;
import org.elastos.hive.network.NodeApi;
import org.elastos.hive.network.model.FilesResponse;
import org.elastos.hive.network.model.HashResponse;
import org.elastos.hive.network.model.UploadOutputStream;
import org.elastos.hive.utils.JsonUtil;
import org.elastos.hive.utils.ResponseHelper;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import retrofit2.Response;

class FilesClient implements Files {
	private AuthHelper authHelper;
	private ConnectionManager connectionManager;

	FilesClient(AuthHelper authHelper) {
		this.authHelper = authHelper;
		this.connectionManager = authHelper.getConnectionManager();
	}

	@Override
	public <T> CompletableFuture<T> upload(String path, Class<T> resultType) {
		return authHelper.checkValid()
				.thenCompose(result -> uploadImp(path, resultType));
	}

	private <T> CompletableFuture<T> uploadImp(String path, Class<T> resultType) {
		return CompletableFuture.supplyAsync(() -> {
			HttpURLConnection connection = null;
			try {
				connection = this.connectionManager.openURLConnection(path);
				OutputStream outputStream = connection.getOutputStream();

				if (outputStream == null) {
					HiveException e = new HiveException("Connection failure");
					throw new CompletionException(e);
				}

				if(resultType.isAssignableFrom(OutputStream.class))
					return resultType.cast(new UploadOutputStream(connection, outputStream));

				if (resultType.isAssignableFrom(OutputStreamWriter.class))
					return resultType.cast(new OutputStreamWriter(outputStream));

				HiveException e = new HiveException("Not supported result type");
				throw new CompletionException(e);

			} catch (Exception e) {
				ResponseHelper.readConnection(connection);
				HiveException ex = new HiveException(e.getLocalizedMessage());
				throw new CompletionException(ex);
			}
		});
	}

	@Override
	public <T> CompletableFuture<T> download(String path, Class<T> resultType) {
		return authHelper.checkValid()
				.thenCompose(result -> downloadImp(path, resultType));
	}

	private <T> CompletableFuture<T> downloadImp(String remoteFile, Class<T> resultType) {

		return CompletableFuture.supplyAsync(() -> {
			try {
				Response response = getFileOrBuffer(remoteFile);

				if (response == null)
					throw new HiveException(HiveException.ERROR);

				authHelper.checkResponseCode(response);
				if(resultType.isAssignableFrom(Reader.class)) {
					Reader reader = ResponseHelper.getToReader(response);
					return (T) reader;
				} else {
					InputStream inputStream = ResponseHelper.getInputStream(response);
					return (T) inputStream;
				}
			} catch (Exception e) {
				HiveException exception = new HiveException(e.getLocalizedMessage());
				throw new CompletionException(exception);
			}
		});

	}

	@Override
	public CompletableFuture<Boolean> delete(String remoteFile) {
		return authHelper.checkValid()
				.thenCompose(result -> deleteImp(remoteFile));
	}

	private CompletableFuture<Boolean> deleteImp(String remoteFile) {
		return CompletableFuture.supplyAsync(() -> {
			try {
				Map<String, Object> map = new HashMap<>();
				map.put("path", remoteFile);
				String json = JsonUtil.getJsonFromObject(map);

				Response response = this.connectionManager.getHiveVaultApi()
						.deleteFolder(createJsonRequestBody(json))
						.execute();
				authHelper.checkResponseCode(response);
				return true;
			} catch (Exception e) {
				HiveException exception = new HiveException(e.getLocalizedMessage());
				throw new CompletionException(exception);
			}
		});
	}

	@Override
	public CompletableFuture<Boolean> move(String src, String dst) {
		return authHelper.checkValid()
				.thenCompose(result -> moveImp(src, dst));
	}

	private CompletableFuture<Boolean> moveImp(String src, String dst) {

		return CompletableFuture.supplyAsync(() -> {
			try {
				Map<String, Object> map = new HashMap<>();
				map.put("src_path", src);
				map.put("dst_path", dst);
				String json = JsonUtil.getJsonFromObject(map);
				Response response = this.connectionManager.getHiveVaultApi()
						.move(createJsonRequestBody(json))
						.execute();
				authHelper.checkResponseCode(response);
				return true;
			} catch (Exception e) {
				HiveException exception = new HiveException(e.getLocalizedMessage());
				throw new CompletionException(exception);
			}
		});
	}

	@Override
	public CompletableFuture<Boolean> copy(String src, String dst) {
		return authHelper.checkValid()
				.thenCompose(result -> copyImp(src, dst));
	}

	private CompletableFuture<Boolean> copyImp(String src, String dst) {

		return CompletableFuture.supplyAsync(() -> {
			try {
				Map<String, Object> map = new HashMap<>();
				map.put("src_path", src);
				map.put("dst_path", dst);
				String json = JsonUtil.getJsonFromObject(map);
				Response response = this.connectionManager.getHiveVaultApi()
						.copy(createJsonRequestBody(json))
						.execute();
				authHelper.checkResponseCode(response);
				return true;
			} catch (Exception e) {
				HiveException exception = new HiveException(e.getLocalizedMessage());
				throw new CompletionException(exception);
			}
		});
	}

	@Override
	public CompletableFuture<String> hash(String remoteFile) {
		return authHelper.checkValid()
				.thenCompose(result -> hashImp(remoteFile));
	}

	private CompletableFuture<String> hashImp(String remoteFile) {

		return CompletableFuture.supplyAsync(() -> {
			try {
				Response<HashResponse> response = this.connectionManager.getHiveVaultApi()
						.hash(remoteFile)
						.execute();
				authHelper.checkResponseCode(response);
				String ret = response.body().getSHA256();
				return ret;
			} catch (Exception e) {
				HiveException exception = new HiveException(e.getLocalizedMessage());
				throw new CompletionException(exception);
			}
		});
	}

	@Override
	public CompletableFuture<List<FileInfo>> list(String folder) {
		return authHelper.checkValid()
				.thenCompose(result -> listImp(folder));
	}

	private CompletableFuture<List<FileInfo>> listImp(String folder) {

		return CompletableFuture.supplyAsync(() -> {
			try {
				NodeApi api = this.connectionManager.getHiveVaultApi();
				Response<FilesResponse> response = api.files(folder).execute();

				authHelper.checkResponseCode(response);
				List<FileInfo> list = response.body().getFiles();
				return list;
			} catch (Exception e) {
				HiveException exception = new HiveException(e.getLocalizedMessage());
				throw new CompletionException(exception);
			}
		});
	}

	@Override
	public CompletableFuture<FileInfo> stat(String path) {
		return authHelper.checkValid()
				.thenCompose(result -> statImp(path));
	}

	public CompletableFuture<FileInfo> statImp(String path) {
		return CompletableFuture.supplyAsync(() -> {
			try {
				NodeApi api = this.connectionManager.getHiveVaultApi();
				Response<FileInfo> response = api.getProperties(path).execute();

				authHelper.checkResponseCode(response);
				FileInfo fileInfo = response.body();
				return fileInfo;
			} catch (Exception e) {
				HiveException exception = new HiveException(e.getLocalizedMessage());
				throw new CompletionException(exception);
			}
		});
	}

	private RequestBody createJsonRequestBody(String json) {
		return RequestBody.create(MediaType.parse("Content-Type, application/json"), json);
	}

	private Response getFileOrBuffer(String destFilePath) throws HiveException {
		Response response;
		try {
			response = this.connectionManager.getHiveVaultApi()
					.downloader(destFilePath)
					.execute();

		} catch (Exception ex) {
			throw new HiveException(ex.getMessage());
		}
		return response;
	}
}
