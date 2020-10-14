package org.elastos.hive;

import java.util.concurrent.CompletableFuture;

import org.elastos.hive.exception.HiveException;
import org.elastos.hive.scripting.Condition;
import org.elastos.hive.scripting.Executable;

import com.fasterxml.jackson.databind.JsonNode;

public interface Scripting {
    /**
     * Lets the vault owner register a script on his vault for a given app. The script is built on the client side, then
     * serialized and stored on the hive back-end. Later on, anyone, including the vault owner or external users, can
     * use Scripting.call() to execute one of those scripts and get results/data.
     */
    CompletableFuture<Boolean> registerScript(String name, Executable executable) throws HiveException;
    CompletableFuture<Boolean> registerScript(String name, Condition condition, Executable executable) throws HiveException;

    /**
     * Executes a previously registered server side script using Scripting.setScript(). Vault owner or external users are
     * allowed to call scripts on someone's vault.
     *
     * Call parameters (params field) are meant to be used by scripts on the server side, for example as injected parameters
     * to mongo queries. Ex: if "params" contains a field "name":"someone", then the called script is able to reference this parameter
     * using "$params.name".
     *
     * T: String, byte[], JsonNode, Reader
     */
    <T> CompletableFuture<T> call(String scriptName, Class<T> resultType) throws HiveException;
    <T> CompletableFuture<T> call(String scriptName, JsonNode params, Class<T> resultType) throws HiveException;

    /**
     * Run a script to upload a file.
     * The upload works a bit differently compared to other types of executable queries because you will need to pass the json data
     * as "metaddata" as a multipart-form data along with the actual file passed as "data" form field
     *
     * Call parameters (params field) are meant to be used by scripts on the server side, for example as injected parameters
     * to mongo queries. Ex: if "params" contains a field "name":"someone", then the called script is able to reference this parameter
     * using "$params.name".
     */
    CompletableFuture<Void> call(String file, JsonNode params) throws HiveException;
}
