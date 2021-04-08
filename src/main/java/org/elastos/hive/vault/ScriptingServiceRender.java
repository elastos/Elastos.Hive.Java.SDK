package org.elastos.hive.vault;

import com.fasterxml.jackson.databind.JsonNode;
import org.elastos.hive.ScriptRunner;
import org.elastos.hive.ServiceEndpoint;
import org.elastos.hive.network.model.Condition;
import org.elastos.hive.network.model.Executable;
import org.elastos.hive.network.request.RegisterScriptRequestBody;
import org.elastos.hive.network.response.HiveResponseBody;
import org.elastos.hive.service.ScriptingService;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;

class ScriptingServiceRender extends HiveVaultRender implements ScriptingService, HttpExceptionHandler {
	private ScriptRunner scriptRunner;

	public ScriptingServiceRender(ServiceEndpoint serviceEndpoint) {
		super(serviceEndpoint);
		this.scriptRunner = new ScriptRunner(getServiceEndpoint().getAppContext(),
				getServiceEndpoint().getProviderAddress(),
				getServiceEndpoint().getTargetDid(),
				getServiceEndpoint().getAppDid());
	}

	@Override
	public CompletableFuture<Boolean> registerScript(String name, Executable executable,
													 boolean allowAnonymousUser, boolean allowAnonymousApp) {
		return registerScript(name, null, executable, allowAnonymousUser, allowAnonymousApp);
	}

	@Override
	public CompletableFuture<Boolean> registerScript(String name, Condition condition, Executable executable,
													 boolean allowAnonymousUser, boolean allowAnonymousApp) {
		return CompletableFuture.supplyAsync(()-> {
			try {
				HiveResponseBody.validateBody(
						getConnectionManager().getScriptingApi()
								.registerScript(new RegisterScriptRequestBody().setName(name)
										.setExecutable(executable)
										.setAllowAnonymousUser(allowAnonymousUser)
										.setAllowAnonymousApp(allowAnonymousApp)
										.setCondition(condition))
								.execute().body());
				return true;
			} catch (Exception e) {
				throw new CompletionException(convertException(e));
			}
		});
	}

	@Override
	public <T> CompletableFuture<T> callScript(String name, JsonNode params, String appDid, Class<T> resultType) {
		return CompletableFuture.supplyAsync(()-> {
			try {
				return scriptRunner.callScript(name, params, appDid, resultType);
			} catch (Exception e) {
				throw new CompletionException(convertException(e));
			}
		});
	}

	@Override
	public <T> CompletableFuture<T> callScriptUrl(String name, String params, String appDid, Class<T> resultType) {
		return CompletableFuture.supplyAsync(()-> {
			try {
				return scriptRunner.callScriptUrl(name, params, appDid, resultType);
			} catch (Exception e) {
				throw new CompletionException(convertException(e));
			}
		});
	}

	@Override
	public <T> CompletableFuture<T> uploadFile(String transactionId, Class<T> resultType) {
		return CompletableFuture.supplyAsync(()-> {
			try {
				return scriptRunner.uploadFile(transactionId, resultType);
			} catch (Exception e) {
				throw new CompletionException(convertException(e));
			}
		});
	}

	@Override
	public <T> CompletableFuture<T> downloadFile(String transactionId, Class<T> resultType) {
		return CompletableFuture.supplyAsync(()-> {
			try {
				return scriptRunner.downloadFile(transactionId, resultType);
			} catch (Exception e) {
				throw new CompletionException(convertException(e));
			}
		});
	}
}
