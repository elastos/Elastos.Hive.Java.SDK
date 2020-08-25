package org.elastos.hive.interfaces;

import com.fasterxml.jackson.databind.JsonNode;

import org.elastos.hive.Callback;
import org.elastos.hive.database.CountOptions;
import org.elastos.hive.database.CreateCollectionOptions;
import org.elastos.hive.database.DeleteOptions;
import org.elastos.hive.database.DeleteResult;
import org.elastos.hive.database.FindOptions;
import org.elastos.hive.database.InsertOptions;
import org.elastos.hive.database.InsertResult;
import org.elastos.hive.database.UpdateOptions;
import org.elastos.hive.database.UpdateResult;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public interface Database {

    CompletableFuture<Boolean> createCollection(String name, CreateCollectionOptions options);
    CompletableFuture<Boolean> createCollection(String name, CreateCollectionOptions options, Callback<Boolean> callback);

    CompletableFuture<Boolean> deleteCollection(String name);
    CompletableFuture<Boolean> deleteCollection(String name, Callback<Boolean> callback);

    CompletableFuture<InsertResult> insertOne(String collection, JsonNode doc, InsertOptions options);
    CompletableFuture<InsertResult> insertOne(String collection, JsonNode doc, InsertOptions options, Callback<InsertResult> callback);

    CompletableFuture<InsertResult> insertMany(String collection, List<JsonNode> docs, InsertOptions options);
    CompletableFuture<InsertResult> insertMany(String collection, List<JsonNode> docs, InsertOptions options, Callback<InsertResult> callback);

    CompletableFuture<Long> countDocuments(String collection, JsonNode query, CountOptions options);
    CompletableFuture<Long> countDocuments(String collection, JsonNode query, CountOptions options, Callback<Long> callback);

    CompletableFuture<JsonNode> findOne(String collection, JsonNode query, FindOptions options);
    CompletableFuture<JsonNode> findOne(String collection, JsonNode query, FindOptions options, Callback<JsonNode> callback);

    CompletableFuture<List<JsonNode>> findMany(String collection, JsonNode query, FindOptions options);
    CompletableFuture<List<JsonNode>> findMany(String collection, JsonNode query, FindOptions options, Callback<List<JsonNode>> callback);

    CompletableFuture<UpdateResult> updateOne(String collection, JsonNode filter, JsonNode update, UpdateOptions options);
    CompletableFuture<UpdateResult> updateOne(String collection, JsonNode filter, JsonNode update, UpdateOptions options, Callback<UpdateResult> callback);

    CompletableFuture<UpdateResult> updateMany(String collection, JsonNode filter, JsonNode update, UpdateOptions options);
    CompletableFuture<UpdateResult> updateMany(String collection, JsonNode filter, JsonNode update, UpdateOptions options, Callback<UpdateResult> callback);

    CompletableFuture<DeleteResult> deleteOne(String collection, JsonNode filter, DeleteOptions options);
    CompletableFuture<DeleteResult> deleteOne(String collection, JsonNode filter, DeleteOptions options, Callback<DeleteResult> callback);

    CompletableFuture<DeleteResult> deleteMany(String collection, JsonNode filter, DeleteOptions options);
    CompletableFuture<DeleteResult> deleteMany(String collection, JsonNode filter, DeleteOptions options, Callback<DeleteResult> callback);

}
