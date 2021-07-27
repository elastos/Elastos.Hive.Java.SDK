package org.elastos.hive.demo.sdk.scripting;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;

import org.elastos.hive.ScriptRunner;
import org.elastos.hive.demo.sdk.SdkContext;

import java.util.concurrent.CompletableFuture;

public class ScriptCaller {
    private ScriptRunner scriptRunner;

    private String ownerDid;
    private String appDid;

    public ScriptCaller(SdkContext sdkContext) {
        scriptRunner = sdkContext.newCallerScriptRunner();
        ownerDid = sdkContext.getUserDid();
        appDid = sdkContext.getAppDid();
    }

    public CompletableFuture<JsonNode> runScript() {
        ObjectNode params = JsonNodeFactory.instance.objectNode();
        params.put("author", "John");
        params.put("content", "message");
        return scriptRunner.callScript(ScriptConst.SCRIPT_NAME,
                params, ownerDid, appDid, JsonNode.class);
    }
}
