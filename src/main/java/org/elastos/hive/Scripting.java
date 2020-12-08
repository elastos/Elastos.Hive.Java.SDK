package org.elastos.hive;

import com.fasterxml.jackson.databind.JsonNode;

import java.util.concurrent.CompletableFuture;

import org.elastos.hive.scripting.Condition;
import org.elastos.hive.scripting.Executable;

public interface Scripting {

	/**
	 * Lets the vault owner register a script on his vault for a given app. The script is built on the client side, then
	 * serialized and stored on the hive back-end. Later on, anyone, including the vault owner or external users, can
	 * use Scripting.call() to execute one of those scripts and get results/data.
	 */
	CompletableFuture<Boolean> registerScript(String name, Executable executable);

	CompletableFuture<Boolean> registerScript(String name, Condition condition, Executable executable);

	/**
	 * Executes a previously registered server side script using Scripting.setScript(). Vault owner or external users are
	 *
	 * @param name       the call's script name
	 * @param resultType String, byte[], JsonNode, Reader
	 * @param <T> String, byte[], JsonNode, Reader
	 * @return
	 */
	<T> CompletableFuture<T> callScript(String name, JsonNode params, String appDid, Class<T> resultType);

	/**
	 * Run a script to upload a file NOTE: The upload works a bit differently compared to other
	 * types of executable queries because there are two steps to this executable. First, register a
	 * script on the vault, then you call this api to actually upload the file
	 * @param transactionId
	 * @param resultType Reader or InputStream class
	 * @param <T> Write
	 * @return
	 */
	<T> CompletableFuture<T> callToUploadFile(String transactionId, Class<T> resultType);

	/**
	 * Run a script to download a file NOTE: The download works a bit differently compared to other
	 * types of executable queries because there are two steps to this executable. First, register a
	 * script on the vault, then you call this api to actually upload the file
	 * @param resultType Reader or InputStream class
	 * @param <T> Reader
	 * @return
	 */
	<T> CompletableFuture<T> callToDownloadFile(String transactionId, Class<T> resultType);
}
