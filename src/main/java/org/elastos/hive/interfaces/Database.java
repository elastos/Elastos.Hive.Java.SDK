package org.elastos.hive.interfaces;

import java.util.List;
import java.util.concurrent.CompletableFuture;

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
import org.elastos.hive.exception.HiveException;

import com.fasterxml.jackson.databind.JsonNode;

public interface Database {

    CompletableFuture<Boolean> createCollection(String name, CreateCollectionOptions options) throws HiveException;
    CompletableFuture<Boolean> createCollection(String name, CreateCollectionOptions options, Callback<Boolean> callback) throws HiveException;

    CompletableFuture<Boolean> deleteCollection(String name) throws HiveException;
    CompletableFuture<Boolean> deleteCollection(String name, Callback<Boolean> callback) throws HiveException;

    CompletableFuture<InsertResult> insertOne(String collection, JsonNode doc, InsertOptions options) throws HiveException;
    CompletableFuture<InsertResult> insertOne(String collection, JsonNode doc, InsertOptions options, Callback<InsertResult> callback) throws HiveException;

    CompletableFuture<InsertResult> insertMany(String collection, List<JsonNode> docs, InsertOptions options) throws HiveException;
    CompletableFuture<InsertResult> insertMany(String collection, List<JsonNode> docs, InsertOptions options, Callback<InsertResult> callback) throws HiveException;

    CompletableFuture<Long> countDocuments(String collection, JsonNode query, CountOptions options) throws HiveException;
    CompletableFuture<Long> countDocuments(String collection, JsonNode query, CountOptions options, Callback<Long> callback) throws HiveException;

    CompletableFuture<JsonNode> findOne(String collection, JsonNode query, FindOptions options) throws HiveException;
    CompletableFuture<JsonNode> findOne(String collection, JsonNode query, FindOptions options, Callback<JsonNode> callback) throws HiveException;

    CompletableFuture<List<JsonNode>> findMany(String collection, JsonNode query, FindOptions options) throws HiveException;
    CompletableFuture<List<JsonNode>> findMany(String collection, JsonNode query, FindOptions options, Callback<List<JsonNode>> callback) throws HiveException;

    CompletableFuture<UpdateResult> updateOne(String collection, JsonNode filter, JsonNode update, UpdateOptions options) throws HiveException;
    CompletableFuture<UpdateResult> updateOne(String collection, JsonNode filter, JsonNode update, UpdateOptions options, Callback<UpdateResult> callback) throws HiveException;

    CompletableFuture<UpdateResult> updateMany(String collection, JsonNode filter, JsonNode update, UpdateOptions options) throws HiveException;
    CompletableFuture<UpdateResult> updateMany(String collection, JsonNode filter, JsonNode update, UpdateOptions options, Callback<UpdateResult> callback) throws HiveException;

    CompletableFuture<DeleteResult> deleteOne(String collection, JsonNode filter, DeleteOptions options) throws HiveException;
    CompletableFuture<DeleteResult> deleteOne(String collection, JsonNode filter, DeleteOptions options, Callback<DeleteResult> callback) throws HiveException;

    CompletableFuture<DeleteResult> deleteMany(String collection, JsonNode filter, DeleteOptions options) throws HiveException;
    CompletableFuture<DeleteResult> deleteMany(String collection, JsonNode filter, DeleteOptions options, Callback<DeleteResult> callback) throws HiveException;

}
