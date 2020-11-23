package org.elastos.hive;

import java.util.concurrent.CompletableFuture;

import org.elastos.hive.scripting.CallConfig;
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
	 * @param config
	 * @see CallConfig
	 * @param resultType String, byte[], JsonNode, Reader
	 * @param <T> String, byte[], JsonNode, Reader
	 * @return
	 */
	<T> CompletableFuture<T> callScript(String name, CallConfig config, Class<T> resultType);
}
