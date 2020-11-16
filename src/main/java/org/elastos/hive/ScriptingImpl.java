package org.elastos.hive;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;

import org.elastos.hive.connection.ConnectionManager;
import org.elastos.hive.exception.HiveException;
import org.elastos.hive.scripting.Condition;
import org.elastos.hive.scripting.Executable;
import org.elastos.hive.utils.JsonUtil;
import org.elastos.hive.utils.ResponseHelper;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;

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
				e.printStackTrace();
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
			response = this.connectionManager.getVaultApi()
					.registerScript(RequestBody.create(MediaType.parse("Content-Type, application/json"), json))
					.execute();

			authHelper.checkResponseWithRetry(response);
			return true;
		} catch (IOException e) {
			throw new HiveException(new HiveException(e.getMessage()));
		}
	}

	@Override
	public <T> CompletableFuture<T> call(String scriptName, Class<T> resultType) {
		return this.call(scriptName, null, (String)null, resultType);
	}

	@Override
	public <T> CompletableFuture<T> call(String scriptName, JsonNode params, Class<T> resultType) {
		return this.call(scriptName, params, (String)null, resultType);
	}

	@Override
	public <T> CompletableFuture<T> call(String scriptName, String appDid, Class<T> resultType) {
		return this.call(scriptName, null, appDid, resultType);
	}

	@Override
	public <T> CompletableFuture<T> call(String scriptName, JsonNode params, String appDid, Class<T> resultType) {
		return authHelper.checkValid().thenApply(aVoid -> {
			try {
				return callScriptImpl(scriptName, params, appDid, resultType);
			} catch (HiveException e) {
				e.printStackTrace();
				throw new CompletionException(e);
			}
		});
	}

	private <T> T callScriptImpl(String scriptName, JsonNode params, String appDid, Class<T> clazz) throws HiveException {
		try {
			Map<String, Object> map = new HashMap<>();
			map.put("name", scriptName);
			if (params != null)
				map.put("params", params);

			ObjectNode targetNode = JsonNodeFactory.instance.objectNode();
			String ownerDid = this.authHelper.getOwnerDid();
			if(null != ownerDid) {
				targetNode.put("target_did", ownerDid);
				if (null != appDid)
					targetNode.put("target_app_did", appDid);
				map.put("context", targetNode);
			}

			String json = JsonUtil.serialize(map);
			Response<ResponseBody> response;

			response = this.connectionManager.getVaultApi()
					.callScript(RequestBody.create(MediaType.parse("Content-Type, application/json"), json))
					.execute();
			authHelper.checkResponseWithRetry(response);
			return ResponseHelper.getValue(response, clazz);
		} catch (Exception e) {
			throw new HiveException(e.getLocalizedMessage());
		}
	}

	@Override
	public <T> CompletableFuture<T> call(String name, JsonNode params, Type type, Class<T> resultType) {
		return authHelper.checkValid().thenApply(aVoid -> {
			try {
				if (type == Type.UPLOAD) {
					return uploadFileImpl(name, params, resultType);
				} else if (type == Type.DOWNLOAD) {
					return downloadFileImpl(name, params, resultType);
				} else if (type == Type.PROPERTIES) {
					return callScriptImpl(name, params, (String)null, resultType);
				}
				return null;
			} catch (HiveException e) {
				e.printStackTrace();
				throw new CompletionException(e);
			}
		});
	}

	private <T> T uploadFileImpl(String filePath, JsonNode params, Class<T> resultType) throws HiveException {
		try {
			String json = params.toString();
			File file = new File(filePath);
			RequestBody requestFile =
					RequestBody.create(MediaType.parse("multipart/form-data"), file);
			MultipartBody.Part body = MultipartBody.Part.createFormData("data", file.getName(), requestFile);

			RequestBody metadata =
					RequestBody.create(
							MediaType.parse("application/json"), json);

			Response<ResponseBody> response = this.connectionManager.getVaultApi()
					.callScript(body, metadata)
					.execute();
			authHelper.checkResponseWithRetry(response);
			return resultType.cast(ResponseHelper.toString(response));
		} catch (Exception e) {
			e.printStackTrace();
			throw new HiveException(e.getMessage());
		}
	}

	private <T> T downloadFileImpl(String scriptName, JsonNode params, Class<T> resultType) throws HiveException {
		try {
			Map<String, Object> map = new HashMap<>();

			map.put("name", scriptName);
			if (params != null)
				map.put("params", params);

			String json = JsonUtil.serialize(map);
			Response<ResponseBody> response;

			response = this.connectionManager.getVaultApi()
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