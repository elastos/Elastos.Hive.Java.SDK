package org.elastos.hive;

import com.fasterxml.jackson.databind.JsonNode;

import org.elastos.hive.service.ScriptingInvocationService;
import org.elastos.hive.vault.ScriptingServiceRender;

import java.util.concurrent.CompletableFuture;

public class ScriptRunner extends ServiceEndpoint implements ScriptingInvocationService {
	private ScriptingServiceRender scriptingServiceRender;

	public ScriptRunner(AppContext context, String providerAddress) {
		super(context, providerAddress);
		this.scriptingServiceRender = new ScriptingServiceRender(this);
	}

	@Override
	public <T> CompletableFuture<T> callScript(String name, JsonNode params,
											   String targetDid, String targetAppDid, Class<T> resultType) {
		return scriptingServiceRender.callScript(name, params, targetDid, targetAppDid, resultType);
	}

	/**
	 * Executes a previously registered server side script with a direct URL
	 * where the values can be passed as part of the query.
	 * Vault owner or external users are allowed to call scripts on someone's vault.
	 *
	 * @param name	   The call's script name
	 * @param params	 The parameters for the script.
	 * @param targetDid  The script owner's user did.
	 * @param targetAppDid The script owner's application did.
	 * @param resultType String, byte[], JsonNode, Reader, Write, OutputStream, Reader, InputStream
	 * @param <T>		String, byte[], JsonNode, Reader, Write, OutputStream, Reader, InputStream
	 * @return 			 Result for specific script type
	 */
	public <T> CompletableFuture<T> callScriptUrl(String name, String params,
												  String targetDid, String targetAppDid, Class<T> resultType) {
		return scriptingServiceRender.callScriptUrl(name, params, targetDid, targetAppDid, resultType);
	}

	@Override
	public <T> CompletableFuture<T> downloadFile(String transactionId, Class<T> resultType) {
		return scriptingServiceRender.downloadFile(transactionId, resultType);
	}

	@Override
	public <T> CompletableFuture<T> uploadFile(String transactionId, Class<T> resultType) {
		return scriptingServiceRender.uploadFile(transactionId, resultType);
	}
}
