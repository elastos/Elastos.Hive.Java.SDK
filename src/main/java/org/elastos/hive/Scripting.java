package org.elastos.hive;

import java.util.concurrent.CompletableFuture;

import org.elastos.hive.scripting.Condition;
import org.elastos.hive.scripting.Executable;

import com.fasterxml.jackson.databind.JsonNode;

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
     * allowed to call scripts on someone's vault.
     * <p>
     * Call parameters (params field) are meant to be used by scripts on the server side, for example as injected parameters
     * to mongo queries. Ex: if "params" contains a field "name":"someone", then the called script is able to reference this parameter
     * using "$params.name".
     * <p>
     * T: String, byte[], JsonNode, Reader
     */
    <T> CompletableFuture<T> call(String scriptName, Class<T> resultType);

    <T> CompletableFuture<T> call(String scriptName, JsonNode params, Class<T> resultType);

    /**
     * Executes a previously registered server side script using Scripting.setScript(). Vault owner or external users are
     * allowed to call scripts on someone's vault.
     *
     * @param scriptName the call's script name
     * @param appDid     app did is an optional parameter
     * @param resultType String, byte[], JsonNode, Reader
     */
    <T> CompletableFuture<T> call(String scriptName, String appDid, Class<T> resultType);

    /**
     * Executes a previously registered server side script using Scripting.setScript(). Vault owner or external users are
     * allowed to call scripts on someone's vault.
     *
     * @param scriptName the call's script name
     * @param params     Call parameters (params field) are meant to be used by scripts on the server side
     * @param appDid     app did is an optional parameter,
     * @param resultType String, byte[], JsonNode, Reader
     */
    <T> CompletableFuture<T> call(String scriptName, JsonNode params, String appDid, Class<T> resultType);


    enum Type {
        UPLOAD,
        DOWNLOAD,
        PROPERTIES
    }

    /**
     * Run a file script: upload, download, hash, properties
     *
     * @param name
     * @param params
     * @param type       Scripting file type
     * @param resultType upload(String, byte[], JsonNode, Reader), download(Reader or InputStream), hash/properties(String, byte[], JsonNode, Reader)
     * @param <T>        upload(String, byte[], JsonNode, Reader), download(Reader or InputStream), hash/properties(String, byte[], JsonNode, Reader)
     * @return
     */
    <T> CompletableFuture<T> call(String name, JsonNode params, Type type, Class<T> resultType);
}
