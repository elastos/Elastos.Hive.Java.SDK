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
	 * types of executable queries because there are two steps to this executable. First, you run
	 * the script to get a transaction ID and then secondly, you call a second API endpoint to actually
	 * upload the file related to that transaction ID
	 * @param resultType Write or OutputStream class
	 * @param <T> Write, OutputStream
	 * @return
	 */
	<T> CompletableFuture<T> callToUploadFile(String name, JsonNode params, String appDid, Class<T> resultType);

	/**
	 * Run a script to download a file NOTE: The download works a bit differently compared to other
	 * types of executable queries because there are two steps to this executable. First, you run the
	 * script to get a transaction ID and then secondly, you call a second API endpoint to actually
	 * download the file related to that transaction ID
	 * @param resultType Reader or InputStream class
	 * @param <T> Reader or InputStream
	 * @return
	 */
	<T> CompletableFuture<T> callToDownloadFile(String name, JsonNode params, String appDid, Class<T> resultType);
}
