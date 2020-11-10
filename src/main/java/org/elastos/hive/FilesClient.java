package org.elastos.hive;

import com.fasterxml.jackson.databind.JsonNode;

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
import org.elastos.hive.files.FilesList;
import org.elastos.hive.files.UploadOutputStream;
import org.elastos.hive.utils.JsonUtil;
import org.elastos.hive.utils.ResponseHelper;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
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

				if(resultType.isAssignableFrom(OutputStream.class)) {
					UploadOutputStream uploader = new UploadOutputStream(connection, outputStream);
					return resultType.cast(uploader);
				}

				if (resultType.isAssignableFrom(OutputStreamWriter.class)) {
					OutputStreamWriter writer = new OutputStreamWriter(outputStream);
					return resultType.cast(writer);
				}

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
				Response<ResponseBody> response;

				response = this.connectionManager.getVaultApi()
						.downloader(remoteFile)
						.execute();
				if (response == null)
					throw new HiveException(HiveException.ERROR);

				authHelper.checkResponseWithRetry(response);

				if(resultType.isAssignableFrom(Reader.class)) {
					Reader reader = ResponseHelper.getToReader(response);
					return resultType.cast(reader);
				}
				if (resultType.isAssignableFrom(InputStream.class)){
					InputStream inputStream = ResponseHelper.getInputStream(response);
					return resultType.cast(inputStream);
				}

				HiveException e = new HiveException("Not supported result type");
				throw new CompletionException(e);

			} catch (Exception e) {
				HiveException ex = new HiveException(e.getLocalizedMessage());
				throw new CompletionException(ex);
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

				String json = JsonUtil.serialize(map);
				Response<ResponseBody> response;

				response = this.connectionManager.getVaultApi()
						.deleteFolder(createJsonRequestBody(json))
						.execute();
				authHelper.checkResponseWithRetry(response);
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

				String json = JsonUtil.serialize(map);
				Response<ResponseBody> response;

				response = this.connectionManager.getVaultApi()
						.move(createJsonRequestBody(json))
						.execute();
				authHelper.checkResponseWithRetry(response);
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

				String json = JsonUtil.serialize(map);
				Response<ResponseBody> response;

				response = this.connectionManager.getVaultApi()
						.copy(createJsonRequestBody(json))
						.execute();
				authHelper.checkResponseWithRetry(response);
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
				Response response = this.connectionManager.getVaultApi()
						.hash(remoteFile)
						.execute();
				authHelper.checkResponseWithRetry(response);
				JsonNode ret = ResponseHelper.getValue(response, JsonNode.class);
				return ret.get("SHA256").toString();
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
				NodeApi api = this.connectionManager.getVaultApi();
				Response<FilesList> response = api.files(folder).execute();

				authHelper.checkResponseWithRetry(response);
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
				NodeApi api = this.connectionManager.getVaultApi();
				Response<FileInfo> response = api.getProperties(path).execute();

				authHelper.checkResponseWithRetry(response);
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
}
