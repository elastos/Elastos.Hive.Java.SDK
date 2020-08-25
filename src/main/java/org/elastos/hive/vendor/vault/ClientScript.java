package org.elastos.hive.vendor.vault;

import org.elastos.hive.interfaces.scripting.Scripting;
import org.elastos.hive.interfaces.scripting.conditions.Condition;
import org.elastos.hive.interfaces.scripting.executables.ExecutionSequence;
import org.json.JSONObject;

import java.util.concurrent.CompletableFuture;

class ClientScript implements Scripting {

    private VaultAuthHelper authHelper;

    ClientScript(VaultAuthHelper authHelper) {
        this.authHelper = authHelper;
    }

    @Override
    public CompletableFuture<Void> registerSubCondition(String conditionName, Condition condition) {
        JSONObject httpPayload = new JSONObject();

        httpPayload.put("conditionName", conditionName);
        httpPayload.put("condition", condition.toJSON());

        // TODO: Call the HTTP method to register the sub condition

        return null;
    }

    @Override
    public CompletableFuture<Void> setScript(String functionName, ExecutionSequence executionSequence) {
        return this.setScript(functionName, executionSequence, null);
    }

    @Override
    public CompletableFuture<Void> setScript(String functionName, ExecutionSequence executionSequence, Condition accessCondition) {
        JSONObject httpPayload = new JSONObject();

        httpPayload.put("scriptName", functionName);
        httpPayload.put("executionSequence", executionSequence.toJSON());

        if (accessCondition != null)
            httpPayload.put("accessCondition", accessCondition.toJSON());

        // TODO: Call the HTTP method to set the script

        return null;
    }

    @Override
    public CompletableFuture<JSONObject> call(String functionName) {
        return this.call(functionName, null);
    }

    @Override
    public CompletableFuture<JSONObject> call(String functionName, JSONObject params) {
        JSONObject httpPayload = new JSONObject();

        httpPayload.put("scriptName", functionName);

        if (params != null)
            httpPayload.put("params", params);

        // TODO: Call the HTTP method to call the script

        return null;
    }

}
