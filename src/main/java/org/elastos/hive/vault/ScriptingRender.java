package org.elastos.hive.vault;

import com.fasterxml.jackson.databind.JsonNode;
import org.elastos.hive.ScriptRunner;
import org.elastos.hive.Vault;
import org.elastos.hive.connection.ConnectionManager;
import org.elastos.hive.exception.HiveException;
import org.elastos.hive.network.model.Condition;
import org.elastos.hive.network.model.Executable;
import org.elastos.hive.network.request.RegisterScriptRequestBody;
import org.elastos.hive.network.response.RegisterScriptResponseBody;
import org.elastos.hive.network.response.ResponseBodyBase;
import org.elastos.hive.service.ScriptingService;
import retrofit2.Response;

import java.io.IOException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;

class ScriptingRender implements ScriptingService {
	private ConnectionManager connectionManager;
	private ScriptRunner scriptRunner;

	public ScriptingRender(Vault vault) {
		this.connectionManager = vault.getConnectionManager();
		this.scriptRunner = new ScriptRunner(vault.getAppContext(), vault.getProviderAddress(), vault.getUserDid(), null, null);
	}

	@Override
	public CompletableFuture<Boolean> registerScript(String name, Executable executable, boolean allowAnonymousUser, boolean allowAnonymousApp) {
		return CompletableFuture.supplyAsync(()->registerScriptImpl(name, null, executable, allowAnonymousUser, allowAnonymousApp));
	}

	private boolean registerScriptImpl(String name, Condition condition, Executable executable, boolean allowAnonymousUser, boolean allowAnonymousApp) {
		try {
			Response<RegisterScriptResponseBody> response = this.connectionManager.getScriptingApi()
					.registerScript(new RegisterScriptRequestBody().setName(name)
							.setExecutable(executable)
							.setAllowAnonymousUser(allowAnonymousUser)
							.setAllowAnonymousApp(allowAnonymousApp)
					.setCondition(condition))
					.execute();
			ResponseBodyBase.validateBody(response);
			return true;
		} catch (HiveException|IOException e) {
			throw new CompletionException(new HiveException(e.getMessage()));
		}
	}

	@Override
	public CompletableFuture<Boolean> registerScript(String name, Condition condition, Executable executable, boolean allowAnonymousUser, boolean allowAnonymousApp) {
		return CompletableFuture.supplyAsync(()->registerScriptImpl(name, condition, executable, allowAnonymousUser, allowAnonymousApp));
	}

	@Override
	public <T> CompletableFuture<T> callScript(String name, JsonNode params, String appDid, Class<T> resultType) {
		return null;
	}

	@Override
	public <T> CompletableFuture<T> callScriptUrl(String name, String params, String appDid, Class<T> resultType) {
		return null;
	}

	@Override
	public <T> CompletableFuture<T> uploadFile(String transactionId, Class<T> resultType) {
		return null;
	}

	@Override
	public <T> CompletableFuture<T> downloadFile(String transactionId, Class<T> resultType) {
		return null;
	}
}
