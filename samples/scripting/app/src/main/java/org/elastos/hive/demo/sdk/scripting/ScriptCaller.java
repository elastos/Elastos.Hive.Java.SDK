package org.elastos.hive.demo.sdk.scripting;

import com.fasterxml.jackson.databind.JsonNode;

import org.elastos.hive.ScriptRunner;
import org.elastos.hive.demo.sdk.SdkContext;
import org.elastos.hive.network.model.KeyValueDict;
import org.elastos.hive.network.response.HiveResponseBody;

import java.util.concurrent.CompletableFuture;

public class ScriptCaller {
    private final SdkContext sdkContext;

    private ScriptRunner scriptRunner;

    private String ownerDid;
    private String callDid;
    private String appDid;

    public ScriptCaller(SdkContext sdkContext) {
        this.sdkContext = sdkContext;
        scriptRunner = sdkContext.newCallerScriptRunner();
        ownerDid = sdkContext.getOwnerDid();
        callDid = sdkContext.getCallerDid();
        appDid = sdkContext.getAppId();
    }

    public CompletableFuture<JsonNode> runScript() {
        return scriptRunner.callScript(ScriptConst.SCRIPT_NAME,
                HiveResponseBody.map2JsonNode(
                        new KeyValueDict().putKv("author", "John").putKv("content", "message")),
                ownerDid, appDid, JsonNode.class);
    }
}
