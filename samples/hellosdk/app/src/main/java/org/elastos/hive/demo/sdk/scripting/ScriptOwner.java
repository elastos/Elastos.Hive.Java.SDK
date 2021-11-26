package org.elastos.hive.demo.sdk.scripting;

import com.fasterxml.jackson.databind.JsonNode;
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

public class ScriptOwner {

    private final SdkContext sdkContext;

    private ScriptingService scriptingService;
    private DatabaseService databaseService;

    private String callDid;

    public ScriptOwner(SdkContext sdkContext) {
        this.sdkContext = sdkContext;
        scriptingService = this.sdkContext.newVault().getScriptingService();
        databaseService = this.sdkContext.newVault().getDatabaseService();
        callDid = this.sdkContext.getCallerDid();
    }

    private CompletableFuture<Void> cleanTwoCollections() {
        return databaseService.deleteCollection(ScriptConst.COLLECTION_GROUP)
                .thenCompose(result -> databaseService.deleteCollection(ScriptConst.COLLECTION_GROUP_MESSAGE));
    }

    private CompletableFuture<Void> createTwoCollections() {
        return databaseService.createCollection(ScriptConst.COLLECTION_GROUP)
                .thenCompose(result->databaseService.createCollection(ScriptConst.COLLECTION_GROUP_MESSAGE));
    }

    private CompletableFuture<InsertResult> addPermission2Caller() {
        // The document to save the permission of the user DID.
        ObjectNode conditionDoc = JsonNodeFactory.instance.objectNode();
        conditionDoc.put("collection", ScriptConst.COLLECTION_GROUP_MESSAGE);
        conditionDoc.put("did", callDid);
        // Insert the permission to COLLECTION_GROUP
        return databaseService.insertOne(
                ScriptConst.COLLECTION_GROUP,
                conditionDoc,
                new InsertOptions().bypassDocumentValidation(false));
    }

    private CompletableFuture<Void> doSetScript() {
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
        return scriptingService.registerScript(ScriptConst.SCRIPT_NAME,
                new QueryHasResultCondition("verify_user_permission", ScriptConst.COLLECTION_GROUP, filter),
                new InsertExecutable(ScriptConst.SCRIPT_NAME, ScriptConst.COLLECTION_GROUP_MESSAGE, msgDoc, options),
                false, false);
    }

    public CompletableFuture<Void> setScript() {
        return cleanTwoCollections()
                .thenCompose(result->createTwoCollections())
                .thenCompose(result->addPermission2Caller())
                .thenCompose(result->doSetScript());
    }

    public CompletableFuture<InsertResult> insertDocument() {
        return cleanTwoCollections()
                .thenCompose(result->createTwoCollections())
                .thenCompose(result->{
                    ObjectNode msg = JsonNodeFactory.instance.objectNode();
                    msg.put("author", "John");
                    msg.put("content", "the message from the owner.");
                    return databaseService.insertOne(ScriptConst.COLLECTION_GROUP_MESSAGE,
                            msg, new InsertOptions().bypassDocumentValidation(true));
                });
    }

}
