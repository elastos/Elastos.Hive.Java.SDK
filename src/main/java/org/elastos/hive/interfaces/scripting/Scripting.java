package org.elastos.hive.interfaces.scripting;

import org.elastos.hive.interfaces.scripting.conditions.Condition;
import org.elastos.hive.interfaces.scripting.executables.Executable;
import org.elastos.hive.interfaces.scripting.executables.ExecutionSequence;
import org.json.JSONObject;

import java.util.concurrent.CompletableFuture;

public interface Scripting {
    /**
     * Registers a sub-condition on the backend. Sub conditions can be referenced from the client side, by the vault owner,
     * while registering scripts using Scripting.setScript().
     */
    CompletableFuture<Void> registerSubCondition(String conditionName, Condition condition);

    /**
     * Lets the vault owner register a script on his vault for a given app. The script is built on the client side, then
     * serialized and stored on the hive back-end. Later on, anyone, including the vault owner or external users, can
     * use Scripting.call() to execute one of those scripts and get results/data.
     */
    CompletableFuture<Void> setScript(String functionName, ExecutionSequence executionSequence);
    CompletableFuture<Void> setScript(String functionName, ExecutionSequence executionSequence, Condition accessCondition);

    /**
     * Executes a previously registered server side script using Scripting.setScript(). Vault owner or external users are
     * allowed to call scripts on someone's vault.
     *
     * Call parameters (params field) are meant to be used by scripts on the server side, for example as injected parameters
     * to mongo queries. Ex: if "params" contains a field "name":"someone", then the called script is able to reference this parameter
     * using "$params.name".
     */
    CompletableFuture<JSONObject> call(String functionName);
    CompletableFuture<JSONObject> call(String functionName, JSONObject params);
}
