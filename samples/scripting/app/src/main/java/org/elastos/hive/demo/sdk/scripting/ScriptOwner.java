package org.elastos.hive.demo.sdk.scripting;

import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;

import org.elastos.hive.database.InsertOneOptions;
import org.elastos.hive.database.InsertOneResult;
import org.elastos.hive.demo.sdk.SdkContext;
import org.elastos.hive.network.model.Condition;
import org.elastos.hive.network.model.Executable;
import org.elastos.hive.network.model.KeyValueDict;
import org.elastos.hive.network.model.ScriptFindBody;
import org.elastos.hive.network.model.ScriptInsertExecutableBody;
import org.elastos.hive.service.DatabaseService;
import org.elastos.hive.service.ScriptingService;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;

public class ScriptOwner {
    private final SdkContext sdkContext;

    private ScriptingService scriptingService;
    private DatabaseService databaseService;

    private String ownerDid;
    private String callDid;
    private String appDid;

    public ScriptOwner(SdkContext sdkContext) {
        this.sdkContext = sdkContext;
        scriptingService = this.sdkContext.newVault().getScriptingService();
        databaseService = this.sdkContext.newVault().getDatabaseService();
        ownerDid = this.sdkContext.getOwnerDid();
        callDid = this.sdkContext.getCallerDid();
        appDid = this.sdkContext.getAppId();
    }

    public CompletableFuture<Boolean> setScript() {
        CompletableFuture<Boolean> createGroup = databaseService.createCollection(
                ScriptConst.COLLECTION_GROUP, null);
        CompletableFuture<Boolean> createMessage = databaseService.createCollection(
                ScriptConst.COLLECTION_GROUP_MESSAGE, null);
        //add group named COLLECTION_GROUP_MESSAGE and add caller did into it,
        //  then caller will get the permission
        //  to access collection COLLECTION_GROUP_MESSAGE
        ObjectNode docNode = JsonNodeFactory.instance.objectNode();
        docNode.put("collection", ScriptConst.COLLECTION_GROUP_MESSAGE);
        docNode.put("did", callDid);
        CompletableFuture<InsertOneResult> addPermission = databaseService.insertOne(
                ScriptConst.COLLECTION_GROUP,
                docNode,
                new InsertOneOptions(false));
        KeyValueDict filter = new KeyValueDict().putKv("collection", ScriptConst.COLLECTION_GROUP_MESSAGE)
                .putKv("did", "$caller_did");
        CompletableFuture<Boolean> setScript = scriptingService.registerScript(ScriptConst.SCRIPT_NAME,
                new Condition(
                        "verify_user_permission",
                        "queryHasResults",
                        new ScriptFindBody(ScriptConst.COLLECTION_GROUP, filter)),
                Executable.createInsertExecutable(ScriptConst.SCRIPT_NAME,
                        new ScriptInsertExecutableBody(ScriptConst.COLLECTION_GROUP_MESSAGE, new KeyValueDict()
                                .putKv("author", "$params.author")
                                .putKv("content", "$params.content"),
                                new KeyValueDict().putKv("bypass_document_validation",false)
                                        .putKv("ordered",true)
                        )),
                false, false);
        return createGroup.thenCombineAsync(createMessage, (r1, r2)->{
            if (!r1 || !r2)
                throw new CompletionException(new Exception("Failed to prepare."));
            return true;
        }).thenCombineAsync(addPermission, (r1, r2)->{
            if (r2.getInsertedId() == null || r2.getInsertedId().isEmpty())
                throw new CompletionException(new Exception("Failed to add permission."));
            return true;
        }).thenCombineAsync(setScript, (r1,r2)->{
            if (!r2)
                throw new CompletionException(new Exception("Failed to set script."));
            return true;
        });
    }
}
