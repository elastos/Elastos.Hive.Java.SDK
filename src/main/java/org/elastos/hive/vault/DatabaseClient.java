package org.elastos.hive.vault;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;

import org.elastos.hive.Database;
import org.elastos.hive.database.CountOptions;
import org.elastos.hive.database.DeleteOptions;
import org.elastos.hive.database.DeleteResult;
import org.elastos.hive.database.FindOptions;
import org.elastos.hive.database.InsertManyResult;
import org.elastos.hive.database.InsertOneResult;
import org.elastos.hive.database.InsertOptions;
import org.elastos.hive.database.UpdateOptions;
import org.elastos.hive.database.UpdateResult;
import org.elastos.hive.exception.HiveException;
import org.elastos.hive.utils.JsonUtil;
import org.elastos.hive.utils.ResponseHelper;
import org.elastos.hive.connection.ConnectionManager;
import org.elastos.hive.vault.network.model.CountDocResponse;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import retrofit2.Response;

public class DatabaseClient implements Database {

    private AuthHelper authHelper;

    public DatabaseClient(AuthHelper authHelper) {
        this.authHelper = authHelper;
    }

    @Override
    public CompletableFuture<Boolean> createCollection(String name) {
        return authHelper.checkValid()
                .thenCompose(result -> createColImp(name));
    }

    private CompletableFuture<Boolean> createColImp(String collection) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                Map<String, Object> map = new HashMap<>();
                map.put("collection", collection);
                String json = JsonUtil.getJsonFromObject(map);

                Response response = ConnectionManager.getHiveVaultApi()
                        .createCollection(RequestBody.create(MediaType.parse("Content-Type, application/json"), json))
                        .execute();

                authHelper.checkResponseCode(response);
                return true;
            } catch (Exception e) {
                HiveException exception = new HiveException(e.getLocalizedMessage());
                throw new CompletionException(exception);
            }
        });
    }

    @Override
    public CompletableFuture<Boolean> deleteCollection(String name) {
        return authHelper.checkValid()
                .thenCompose(result -> deleteColImp(name));
    }

    private CompletableFuture<Boolean> deleteColImp(String collection) {
        return CompletableFuture.supplyAsync(() -> {
            try {

                Map map = new HashMap<>();
                map.put("collection", collection);
                String json = JsonUtil.getJsonFromObject(map);

                Response response = ConnectionManager.getHiveVaultApi()
                        .deleteCollection(RequestBody.create(MediaType.parse("Content-Type, application/json"), json))
                        .execute();

                authHelper.checkResponseCode(response);
                return true;
            } catch (Exception e) {
                HiveException exception = new HiveException(e.getLocalizedMessage());
                throw new CompletionException(exception);
            }
        });
    }

    @Override
    public CompletableFuture<InsertOneResult> insertOne(String collection, JsonNode doc, InsertOptions options) {
        return authHelper.checkValid()
                .thenCompose(result -> insertOneImp(collection, doc, options));
    }

    private CompletableFuture<InsertOneResult> insertOneImp(String collection, JsonNode doc, InsertOptions options) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                ObjectNode rootNode = JsonNodeFactory.instance.objectNode();
                rootNode.put("collection", collection);
                rootNode.set("document", doc);
                if(null!=options) rootNode.set("options", JsonUtil.getJsonNode(options.serialize()));

                String json = rootNode.toString();
                Response response = ConnectionManager.getHiveVaultApi()
                        .insertOne(RequestBody.create(MediaType.parse("Content-Type, application/json"), json))
                        .execute();

                authHelper.checkResponseCode(response);
                InsertOneResult insertResult = InsertOneResult.deserialize(ResponseHelper.toString(response));
                return insertResult;
            } catch (Exception e) {
                HiveException exception = new HiveException(e.getLocalizedMessage());
                throw new CompletionException(exception);
            }
        });
    }

    @Override
    public CompletableFuture<InsertManyResult> insertMany(String collection, List<JsonNode> docs, InsertOptions options) {
        return authHelper.checkValid()
                .thenCompose(result -> insertManyImp(collection, docs, options));
    }

    private CompletableFuture<InsertManyResult> insertManyImp(String collection, List<JsonNode> docs, InsertOptions options) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                ObjectNode rootNode = JsonNodeFactory.instance.objectNode();
                ArrayNode arrayNode = JsonNodeFactory.instance.arrayNode();
                arrayNode.addAll(docs);
                rootNode.put("collection", collection);
                rootNode.set("document", arrayNode);
                if(null!=options) rootNode.set("options", JsonUtil.getJsonNode(options.serialize()));

                String json = rootNode.toString();
                Response response = ConnectionManager.getHiveVaultApi()
                        .insertMany(RequestBody.create(MediaType.parse("Content-Type, application/json"), json))
                        .execute();

                authHelper.checkResponseCode(response);
                InsertManyResult insertResult = InsertManyResult.deserialize(ResponseHelper.toString(response));
                return insertResult;
            } catch (Exception e) {
                HiveException exception = new HiveException(e.getLocalizedMessage());
                throw new CompletionException(exception);
            }
        });
    }

    @Override
    public CompletableFuture<Long> countDocuments(String collection, JsonNode query, CountOptions options) {
        return authHelper.checkValid()
                .thenCompose(result -> countDocumentsImp(collection, query, options));
    }

    private CompletableFuture<Long> countDocumentsImp(String collection, JsonNode query, CountOptions options) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                ObjectNode rootNode = JsonNodeFactory.instance.objectNode();
                rootNode.put("collection", collection);
                if(null!=query) rootNode.set("filter", query);
                if(null!=options) rootNode.set("options", JsonUtil.getJsonNode(options.serialize()));

                String json = rootNode.toString();
                Response<CountDocResponse> response = ConnectionManager.getHiveVaultApi()
                        .countDocs(RequestBody.create(MediaType.parse("Content-Type, application/json"), json))
                        .execute();

                authHelper.checkResponseCode(response);
                long count = response.body().getCount();
                return count;
            } catch (Exception e) {
                HiveException exception = new HiveException(e.getLocalizedMessage());
                throw new CompletionException(exception);
            }
        });
    }

    @Override
    public CompletableFuture<JsonNode> findOne(String collection, JsonNode query, FindOptions options) {
        return authHelper.checkValid()
                .thenCompose(result -> findOneImp(collection, query, options));
    }

    private CompletableFuture<JsonNode> findOneImp(String collection, JsonNode query, FindOptions options) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                ObjectNode rootNode = JsonNodeFactory.instance.objectNode();
                rootNode.put("collection", collection);
                if(null!=query) rootNode.set("filter", query);
                if(null!=options) rootNode.set("options", JsonUtil.getJsonNode(options.serialize()));

                String json = rootNode.toString();
                Response response = ConnectionManager.getHiveVaultApi()
                        .findOne(RequestBody.create(MediaType.parse("Content-Type, application/json"), json))
                        .execute();

                authHelper.checkResponseCode(response);
                JsonNode jsonNode = ResponseHelper.getValue(response, JsonNode.class);
                JsonNode item = jsonNode.get("items");
                return item;
            } catch (Exception e) {
                HiveException exception = new HiveException(e.getLocalizedMessage());
                throw new CompletionException(exception);
            }
        });
    }

    @Override
    public CompletableFuture<List<JsonNode>> findMany(String collection, JsonNode query, FindOptions options) {
        return authHelper.checkValid()
                .thenCompose(result -> findManyImp(collection, query, options));
    }

    private CompletableFuture<List<JsonNode>> findManyImp(String collection, JsonNode query, FindOptions options) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                ObjectNode rootNode = JsonNodeFactory.instance.objectNode();
                rootNode.put("collection", collection);
                if(null!=query) rootNode.set("filter", query);
                if(null!=options) rootNode.set("options", JsonUtil.getJsonNode(options.serialize()));

                String json = rootNode.toString();
                Response response = ConnectionManager.getHiveVaultApi()
                        .findMany(RequestBody.create(MediaType.parse("Content-Type, application/json"), json))
                        .execute();

                authHelper.checkResponseCode(response);
                List<JsonNode> jsonNodes = ResponseHelper.getArray(response, "items");
                return jsonNodes;
            } catch (Exception e) {
                HiveException exception = new HiveException(e.getLocalizedMessage());
                throw new CompletionException(exception);
            }
        });
    }

    @Override
    public CompletableFuture<UpdateResult> updateOne(String collection, JsonNode filter, JsonNode update, UpdateOptions options) {
        return authHelper.checkValid()
                .thenCompose(result -> updateOneImp(collection, filter, update, options));
    }

    private CompletableFuture<UpdateResult> updateOneImp(String collection, JsonNode filter, JsonNode update, UpdateOptions options) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                ObjectNode rootNode = JsonNodeFactory.instance.objectNode();
                rootNode.put("collection", collection);
                if(null!=filter) rootNode.set("filter", filter);
                if(null!=update) rootNode.set("update", update);
                if(null!=options) rootNode.set("options", JsonUtil.getJsonNode(options.serialize()));

                String json = rootNode.toString();
                Response response = ConnectionManager.getHiveVaultApi()
                        .updateOne(RequestBody.create(MediaType.parse("Content-Type, application/json"), json))
                        .execute();

                authHelper.checkResponseCode(response);
                String ret = ResponseHelper.toString(response);
                if(ret.contains("_error")) {
                    HiveException exception = new HiveException(ret);
                    throw exception;
                }
                UpdateResult updateResult = UpdateResult.deserialize(ret);
                return updateResult;
            } catch (Exception e) {
                e.printStackTrace();
            }

            return null;
        });
    }

    @Override
    public CompletableFuture<UpdateResult> updateMany(String collection, JsonNode filter, JsonNode update, UpdateOptions options) {
        return authHelper.checkValid()
                .thenCompose(result -> updateManyImp(collection, filter, update, options));
    }

    private CompletableFuture<UpdateResult> updateManyImp(String collection, JsonNode filter, JsonNode update, UpdateOptions options) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                ObjectNode rootNode = JsonNodeFactory.instance.objectNode();
                rootNode.put("collection", collection);
                if(null!=filter) rootNode.set("filter", filter);
                if(null!=update) rootNode.set("update", update);
                if(null!=options) rootNode.set("options", JsonUtil.getJsonNode(options.serialize()));

                String json = rootNode.toString();
                Response response = ConnectionManager.getHiveVaultApi()
                        .updateMany(RequestBody.create(MediaType.parse("Content-Type, application/json"), json))
                        .execute();

                authHelper.checkResponseCode(response);
                UpdateResult updateResult = UpdateResult.deserialize(ResponseHelper.toString(response));
                return updateResult;
            } catch (Exception e) {
                HiveException exception = new HiveException(e.getLocalizedMessage());
                throw new CompletionException(exception);
            }
        });
    }

    @Override
    public CompletableFuture<DeleteResult> deleteOne(String collection, JsonNode filter, DeleteOptions options) {
        return authHelper.checkValid()
                .thenCompose(result -> deleteOneImp(collection, filter, options));
    }

    private CompletableFuture<DeleteResult> deleteOneImp(String collection, JsonNode filter, DeleteOptions options) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                ObjectNode rootNode = JsonNodeFactory.instance.objectNode();
                rootNode.put("collection", collection);
                if(null!=filter) rootNode.set("filter", filter);

                String json = rootNode.toString();
                Response response = ConnectionManager.getHiveVaultApi()
                        .deleteOne(RequestBody.create(MediaType.parse("Content-Type, application/json"), json))
                        .execute();

                authHelper.checkResponseCode(response);
                DeleteResult deleteResult = DeleteResult.deserialize(ResponseHelper.toString(response));
                return deleteResult;
            } catch (Exception e) {
                HiveException exception = new HiveException(e.getLocalizedMessage());
                throw new CompletionException(exception);
            }
        });
    }

    @Override
    public CompletableFuture<DeleteResult> deleteMany(String collection, JsonNode filter, DeleteOptions options) {
        return authHelper.checkValid()
                .thenCompose(result -> deleteManyImp(collection, filter, options));
    }

    private CompletableFuture<DeleteResult> deleteManyImp(String collection, JsonNode filter, DeleteOptions options) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                ObjectNode rootNode = JsonNodeFactory.instance.objectNode();
                rootNode.put("collection", collection);
                if(null!=filter) rootNode.set("filter", filter);

                String json = rootNode.toString();
                Response response = ConnectionManager.getHiveVaultApi()
                        .deleteMany(RequestBody.create(MediaType.parse("Content-Type, application/json"), json))
                        .execute();

                authHelper.checkResponseCode(response);
                DeleteResult deleteResult = DeleteResult.deserialize(ResponseHelper.toString(response));
                return deleteResult;
            } catch (Exception e) {
                HiveException exception = new HiveException(e.getLocalizedMessage());
                throw new CompletionException(exception);
            }
        });
    }
}
