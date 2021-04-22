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

import java.util.concurrent.ExecutionException;

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

    public boolean setScript() throws ExecutionException, InterruptedException {
        if (!init_for_caller())
            return false;
        if (!set_permission_for_caller())
            return false;
        return register_script_for_caller();
    }

    private boolean init_for_caller() throws ExecutionException, InterruptedException {
        Boolean isSuccess = databaseService.createCollection(
                ScriptConst.COLLECTION_GROUP, null).get();
        if (!isSuccess)
            return false;
        return databaseService.createCollection(
                ScriptConst.COLLECTION_GROUP_MESSAGE, null).get();
    }

    private boolean set_permission_for_caller() throws ExecutionException, InterruptedException {
        //add group named COLLECTION_GROUP_MESSAGE and add caller did into it,
        //  then caller will get the permission
        //  to access collection COLLECTION_GROUP_MESSAGE
        ObjectNode docNode = JsonNodeFactory.instance.objectNode();
        docNode.put("collection", ScriptConst.COLLECTION_GROUP_MESSAGE);
        docNode.put("did", callDid);
        InsertOneResult result = databaseService.insertOne(ScriptConst.COLLECTION_GROUP, docNode,
                new InsertOneOptions(false)).get();
        return result.getInsertedId() != null && !result.getInsertedId().isEmpty();
    }

    private boolean register_script_for_caller() throws ExecutionException, InterruptedException {
        KeyValueDict filter = new KeyValueDict().putKv("collection", ScriptConst.COLLECTION_GROUP_MESSAGE)
                .putKv("did", "$caller_did");
        return scriptingService.registerScript(ScriptConst.SCRIPT_NAME,
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
                false, false).get();
    }
}
