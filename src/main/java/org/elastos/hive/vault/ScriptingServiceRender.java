package org.elastos.hive.vault;

import com.fasterxml.jackson.databind.JsonNode;
import org.elastos.hive.ServiceEndpoint;
import org.elastos.hive.exception.InvalidParameterException;
import org.elastos.hive.network.ScriptingApi;
import org.elastos.hive.network.model.Condition;
import org.elastos.hive.network.model.Executable;
import org.elastos.hive.network.model.ScriptContext;
import org.elastos.hive.network.request.CallScriptRequestBody;
import org.elastos.hive.network.request.RegisterScriptRequestBody;
import org.elastos.hive.network.response.HiveResponseBody;
import org.elastos.hive.service.ScriptingService;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;

public class ScriptingServiceRender extends BaseServiceRender implements ScriptingService, HttpExceptionHandler {
	public ScriptingServiceRender(ServiceEndpoint serviceEndpoint) {
		super(serviceEndpoint);
	}

	@Override
	public CompletableFuture<Void> registerScript(String name, Executable executable,
													 boolean allowAnonymousUser, boolean allowAnonymousApp) {
		return registerScript(name, null, executable, allowAnonymousUser, allowAnonymousApp);
	}

	@Override
	public CompletableFuture<Void> registerScript(String name, Condition condition, Executable executable,
													 boolean allowAnonymousUser, boolean allowAnonymousApp) {
		return CompletableFuture.runAsync(()-> {
			try {
				HiveResponseBody.validateBody(
						getConnectionManager().getScriptingApi()
								.registerScript(new RegisterScriptRequestBody().setName(name)
										.setExecutable(executable)
										.setAllowAnonymousUser(allowAnonymousUser)
										.setAllowAnonymousApp(allowAnonymousApp)
										.setCondition(condition))
								.execute().body());
			} catch (Exception e) {
				throw new CompletionException(convertException(e));
			}
		});
	}

	public <T> CompletableFuture<T> callScript(String name, JsonNode params, String targetDid, String targetAppDid, Class<T> resultType) {
		return CompletableFuture.supplyAsync(()-> {
			try {
				return HiveResponseBody.getValue(HiveResponseBody.validateBodyStr(
						getConnectionManager().getScriptingApi()
								.callScript(new CallScriptRequestBody()
										.setName(name)
										.setContext(new ScriptContext()
												.setTargetDid(targetDid)
												.setTargetAppDid(targetAppDid))
										.setParams(HiveResponseBody.jsonNode2Map(params)))
								.execute()
				), resultType);
			} catch (Exception e) {
				throw new CompletionException(convertException(e));
			}
		});
	}

	public <T> CompletableFuture<T> callScriptUrl(String name, String params, String targetDid, String targetAppDid, Class<T> resultType) {
		return CompletableFuture.supplyAsync(()-> {
			try {
				return HiveResponseBody.getValue(HiveResponseBody.validateBodyStr(
						getConnectionManager().getScriptingApi()
								.callScriptUrl(targetDid, targetAppDid, name, params)
								.execute()
				), resultType);
			} catch (Exception e) {
				throw new CompletionException(convertException(e));
			}
		});
	}

	@Override
	public <T> CompletableFuture<T> uploadFile(String transactionId, Class<T> resultType) {
		return CompletableFuture.supplyAsync(()-> {
			try {
				if (transactionId == null)
					throw new InvalidParameterException("Invalid parameter transactionId.");

				return HiveResponseBody.getRequestStream(
						getConnectionManager().openConnection(ScriptingApi.API_SCRIPT_UPLOAD + "/" + transactionId),
						resultType);
			} catch (Exception e) {
				throw new CompletionException(convertException(e));
			}
		});
	}

	@Override
	public <T> CompletableFuture<T> downloadFile(String transactionId, Class<T> resultType) {
		return CompletableFuture.supplyAsync(()-> {
			try {
				if (transactionId == null)
					throw new InvalidParameterException("Invalid parameter transactionId.");

				return HiveResponseBody.getResponseStream(
						getConnectionManager().getScriptingApi()
								.callDownload(transactionId)
								.execute(),
						resultType);
			} catch (Exception e) {
				throw new CompletionException(convertException(e));
			}
		});
	}

	@Override
	public <T> CompletableFuture<T> callScript(String name, JsonNode params, String appDid, Class<T> resultType) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public CompletableFuture<Void> unregisterScript(String name) {
		// TODO Auto-generated method stub
		return null;
	}
}
