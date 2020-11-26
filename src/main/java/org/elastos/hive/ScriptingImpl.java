package org.elastos.hive;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;

import org.elastos.hive.connection.ConnectionManager;
import org.elastos.hive.exception.HiveException;
import org.elastos.hive.scripting.CallConfig;
import org.elastos.hive.scripting.Condition;
import org.elastos.hive.scripting.DownloadCallConfig;
import org.elastos.hive.scripting.Executable;
import org.elastos.hive.scripting.GeneralCallConfig;
import org.elastos.hive.scripting.UploadCallConfig;
import org.elastos.hive.utils.JsonUtil;
import org.elastos.hive.utils.ResponseHelper;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
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
		return authHelper.checkValid().thenApply(aVoid -> {
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
	public <T> CompletableFuture<T> callScript(String name, CallConfig config, Class<T> resultType) {
		return authHelper.checkValid().thenApply(aVoid -> {
			try {
				if (config instanceof UploadCallConfig) {
					return uploadFileImpl(name, ((UploadCallConfig) config), resultType);
				} else if (config instanceof DownloadCallConfig) {
					return downloadFileImpl(name, ((DownloadCallConfig) config), resultType);
				} else if (config instanceof GeneralCallConfig) {
					return callScriptImpl(name, ((GeneralCallConfig) config), resultType);
				} else if(null == config) {
					callScriptImpl(name, null, resultType);
				}
				return null;
			} catch (HiveException e) {
				throw new CompletionException(e);
			}
		});
	}

	private <T> T callScriptImpl(String scriptName, GeneralCallConfig config, Class<T> clazz) throws HiveException {
		try {
			Map<String, Object> map = new HashMap<>();
			map.put("name", scriptName);

			if (null != config) {
				JsonNode params = config.params();
				if(params!= null) map.put("params", params);
			}

			ObjectNode targetNode = JsonNodeFactory.instance.objectNode();
			String ownerDid = this.authHelper.getOwnerDid();
			if (null != ownerDid) {
				targetNode.put("target_did", ownerDid);
				String appDid = (config==null)?null:config.appDid();
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

	private <T> T uploadFileImpl(String scriptName, UploadCallConfig config, Class<T> resultType) throws HiveException {
		try {
			Map<String, Object> map = new HashMap<>();
			map.put("name", scriptName);
			JsonNode params = config.params();
			if (params != null)
				map.put("params", params);

			String json = JsonUtil.serialize(map);

			File file = new File(config.filePath());
			RequestBody requestFile =
					RequestBody.create(MediaType.parse("multipart/form-data"), file);
			MultipartBody.Part body = MultipartBody.Part.createFormData("data", file.getName(), requestFile);

			RequestBody metadata =
					RequestBody.create(
							MediaType.parse("application/json"), json);

			Response<ResponseBody> response = this.connectionManager.getScriptingApi()
					.callScript(body, metadata)
					.execute();
			authHelper.checkResponseWithRetry(response);
			return resultType.cast(ResponseHelper.toString(response));
		} catch (Exception e) {
			throw new HiveException(e.getLocalizedMessage());
		}
	}

	private <T> T downloadFileImpl(String scriptName, DownloadCallConfig config, Class<T> resultType) throws HiveException {
		try {
			Map<String, Object> map = new HashMap<>();
			map.put("name", scriptName);
			JsonNode params = config.params();
			if (params != null)
				map.put("params", params);

			String json = JsonUtil.serialize(map);
			Response<ResponseBody> response;

			response = this.connectionManager.getScriptingApi()
					.callScript(RequestBody.create(MediaType.parse("Content-Type, application/json"), json))
					.execute();

			if (response == null)
				throw new HiveException(HiveException.ERROR);

			authHelper.checkResponseWithRetry(response);
			if (resultType.isAssignableFrom(Reader.class)) {
				Reader reader = ResponseHelper.getToReader(response);
				return resultType.cast(reader);
			}
			if (resultType.isAssignableFrom(InputStream.class)) {
				InputStream inputStream = ResponseHelper.getInputStream(response);
				return resultType.cast(inputStream);
			}

			throw new HiveException("No support result Type");
		} catch (Exception e) {
			throw new HiveException(e.getLocalizedMessage());
		}
	}
}