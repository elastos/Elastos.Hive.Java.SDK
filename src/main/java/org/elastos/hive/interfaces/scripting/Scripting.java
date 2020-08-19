package org.elastos.hive.interfaces.scripting;

import org.elastos.hive.interfaces.scripting.conditions.Condition;
import org.elastos.hive.interfaces.scripting.executables.Executable;
import org.elastos.hive.interfaces.scripting.executables.ExecutionSequence;
import org.json.JSONObject;

import java.util.concurrent.CompletableFuture;

public interface Scripting {
    /**
     * Sends a HTTP call to register a sub-condition on the backend
     */
    CompletableFuture<Void> registerSubCondition(String conditionName, Condition condition);

    CompletableFuture<Void> setScript(String functionName, ExecutionSequence executionSequence);
    CompletableFuture<Void> setScript(String functionName, ExecutionSequence executionSequence, Condition accessCondition);

    CompletableFuture<JSONObject> call(String functionName);
    CompletableFuture<JSONObject> call(String functionName, JSONObject params);
}
