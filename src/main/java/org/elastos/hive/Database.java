package org.elastos.hive;

import java.util.List;
import java.util.concurrent.CompletableFuture;

import org.elastos.hive.database.CountOptions;
import org.elastos.hive.database.CreateCollectionOptions;
import org.elastos.hive.database.DeleteOptions;
import org.elastos.hive.database.DeleteResult;
import org.elastos.hive.database.FindOptions;
import org.elastos.hive.database.InsertManyResult;
import org.elastos.hive.database.InsertOneResult;
import org.elastos.hive.database.InsertOptions;
import org.elastos.hive.database.UpdateOptions;
import org.elastos.hive.database.UpdateResult;

import com.fasterxml.jackson.databind.JsonNode;

public interface Database {

    /**
     * Lets the vault owner create a collection on database.
     * @param name the collection name
     * @return fail(false) or success(treu)
     */
    CompletableFuture<Boolean> createCollection(String name, CreateCollectionOptions options);


    /**
     * Lets the vault owner delete a collection on database according to collection name.
     * @param name the collection name
     * @return fail(false) or success(true)
     */
    CompletableFuture<Boolean> deleteCollection(String name);


    /**
     * Insert a new document in a given collection
     * @param collection the collection name
     * @param doc The document to insert. Must be a mutable mapping type. If
     *            the document does not have an _id field one will be added automatically
     * @param options bypass_document_validation: (optional) If True, allows
     *                the write to opt-out of document level validation. Default is False.{@link InsertOptions}
     * @return Results returned by InsertOneResult{@link InsertOneResult} wrapper
     */
    CompletableFuture<InsertOneResult> insertOne(String collection, JsonNode doc, InsertOptions options);


    /**
     * Insert many new documents in a given collection
     * @param collection the collection name
     * @param docs The document to insert. Must be a mutable mapping type. If the
     *             document does not have an _id field one will be added automatically.
     * @param options ordered (optional): If True (the default) documents will be inserted on the server serially,
     *                in the order provided. If an error occurs all remaining inserts are aborted. If False, documents
     *                will be inserted on the server in arbitrary order, possibly in parallel, and all document inserts will be attempted.
     *                bypass_document_validation: (optional) If True, allows the write to opt-out of document level validation. Default is False.{@link InsertOptions}
     * @return Results returned by InsertManyResult{@link InsertManyResult} wrapper
     */
    CompletableFuture<InsertManyResult> insertMany(String collection, List<JsonNode> docs, InsertOptions options);


    /**
     * Count documents
     * @param collection the collection name
     * @param query The document of filter
     * @param options
     *              skip (int): The number of matching documents to skip before returning results.
     *              limit (int): The maximum number of documents to count. Must be a positive integer. If not provided, no limit is imposed.
     *              maxTimeMS (int): The maximum amount of time to allow this operation to run, in milliseconds.
     *              {@link CountOptions}
     * @return count size
     */
    CompletableFuture<Long> countDocuments(String collection, JsonNode query, CountOptions options);


    /**
     * Find a specific document
     * @param collection the collection name
     * @param query optional, a JSON object specifying elements which must be present for a document to be included in the result set
     * @param options optional,refer to {@link FindOptions}
     * @return a JSON object document result
     */
    CompletableFuture<JsonNode> findOne(String collection, JsonNode query, FindOptions options);

    /**
     * Find many documents
     * @param collection the collection name
     * @param query optional, a JSON object specifying elements which must be present for a document to be included in the result set
     * @param options optional,refer to {@link FindOptions}
     * @return a JsonNode array result of document
     */
    CompletableFuture<List<JsonNode>> findMany(String collection, JsonNode query, FindOptions options);


    /**
     * Update an existing document in a given collection
     * @param collection the collection name
     * @param filter A query that matches the document to update.
     * @param update The modifications to apply.
     * @param options optional, refer to {@link UpdateOptions}
     * @return Results returned by InsertResult{@link UpdateResult} wrapper
     */
    CompletableFuture<UpdateResult> updateOne(String collection, JsonNode filter, JsonNode update, UpdateOptions options);


    /**
     * Update many existing documents in a given collection
     * @param collection the collection name
     * @param filter A query that matches the document to update.
     * @param update The modifications to apply.
     * @param options optional, refer to {@link UpdateOptions}
     * @return Results returned by InsertResult{@link UpdateResult} wrapper
     */
    CompletableFuture<UpdateResult> updateMany(String collection, JsonNode filter, JsonNode update, UpdateOptions options);


    /**
     * Delete an existing document in a given collection
     * @param collection the collection name
     * @param filter A query that matches the document to delete.
     * @param options
     * @return
     */
    CompletableFuture<DeleteResult> deleteOne(String collection, JsonNode filter, DeleteOptions options);


    /**
     * Delete many existing documents in a given collection
     * @param collection the collection name
     * @param filter A query that matches the document to delete.
     * @param options
     * @return
     */
    CompletableFuture<DeleteResult> deleteMany(String collection, JsonNode filter, DeleteOptions options);

}
