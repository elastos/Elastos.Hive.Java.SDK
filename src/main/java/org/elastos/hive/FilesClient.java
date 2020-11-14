package org.elastos.hive;

import java.io.IOException;
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
import org.elastos.hive.files.FilesList;
import org.elastos.hive.files.UploadOutputStream;
import org.elastos.hive.network.NodeApi;
import org.elastos.hive.utils.JsonUtil;
import org.elastos.hive.utils.ResponseHelper;

import com.fasterxml.jackson.databind.JsonNode;

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
		return authHelper.checkValid().thenApply(aVoid -> {
			try {
				return uploadImpl(path, resultType);
			} catch (HiveException e) {
				e.printStackTrace();
				throw new CompletionException(e);
			}
		});
	}

	private <T> T uploadImpl(String path, Class<T> resultType) throws HiveException {
		try {
			HttpURLConnection connection = null;
			connection = this.connectionManager.openURLConnection(path);
			OutputStream outputStream = connection.getOutputStream();

			if(resultType.isAssignableFrom(OutputStream.class)) {
				UploadOutputStream uploader = new UploadOutputStream(connection, outputStream);
				return resultType.cast(uploader);
			} else if (resultType.isAssignableFrom(OutputStreamWriter.class)) {
				OutputStreamWriter writer = new OutputStreamWriter(outputStream);
				return resultType.cast(writer);
			} else {
				throw new HiveException("Not supported result type");
			}
		} catch (IOException e) {
			throw new HiveException(e.getMessage());
		}
	}

	@Override
	public <T> CompletableFuture<T> download(String path, Class<T> resultType) {
		return authHelper.checkValid().thenApply(aVoid -> {
			try {
				return downloadImpl(path, resultType);
			} catch (HiveException e) {
				e.printStackTrace();
				throw new CompletionException(e);
			}
		});
	}

	private <T> T downloadImpl(String remoteFile, Class<T> resultType) throws HiveException {
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

			throw new HiveException("Not supported result type");
		} catch (Exception e) {
			throw new HiveException(e.getLocalizedMessage());
		}
	}

	@Override
	public CompletableFuture<Boolean> delete(String remoteFile) {
		return authHelper.checkValid().thenApply(aVoid -> {
			try {
				return deleteImpl(remoteFile);
			} catch (HiveException e) {
				e.printStackTrace();
				throw new CompletionException(e);
			}
		});
	}

	private Boolean deleteImpl(String remoteFile) throws HiveException {
		try {
			Map<String, String> map = new HashMap<>();
			map.put("path", remoteFile);

			String json = JsonUtil.serialize(map);
			Response<ResponseBody> response;

			response = this.connectionManager.getVaultApi()
					.deleteFolder(createJsonRequestBody(json))
					.execute();
			authHelper.checkResponseWithRetry(response);
			return true;
		} catch (Exception e) {
			throw new HiveException(e.getMessage());
		}
	}

	@Override
	public CompletableFuture<Boolean> move(String source, String dest) {
		return authHelper.checkValid().thenApply(aVoid -> {
			try {
				return moveImpl(source, dest);
			} catch (HiveException e) {
				e.printStackTrace();
				throw new CompletionException(e);
			}
		});
	}

	private Boolean moveImpl(String source, String dest) throws HiveException {
		try {
			Map<String, Object> map = new HashMap<>();
			map.put("src_path", source);
			map.put("dst_path", dest);

			String json = JsonUtil.serialize(map);
			Response<ResponseBody> response;

			response = this.connectionManager.getVaultApi()
					.move(createJsonRequestBody(json))
					.execute();
			authHelper.checkResponseWithRetry(response);
			return true;
		} catch (Exception e) {
			throw new HiveException(e.getMessage());
		}
	}

	@Override
	public CompletableFuture<Boolean> copy(String source, String dest) {
		return authHelper.checkValid().thenApply(aVoid -> {
			try {
				return copyImpl(source, dest);
			} catch (HiveException e) {
				e.printStackTrace();
				throw new CompletionException(e);
			}
		});
	}

	private Boolean copyImpl(String source, String dest) throws HiveException {
		try {
			Map<String, Object> map = new HashMap<>();
			map.put("src_path", source);
			map.put("dst_path", dest);

			String json = JsonUtil.serialize(map);
			Response<ResponseBody> response;

			response = this.connectionManager.getVaultApi()
					.copy(createJsonRequestBody(json))
					.execute();
			authHelper.checkResponseWithRetry(response);
			return true;
		} catch (Exception e) {
			throw new HiveException(e.getLocalizedMessage());
		}
	}

	@Override
	public CompletableFuture<String> hash(String remoteFile) {
		return authHelper.checkValid().thenApply(aVoid -> {
			try {
				return hashImp(remoteFile);
			} catch (HiveException e) {
				e.printStackTrace();
				throw new CompletionException(e);
			}
		});
	}

	private String hashImp(String remoteFile) throws HiveException {
		try {
			Response response = this.connectionManager.getVaultApi()
					.hash(remoteFile)
					.execute();
			authHelper.checkResponseWithRetry(response);
			JsonNode ret = ResponseHelper.getValue(response, JsonNode.class);
			return ret.get("SHA256").toString();
		} catch (Exception e) {
			throw new HiveException(e.getLocalizedMessage());
		}
	}

	@Override
	public CompletableFuture<List<FileInfo>> list(String folder) {
		return authHelper.checkValid().thenApply(aVoid -> {
			try {
				return listImpl(folder);
			} catch (HiveException e) {
				e.printStackTrace();
				throw new CompletionException(e);
			}
		});
	}

	private List<FileInfo> listImpl(String folder) throws HiveException {
		try {
			NodeApi api = this.connectionManager.getVaultApi();
			Response<FilesList> response = api.files(folder).execute();

			authHelper.checkResponseWithRetry(response);
			return response.body().getFiles();
		} catch (Exception e) {
			throw new HiveException(e.getLocalizedMessage());
		}
	}

	@Override
	public CompletableFuture<FileInfo> stat(String path) {
		return authHelper.checkValid().thenApply(aVoid -> {
			try {
				return statImpl(path);
			} catch (HiveException e) {
				e.printStackTrace();
				throw new CompletionException(e);
			}
		});
	}

	private FileInfo statImpl(String path) throws HiveException {
		try {
			NodeApi api = this.connectionManager.getVaultApi();
			Response<FileInfo> response = api.getProperties(path).execute();

			authHelper.checkResponseWithRetry(response);
			return response.body();
		} catch (Exception e) {
			throw new HiveException(e.getLocalizedMessage());
		}
	}

	private RequestBody createJsonRequestBody(String json) {
		return RequestBody.create(MediaType.parse("Content-Type, application/json"), json);
	}
}
