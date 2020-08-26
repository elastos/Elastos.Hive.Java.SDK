package org.elastos.hive.vendor.vault;

import java.io.Reader;
import java.util.concurrent.CompletableFuture;

import org.elastos.hive.exception.HiveException;
import org.elastos.hive.interfaces.Scripting;
import org.elastos.hive.scripting.Condition;
import org.elastos.hive.scripting.Executable;
import org.json.JSONObject;

import com.fasterxml.jackson.databind.JsonNode;

// TODO: change org.json to Jackson

class ClientScript implements Scripting {

    private VaultAuthHelper authHelper;

    ClientScript(VaultAuthHelper authHelper) {
        this.authHelper = authHelper;
    }

    @Override
    public CompletableFuture<Boolean> registerCondition(String name, Condition condition) throws HiveException {
        JSONObject httpPayload = new JSONObject();

        httpPayload.put("conditionName", name);
        httpPayload.put("condition", condition);

        // TODO: Call the HTTP method to register the sub condition

        return null;
    }

    @Override
    public CompletableFuture<Boolean> registerScript(String name, Executable executable) throws HiveException {
        return this.registerScript(name, null, executable);
    }

    @Override
    public CompletableFuture<Boolean> registerScript(String name, Condition accessCondition, Executable executable) throws HiveException {
        JSONObject httpPayload = new JSONObject();

        httpPayload.put("scriptName", name);

        if (accessCondition != null)
            httpPayload.put("accessCondition", accessCondition);

        httpPayload.put("executable", executable);


        // TODO: Call the HTTP method to set the script

        return null;
    }

    @Override
    public CompletableFuture<Reader> call(String scriptName) {
        return this.call(scriptName, null);
    }

    @Override
    public CompletableFuture<Reader> call(String scriptName, JsonNode params) {
        JSONObject httpPayload = new JSONObject();

        httpPayload.put("scriptName", scriptName);

        if (params != null)
            httpPayload.put("params", params);

        // TODO: Call the HTTP method to call the script

        return null;
    }

}
