package org.elastos.hive;

import java.io.File;
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

class ScriptClient implements Scripting {
	private AuthHelper authHelper;
	private ConnectionManager connectionManager;

	ScriptClient(AuthHelper authHelper) {
		this.authHelper = authHelper;
		this.connectionManager = authHelper.getConnectionManager();
	}

	@Override
	public CompletableFuture<Boolean> registerScript(String name, Executable executable) {
		return this.registerScript(name, null, executable);
	}

	@Override
	public CompletableFuture<Boolean> registerScript(String name, Condition accessCondition, Executable executable) {
		return authHelper.checkValid()
				.thenCompose(result -> registerScriptImp(name, accessCondition, executable));
	}

	private CompletableFuture<Boolean> registerScriptImp(String name, Condition accessCondition, Executable executable) {
		return CompletableFuture.supplyAsync(() -> {
			try {
				Map<String, Object> map = new HashMap<>();
				map.put("name", name);
				map.put("executable", executable);
				if (accessCondition != null)
					map.put("condition", accessCondition);

				String json = JsonUtil.getJsonFromObject(map);

				Response<ResponseBody> response = this.connectionManager.getVaultApi()
						.registerScript(RequestBody.create(MediaType.parse("Content-Type, application/json"), json))
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
	public <T> CompletableFuture<T> call(String scriptName, Class<T> clazz) {
		return authHelper.checkValid()
				.thenCompose(result -> callImp(scriptName, null,null, clazz));
	}

	@Override
	public <T> CompletableFuture<T> call(String scriptName, JsonNode params, Class<T> clazz) {
		return authHelper.checkValid()
				.thenCompose(result -> callImp(scriptName, params, null, clazz));
	}

	@Override
	public <T> CompletableFuture<T> call(String scriptName, String appDid, Class<T> resultType) {
		return this.call(scriptName, null, appDid, resultType);
	}

	@Override
	public <T> CompletableFuture<T> call(String scriptName, JsonNode params, String appDid, Class<T> resultType) {
		return authHelper.checkValid()
				.thenCompose(result -> callImp(scriptName, params, appDid, resultType));
	}

	private <T> CompletableFuture<T> callImp(String scriptName, JsonNode params, String appDid, Class<T> clazz) {
		return CompletableFuture.supplyAsync(() -> {
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

				String json = JsonUtil.getJsonFromObject(map);

				Response response = this.connectionManager.getVaultApi()
						.callScript(RequestBody.create(MediaType.parse("Content-Type, application/json"), json))
						.execute();
				authHelper.checkResponseCode(response);
				return ResponseHelper.getValue(response, clazz);
			} catch (Exception e) {
				HiveException exception = new HiveException(e.getLocalizedMessage());
				throw new CompletionException(exception);
			}
		});
	}

	@Override
	public <T> CompletableFuture<T> call(String name, JsonNode params, Type type, Class<T> resultType) {
		return authHelper.checkValid()
				.thenCompose(result -> {
					if (type == Type.UPLOAD) {
						return fileUploadImp(name, params, resultType);
					} else if (type == Type.DOWNLOAD) {
						return fileDownloadImp(name, params, resultType);
					} else if (type == Type.PROPERTIES) {
						return callImp(name, params, null, resultType);
					}
					return null;
				});
	}

	private <T> CompletableFuture<T> fileUploadImp(String filePath, JsonNode params, Class<T> resultType) {
		return CompletableFuture.supplyAsync(() -> {
			try {
				String json = params.toString();
				File file = new File(filePath);
				RequestBody requestFile =
						RequestBody.create(MediaType.parse("multipart/form-data"), file);
				MultipartBody.Part body = MultipartBody.Part.createFormData("data", file.getName(), requestFile);

				RequestBody metadata =
						RequestBody.create(
								MediaType.parse("application/json"), json);

				Response response = this.connectionManager.getVaultApi()
						.callScript(body, metadata)
						.execute();
				authHelper.checkResponseCode(response);
				return (T) ResponseHelper.toString(response);
			} catch (Exception e) {
				e.printStackTrace();
				throw new CompletionException(e);
			}
		});
	}

	private <T> CompletableFuture<T> fileDownloadImp(String scriptName, JsonNode params, Class<T> resultType) {

		return CompletableFuture.supplyAsync(() -> {
			try {
				Map<String, Object> map = new HashMap<>();
				map.put("name", scriptName);
				if (params != null)
					map.put("params", params);

				String json = JsonUtil.getJsonFromObject(map);

				Response response = this.connectionManager.getVaultApi()
						.callScript(RequestBody.create(MediaType.parse("Content-Type, application/json"), json))
						.execute();

				if (response == null)
					throw new HiveException(HiveException.ERROR);

				authHelper.checkResponseCode(response);
				if (resultType.isAssignableFrom(Reader.class)) {
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
}