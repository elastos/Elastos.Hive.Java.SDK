package org.elastos.hive.demo.sdk.scripting;

import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;

import org.elastos.hive.demo.sdk.SdkContext;
import org.elastos.hive.service.DatabaseService;
import org.elastos.hive.service.ScriptingService;
import org.elastos.hive.vault.database.InsertOptions;
import org.elastos.hive.vault.database.InsertResult;
import org.elastos.hive.vault.scripting.InsertExecutable;
import org.elastos.hive.vault.scripting.QueryHasResultCondition;

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
        ownerDid = this.sdkContext.getUserDid();
        callDid = this.sdkContext.getCallerDid();
        appDid = this.sdkContext.getAppDid();
    }

    public CompletableFuture<Boolean> setScript() {
        // Create COLLECTION_GROUP to record the permission of the user DID.
        CompletableFuture<Void> createGroup = databaseService.createCollection(ScriptConst.COLLECTION_GROUP);
        // Create COLLECTION_GROUP_MESSAGE to keep the messages of the application.
        CompletableFuture<Void> createMessage = databaseService.createCollection(ScriptConst.COLLECTION_GROUP_MESSAGE);

        // The document to save the permission of the user DID.
        ObjectNode conditionDoc = JsonNodeFactory.instance.objectNode();
        conditionDoc.put("collection", ScriptConst.COLLECTION_GROUP_MESSAGE);
        conditionDoc.put("did", callDid);
        // Insert persmission to COLLECTION_GROUP
        CompletableFuture<InsertResult> addPermission = databaseService.insertOne(
                ScriptConst.COLLECTION_GROUP,
                conditionDoc,
                new InsertOptions().bypassDocumentValidation(false));

        // The condition to restrict the user DID.
        ObjectNode filter = JsonNodeFactory.instance.objectNode();
        filter.put("collection",ScriptConst.COLLECTION_GROUP_MESSAGE);
        filter.put("did","$caller_did");
        // The message is for inserting to the COLLECTION_GROUP_MESSAGE.
        ObjectNode msgDoc = JsonNodeFactory.instance.objectNode();
        msgDoc.put("author", "$params.author");
        msgDoc.put("content", "$params.content");
        ObjectNode options = JsonNodeFactory.instance.objectNode();
        options.put("bypass_document_validation", false);
        options.put("ordered", true);
        // register the script for caller to insert message to COLLECTION_GROUP_MESSAGE
        CompletableFuture<Void> setScript = scriptingService.registerScript(ScriptConst.SCRIPT_NAME,
                new QueryHasResultCondition("verify_user_permission",ScriptConst.COLLECTION_GROUP, filter),
                new InsertExecutable(ScriptConst.SCRIPT_NAME, ScriptConst.COLLECTION_GROUP_MESSAGE, msgDoc, options),
                false, false);
        return createGroup.thenCombineAsync(createMessage, (r1, r2)-> true)
                .thenCombineAsync(addPermission, (r1, r2)->{
                    if (r2.getInsertedIds() == null || r2.getInsertedIds().isEmpty())
                        throw new CompletionException(new Exception("Failed to add permission."));
                    return true;
                }).thenCombineAsync(setScript, (r1, r2)-> true);
    }
}
