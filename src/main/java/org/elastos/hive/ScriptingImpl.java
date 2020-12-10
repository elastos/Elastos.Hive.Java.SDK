package org.elastos.hive;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;

import org.elastos.hive.connection.ConnectionManager;
import org.elastos.hive.exception.FileNotFoundException;
import org.elastos.hive.exception.HiveException;
import org.elastos.hive.files.UploadOutputStream;
import org.elastos.hive.scripting.Condition;
import org.elastos.hive.scripting.Executable;
import org.elastos.hive.utils.JsonUtil;
import org.elastos.hive.utils.ResponseHelper;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.net.HttpURLConnection;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Response;

class ScriptingImpl implements Scripting {
	private AuthHelper authHelper;
	private ConnectionManager connectionManager;

	ScriptingImpl(AuthHelper authHelper) {
		this.authHelper = authHelper;
		this.connectionManager = authHelper.getConnectionManager();
	}

	@Override
	public CompletableFuture<Boolean> registerScript(String name, Executable executable) {
		return this.registerScript(name, null, executable);
	}

	@Override
	public CompletableFuture<Boolean> registerScript(String name, Condition condition, Executable executable) {
		return authHelper.checkValid().thenApplyAsync(aVoid -> {
			try {
				return registerScriptImpl(name, condition, executable);
			} catch (HiveException e) {
				throw new CompletionException(e);
			}
		});
	}

	private boolean registerScriptImpl(String name, Condition condition, Executable executable) throws HiveException {
		try {
			Map<String, Object> map = new HashMap<>();
			map.put("name", name);
			map.put("executable", executable);
			if (condition != null)
				map.put("condition", condition);

			String json = JsonUtil.serialize(map);

			Response<ResponseBody> response;
			response = this.connectionManager.getScriptingApi()
					.registerScript(RequestBody.create(MediaType.parse("Content-Type, application/json"), json))
					.execute();

			authHelper.checkResponseWithRetry(response);
			return true;
		} catch (IOException e) {
			throw new HiveException(new HiveException(e.getMessage()));
		}
	}

	@Override
	public <T> CompletableFuture<T> callScript(String name,  JsonNode params, String appDid, Class<T> resultType) {
		return authHelper.checkValid().thenApplyAsync(aVoid -> {
			try {
				return callScriptImpl(name, params, appDid, resultType);
			} catch (HiveException e) {
				throw new CompletionException(e);
			}
		});
	}

	private <T> T callScriptImpl(String scriptName, JsonNode params, String appDid, Class<T> clazz) throws HiveException {
		try {
			Map<String, Object> map = new HashMap<>();
			map.put("name", scriptName);

			if(params!= null) map.put("params", params);

			ObjectNode targetNode = JsonNodeFactory.instance.objectNode();
			String ownerDid = this.authHelper.getOwnerDid();
			if (null != ownerDid) {
				targetNode.put("target_did", ownerDid);
				if (null != appDid)
					targetNode.put("target_app_did", appDid);
				map.put("context", targetNode);
			}

			String json = JsonUtil.serialize(map);
			Response<ResponseBody> response;

			response = this.connectionManager.getScriptingApi()
					.callScript(RequestBody.create(MediaType.parse("Content-Type, application/json"), json))
					.execute();
			authHelper.checkResponseWithRetry(response);
			return ResponseHelper.getValue(response, clazz);
		} catch (Exception e) {
			throw new HiveException(e.getLocalizedMessage());
		}
	}

	@Override
	public <T> CompletableFuture<T> uploadFile(String transactionId, Class<T> resultType) {
		return authHelper.checkValid().thenApplyAsync(aVoid -> {
			try {
				return uploadFileImpl(transactionId, resultType);
			} catch (HiveException e) {
				throw new CompletionException(e);
			}
		});
	}

	private <T> T uploadFileImpl(String transactionId, Class<T> resultType) throws HiveException {
		try {
			if(null != transactionId) {
				HttpURLConnection connection = this.connectionManager.openURLConnection("/scripting/run_script_upload/" + transactionId);
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
			} else {
				throw new HiveException("Can not get transaction id");
			}
		} catch (Exception e) {
			throw new HiveException(e.getLocalizedMessage());
		}
	}

	@Override
	public <T> CompletableFuture<T> downloadFile(String transactionId, Class<T> resultType) {
		return authHelper.checkValid().thenApplyAsync(aVoid -> {
			try {
				return downloadFileImpl(transactionId, resultType);
			} catch (HiveException e) {
				throw new CompletionException(e);
			}
		});
	}

	private <T> T downloadFileImpl(String transactionId, Class<T> resultType) throws HiveException {
		try {
			if(null != transactionId) {
				Response<ResponseBody> response;

				response = this.connectionManager.getScriptingApi()
						.callDownload(transactionId)
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
			} else {
				throw new HiveException("Can not get transaction id");
			}
		} catch (Exception e) {
			throw new HiveException(e.getLocalizedMessage());
		}
	}

	private <T> T callDownloadScriptImpl(String scriptName, JsonNode params, String appDid, Class<T> clazz) throws HiveException {
		try {
			Map<String, Object> map = new HashMap<>();
			map.put("name", scriptName);

			if(params!= null) map.put("params", params);

			ObjectNode targetNode = JsonNodeFactory.instance.objectNode();
			String ownerDid = this.authHelper.getOwnerDid();
			if (null != ownerDid) {
				targetNode.put("target_did", ownerDid);
				if (null != appDid)
					targetNode.put("target_app_did", appDid);
				map.put("context", targetNode);
			}

			String json = JsonUtil.serialize(map);
			Response<ResponseBody> response;

			response = this.connectionManager.getScriptingApi()
					.callScript(RequestBody.create(MediaType.parse("Content-Type, application/json"), json))
					.execute();
			int code = response.code();
			if(404 == code) {
				throw new FileNotFoundException(FileNotFoundException.EXCEPTION);
			}
			authHelper.checkResponseWithRetry(response);
			return ResponseHelper.getValue(response, clazz);
		} catch (Exception e) {
			throw new HiveException(e.getLocalizedMessage());
		}
	}
}