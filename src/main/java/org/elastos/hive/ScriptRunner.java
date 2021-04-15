package org.elastos.hive;

import com.fasterxml.jackson.databind.JsonNode;
import org.elastos.hive.vault.ScriptingServiceRender;

import java.util.concurrent.CompletableFuture;

public class ScriptRunner extends ServiceEndpoint {
	private ScriptingServiceRender scriptingServiceRender;

	public ScriptRunner(AppContext context, String providerAddress) {
		super(context, providerAddress);
		this.scriptingServiceRender = new ScriptingServiceRender(this);
	}

	/**
	 * Executes a previously registered server side script using Scripting.setScript().
	 * Vault owner or external users are allowed to call scripts on someone's vault.
	 *
	 * @param name       The call's script name
	 * @param resultType String, byte[], JsonNode, Reader
	 * @param <T> 		 String, byte[], JsonNode, Reader
	 * @return 			 Result for specific script type
	 */
	public <T> CompletableFuture<T> callScript(String name, JsonNode params,
											   String targetDid, String targetAppDid, Class<T> resultType) {
		return scriptingServiceRender.callScript(name, params, targetDid, targetAppDid, resultType);
	}

	/**
	 * Executes a previously registered server side script with a direct URL
	 * where the values can be passed as part of the query.
	 * Vault owner or external users are allowed to call scripts on someone's vault.
	 *
	 * @param name       The call's script name
	 * @param resultType String, byte[], JsonNode, Reader, Write, OutputStream, Reader, InputStream
	 * @param <T>        String, byte[], JsonNode, Reader, Write, OutputStream, Reader, InputStream
	 * @return 			 Result for specific script type
	 */
	public <T> CompletableFuture<T> callScriptUrl(String name, String params,
												  String targetDid, String targetAppDid, Class<T> resultType) {
		return scriptingServiceRender.callScriptUrl(name, params, targetDid, targetAppDid, resultType);
	}

	/**
	 * Run a script to upload a file NOTE: The upload works a bit differently compared to other
	 * types of executable queries because there are two steps to this executable. First, register a
	 * script on the vault, then you call this api to actually upload the file
	 * @param transactionId Transaction id
	 * @param resultType    Write, OutputStream
	 * @param <T>           Write, OutputStream
	 * @return 				Write, OutputStream
	 */
	public <T> CompletableFuture<T> downloadFile(String transactionId, Class<T> resultType) {
		return scriptingServiceRender.downloadFile(transactionId, resultType);
	}

	/**
	 * Run a script to download a file NOTE: The download works a bit differently compared to other
	 * types of executable queries because there are two steps to this executable. First, register a
	 * script on the vault, then you call this api to actually upload the file
	 * @param resultType Reader or InputStream class
	 * @param <T>        Reader, InputStream
	 * @return			 Reader, InputStream
	 */
	public <T> CompletableFuture<T> uploadFile(String transactionId, Class<T> resultType) {
		return scriptingServiceRender.uploadFile(transactionId, resultType);
	}
}
