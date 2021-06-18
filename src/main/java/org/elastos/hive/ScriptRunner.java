package org.elastos.hive;

import com.fasterxml.jackson.databind.JsonNode;

import org.elastos.hive.exception.HiveException;
import org.elastos.hive.service.ScriptingInvocationService;
import org.elastos.hive.vault.scripting.ScriptingController;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;

public class ScriptRunner extends ServiceEndpoint implements ScriptingInvocationService {
	private ScriptingController controller;

	public ScriptRunner(AppContext context, String providerAddress) {
		super(context, providerAddress);
		this.controller = new ScriptingController(this);
	}

	@Override
	public <T> CompletableFuture<T> callScript(String name,
											JsonNode params,
											String targetDid,
											String targetAppDid,
											Class<T> resultType) {

		return CompletableFuture.supplyAsync(()-> {
			try {
				return controller.callScript(name, params, targetDid, targetAppDid, resultType);
			} catch (HiveException | RuntimeException e) {
				throw new CompletionException(e);
			}
		});
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
	public <T> CompletableFuture<T> callScriptUrl(String name,
											String params,
											String targetDid,
											String targetAppDid,
											Class<T> resultType) {

		return CompletableFuture.supplyAsync(()-> {
			try {
				return controller.callScriptUrl(name, params, targetDid, targetAppDid, resultType);
			} catch (HiveException | RuntimeException e) {
				throw new CompletionException(e);
			}
		});
	}

	@Override
	public <T> CompletableFuture<T> downloadFile(String transactionId, Class<T> resultType) {
		return CompletableFuture.supplyAsync(()-> {
			try {
				if (transactionId == null)
					throw new IllegalArgumentException("Empty parameter transactionId.");

				if (resultType == null)
					throw new IllegalArgumentException("Unkown result type");

				return controller.downloadFile(transactionId, resultType);
			} catch (HiveException | RuntimeException e) {
				throw new CompletionException(e);
			}
		});
	}

	@Override
	public <T> CompletableFuture<T> uploadFile(String transactionId, Class<T> resultType) {
		return CompletableFuture.supplyAsync(()-> {
			try {
				if (transactionId == null)
					throw new IllegalArgumentException("Empty parameter transactionId.");

				if (resultType == null)
					throw new IllegalArgumentException("Unkown result type");

				return controller.uploadFile(transactionId, resultType);
			} catch (HiveException | RuntimeException e) {
				throw new CompletionException(e);
			}
		});
	}
}
