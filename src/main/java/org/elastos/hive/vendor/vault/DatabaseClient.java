package org.elastos.hive.vendor.vault;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;

import org.elastos.hive.Callback;
import org.elastos.hive.Database;
import org.elastos.hive.NullCallback;
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
import org.elastos.hive.utils.JsonUtil;
import org.elastos.hive.utils.ResponseHelper;
import org.elastos.hive.vendor.connection.ConnectionManager;
import org.elastos.hive.vendor.vault.network.model.CountDocResponse;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import retrofit2.Response;

public class DatabaseClient implements Database {

    private VaultAuthHelper authHelper;

    public DatabaseClient(VaultAuthHelper authHelper) {
        this.authHelper = authHelper;
    }

    @Override
    public CompletableFuture<Boolean> createCollection(String name, CreateCollectionOptions options) {
        return createCollection(name, options, null);
    }

    @Override
    public CompletableFuture<Boolean> createCollection(String name, CreateCollectionOptions options, Callback<Boolean> callback) {
        return authHelper.checkValid()
                .thenCompose(result -> createColImp(name, options, getCallback(callback)));
    }

    private CompletableFuture<Boolean> createColImp(String collection, CreateCollectionOptions options, Callback<Boolean> callback) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                Map map = new HashMap<>();
                map.put("collection", collection);
                String json = JsonUtil.getJsonFromObject(map);

                Response response = ConnectionManager.getHiveVaultApi()
                        .createCollection(RequestBody.create(MediaType.parse("Content-Type, application/json"), json))
                        .execute();

                int responseCode = ResponseHelper.checkResponseCode(response);
                if (responseCode == 404) {
                    throw new HiveException(HiveException.ITEM_NOT_FOUND);
                } else if (responseCode != 0) {
                    throw new HiveException(HiveException.ERROR);
                }
                callback.onSuccess(true);
                return true;
            } catch (Exception e) {
                HiveException exception = new HiveException(e.getLocalizedMessage());
                callback.onError(exception);
                throw new CompletionException(exception);
            }
        });
    }

    @Override
    public CompletableFuture<Boolean> deleteCollection(String name) {
        return deleteCollection(name, null);
    }

    @Override
    public CompletableFuture<Boolean> deleteCollection(String name, Callback<Boolean> callback) {
        return authHelper.checkValid()
                .thenCompose(result -> deleteColImp(name, getCallback(callback)));
    }

    private CompletableFuture<Boolean> deleteColImp(String collection, Callback<Boolean> callback) {
        return CompletableFuture.supplyAsync(() -> {
            try {

                Map map = new HashMap<>();
                map.put("collection", collection);
                String json = JsonUtil.getJsonFromObject(map);

                Response response = ConnectionManager.getHiveVaultApi()
                        .deleteCollection(RequestBody.create(MediaType.parse("Content-Type, application/json"), json))
                        .execute();

                int responseCode = ResponseHelper.checkResponseCode(response);
                if (responseCode == 404) {
                    throw new HiveException(HiveException.ITEM_NOT_FOUND);
                } else if (responseCode != 0) {
                    throw new HiveException(HiveException.ERROR);
                }
                callback.onSuccess(true);
                return true;
            } catch (Exception e) {
                HiveException exception = new HiveException(e.getLocalizedMessage());
                callback.onError(exception);
                throw new CompletionException(exception);
            }
        });
    }

    @Override
    public CompletableFuture<InsertResult> insertOne(String collection, JsonNode doc, InsertOptions options) {
        return insertOne(collection, doc, options, null);
    }

    @Override
    public CompletableFuture<InsertResult> insertOne(String collection, JsonNode doc, InsertOptions options, Callback<InsertResult> callback) {
        return authHelper.checkValid()
                .thenCompose(result -> insertOneImp(collection, doc, options, getCallback(callback)));
    }

    private CompletableFuture<InsertResult> insertOneImp(String collection, JsonNode doc, InsertOptions options, Callback<InsertResult> callback) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                ObjectNode rootNode = JsonNodeFactory.instance.objectNode();
                rootNode.put("collection", collection);
                rootNode.put("document", doc);
                if(null!=options) rootNode.put("options", JsonUtil.getJsonNode(options.serialize()));

                String json = rootNode.toString();
                Response response = ConnectionManager.getHiveVaultApi()
                        .insertOne(RequestBody.create(MediaType.parse("Content-Type, application/json"), json))
                        .execute();

                int responseCode = ResponseHelper.checkResponseCode(response);
                if (responseCode == 404) {
                    throw new HiveException(HiveException.ITEM_NOT_FOUND);
                } else if (responseCode != 0) {
                    throw new HiveException(HiveException.ERROR);
                }

                InsertResult insertResult = new InsertResult();
                insertResult.deserialize(ResponseHelper.toString(response));
                callback.onSuccess(insertResult);
                return insertResult;
            } catch (Exception e) {
                HiveException exception = new HiveException(e.getLocalizedMessage());
                callback.onError(exception);
                throw new CompletionException(exception);
            }
        });
    }

    @Override
    public CompletableFuture<InsertResult> insertMany(String collection, List<JsonNode> docs, InsertOptions options) {
        return insertMany(collection, docs, options, null);
    }

    @Override
    public CompletableFuture<InsertResult> insertMany(String collection, List<JsonNode> docs, InsertOptions options, Callback<InsertResult> callback) {
        return authHelper.checkValid()
                .thenCompose(result -> insertManyImp(collection, docs, options, getCallback(callback)));
    }

    private CompletableFuture<InsertResult> insertManyImp(String collection, List<JsonNode> docs, InsertOptions options, Callback<InsertResult> callback) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                ObjectNode rootNode = JsonNodeFactory.instance.objectNode();
                ArrayNode arrayNode = JsonNodeFactory.instance.arrayNode();
                arrayNode.addAll(docs);
                rootNode.put("collection", collection);
                rootNode.put("document", arrayNode);
                if(null!=options) rootNode.put("options", JsonUtil.getJsonNode(options.serialize()));

                String json = rootNode.toString();
                Response response = ConnectionManager.getHiveVaultApi()
                        .insertMany(RequestBody.create(MediaType.parse("Content-Type, application/json"), json))
                        .execute();

                int responseCode = ResponseHelper.checkResponseCode(response);
                if (responseCode == 404) {
                    throw new HiveException(HiveException.ITEM_NOT_FOUND);
                } else if (responseCode != 0) {
                    throw new HiveException(HiveException.ERROR);
                }
                InsertResult insertResult = new InsertResult();
                insertResult.deserialize(ResponseHelper.toString(response));
                callback.onSuccess(insertResult);
                return insertResult;
            } catch (Exception e) {
                HiveException exception = new HiveException(e.getLocalizedMessage());
                callback.onError(exception);
                throw new CompletionException(exception);
            }
        });
    }

    @Override
    public CompletableFuture<Long> countDocuments(String collection, JsonNode query, CountOptions options) {
        return countDocuments(collection, query, options, null);
    }

    @Override
    public CompletableFuture<Long> countDocuments(String collection, JsonNode query, CountOptions options, Callback<Long> callback) {
        return authHelper.checkValid()
                .thenCompose(result -> countDocumentsImp(collection, query, options, getCallback(callback)));
    }

    private CompletableFuture<Long> countDocumentsImp(String collection, JsonNode query, CountOptions options, Callback<Long> callback) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                ObjectNode rootNode = JsonNodeFactory.instance.objectNode();
                rootNode.put("collection", collection);
                if(null!=query) rootNode.put("filter", query);
                if(null!=options) rootNode.put("options", JsonUtil.getJsonNode(options.serialize()));

                String json = rootNode.toString();
                Response<CountDocResponse> response = ConnectionManager.getHiveVaultApi()
                        .countDocs(RequestBody.create(MediaType.parse("Content-Type, application/json"), json))
                        .execute();

                int responseCode = ResponseHelper.checkResponseCode(response);
                if (responseCode == 404) {
                    throw new HiveException(HiveException.ITEM_NOT_FOUND);
                } else if (responseCode != 0) {
                    throw new HiveException(HiveException.ERROR);
                }

                long count = response.body().getCount();
                callback.onSuccess(count);
                return count;
            } catch (Exception e) {
                HiveException exception = new HiveException(e.getLocalizedMessage());
                callback.onError(exception);
                throw new CompletionException(exception);
            }
        });
    }

    @Override
    public CompletableFuture<JsonNode> findOne(String collection, JsonNode query, FindOptions options) {
        return findOne(collection, query, options, null);
    }

    @Override
    public CompletableFuture<JsonNode> findOne(String collection, JsonNode query, FindOptions options, Callback<JsonNode> callback) {
        return authHelper.checkValid()
                .thenCompose(result -> findOneImp(collection, query, options, getCallback(callback)));
    }

    private CompletableFuture<JsonNode> findOneImp(String collection, JsonNode query, FindOptions options, Callback<JsonNode> callback) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                ObjectNode rootNode = JsonNodeFactory.instance.objectNode();
                rootNode.put("collection", collection);
                if(null!=query) rootNode.put("filter", query);
                if(null!=options) rootNode.put("options", JsonUtil.getJsonNode(options.serialize()));

                String json = rootNode.toString();
                Response response = ConnectionManager.getHiveVaultApi()
                        .findOne(RequestBody.create(MediaType.parse("Content-Type, application/json"), json))
                        .execute();

                int responseCode = ResponseHelper.checkResponseCode(response);
                if (responseCode == 404) {
                    throw new HiveException(HiveException.ITEM_NOT_FOUND);
                } else if (responseCode != 0) {
                    throw new HiveException(HiveException.ERROR);
                }

                JsonNode jsonNode = ResponseHelper.getVaule(response, JsonNode.class);
                JsonNode item = jsonNode.get("items");
                callback.onSuccess(item);
                return item;
            } catch (Exception e) {
                HiveException exception = new HiveException(e.getLocalizedMessage());
                callback.onError(exception);
                throw new CompletionException(exception);
            }
        });
    }

    @Override
    public CompletableFuture<List<JsonNode>> findMany(String collection, JsonNode query, FindOptions options) {
        return findMany(collection, query, options, null);
    }

    @Override
    public CompletableFuture<List<JsonNode>> findMany(String collection, JsonNode query, FindOptions options, Callback<List<JsonNode>> callback) {
        return authHelper.checkValid()
                .thenCompose(result -> findManyImp(collection, query, options, getCallback(callback)));
    }

    private CompletableFuture<List<JsonNode>> findManyImp(String collection, JsonNode query, FindOptions options, Callback<List<JsonNode>> callback) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                ObjectNode rootNode = JsonNodeFactory.instance.objectNode();
                rootNode.put("collection", collection);
                if(null!=query) rootNode.put("filter", query);
                if(null!=options) rootNode.put("options", JsonUtil.getJsonNode(options.serialize()));

                String json = rootNode.toString();
                Response response = ConnectionManager.getHiveVaultApi()
                        .findMany(RequestBody.create(MediaType.parse("Content-Type, application/json"), json))
                        .execute();

                int responseCode = ResponseHelper.checkResponseCode(response);
                if (responseCode == 404) {
                    throw new HiveException(HiveException.ITEM_NOT_FOUND);
                } else if (responseCode != 0) {
                    throw new HiveException(HiveException.ERROR);
                }

                List<JsonNode> jsonNodes = ResponseHelper.getArray(response, "items");
                callback.onSuccess(jsonNodes);
                return jsonNodes;
            } catch (Exception e) {
                HiveException exception = new HiveException(e.getLocalizedMessage());
                callback.onError(exception);
                throw new CompletionException(exception);
            }
        });
    }

    @Override
    public CompletableFuture<UpdateResult> updateOne(String collection, JsonNode filter, JsonNode update, UpdateOptions options) {
        return updateOne(collection, filter, update, options, null);
    }

    @Override
    public CompletableFuture<UpdateResult> updateOne(String collection, JsonNode filter, JsonNode update, UpdateOptions options, Callback<UpdateResult> callback) {
        return authHelper.checkValid()
                .thenCompose(result -> updateOneImp(collection, filter, update, options, getCallback(callback)));
    }

    private CompletableFuture<UpdateResult> updateOneImp(String collection, JsonNode filter, JsonNode update, UpdateOptions options, Callback<UpdateResult> callback) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                ObjectNode rootNode = JsonNodeFactory.instance.objectNode();
                rootNode.put("collection", collection);
                if(null!=filter) rootNode.put("filter", filter);
                if(null!=update) rootNode.put("update", update);
                if(null!=options) rootNode.put("options", JsonUtil.getJsonNode(options.serialize()));

                String json = rootNode.toString();
                Response response = ConnectionManager.getHiveVaultApi()
                        .updateOne(RequestBody.create(MediaType.parse("Content-Type, application/json"), json))
                        .execute();

                int responseCode = ResponseHelper.checkResponseCode(response);
                if (responseCode == 404) {
                    throw new HiveException(HiveException.ITEM_NOT_FOUND);
                } else if (responseCode != 0) {
                    throw new HiveException(HiveException.ERROR);
                }
                UpdateResult updateResult = new UpdateResult();
                String ret = ResponseHelper.toString(response);
                if(ret.contains("_error")) {
                    HiveException exception = new HiveException(ret);
                    throw exception;
                }
                updateResult.deserialize(ret);
                callback.onSuccess(updateResult);
                return updateResult;
            } catch (Exception e) {
                e.printStackTrace();
            }

            return null;
        });
    }

    @Override
    public CompletableFuture<UpdateResult> updateMany(String collection, JsonNode filter, JsonNode update, UpdateOptions options) {
        return updateMany(collection, filter, update, options, null);
    }

    @Override
    public CompletableFuture<UpdateResult> updateMany(String collection, JsonNode filter, JsonNode update, UpdateOptions options, Callback<UpdateResult> callback) {
        return authHelper.checkValid()
                .thenCompose(result -> updateManyImp(collection, filter, update, options, getCallback(callback)));
    }

    private CompletableFuture<UpdateResult> updateManyImp(String collection, JsonNode filter, JsonNode update, UpdateOptions options, Callback<UpdateResult> callback) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                ObjectNode rootNode = JsonNodeFactory.instance.objectNode();
                rootNode.put("collection", collection);
                if(null!=filter) rootNode.put("filter", filter);
                if(null!=update) rootNode.put("update", update);
                if(null!=options) rootNode.put("options", JsonUtil.getJsonNode(options.serialize()));

                String json = rootNode.toString();
                Response response = ConnectionManager.getHiveVaultApi()
                        .updateMany(RequestBody.create(MediaType.parse("Content-Type, application/json"), json))
                        .execute();

                int responseCode = ResponseHelper.checkResponseCode(response);
                if (responseCode == 404) {
                    throw new HiveException(HiveException.ITEM_NOT_FOUND);
                } else if (responseCode != 0) {
                    throw new HiveException(HiveException.ERROR);
                }
                UpdateResult updateResult = new UpdateResult();
                updateResult.deserialize(ResponseHelper.toString(response));
                callback.onSuccess(updateResult);
                return updateResult;
            } catch (Exception e) {
                HiveException exception = new HiveException(e.getLocalizedMessage());
                callback.onError(exception);
                throw new CompletionException(exception);
            }
        });
    }

    @Override
    public CompletableFuture<DeleteResult> deleteOne(String collection, JsonNode filter, DeleteOptions options) {
        return deleteOne(collection, filter, options, null);
    }

    @Override
    public CompletableFuture<DeleteResult> deleteOne(String collection, JsonNode filter, DeleteOptions options, Callback<DeleteResult> callback) {
        return authHelper.checkValid()
                .thenCompose(result -> deleteOneImp(collection, filter, options, getCallback(callback)));
    }

    private CompletableFuture<DeleteResult> deleteOneImp(String collection, JsonNode filter, DeleteOptions options, Callback<DeleteResult> callback) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                ObjectNode rootNode = JsonNodeFactory.instance.objectNode();
                rootNode.put("collection", collection);
                if(null!=filter) rootNode.put("filter", filter);

                String json = rootNode.toString();
                Response response = ConnectionManager.getHiveVaultApi()
                        .deleteOne(RequestBody.create(MediaType.parse("Content-Type, application/json"), json))
                        .execute();

                int responseCode = ResponseHelper.checkResponseCode(response);
                if (responseCode == 404) {
                    throw new HiveException(HiveException.ITEM_NOT_FOUND);
                } else if (responseCode != 0) {
                    throw new HiveException(HiveException.ERROR);
                }

                DeleteResult deleteResult = new DeleteResult();
                deleteResult.deserialize(ResponseHelper.toString(response));
                callback.onSuccess(deleteResult);
                return deleteResult;
            } catch (Exception e) {
                HiveException exception = new HiveException(e.getLocalizedMessage());
                callback.onError(exception);
                throw new CompletionException(exception);
            }
        });
    }

    @Override
    public CompletableFuture<DeleteResult> deleteMany(String collection, JsonNode filter, DeleteOptions options) {
        return deleteMany(collection, filter, options, null);
    }

    @Override
    public CompletableFuture<DeleteResult> deleteMany(String collection, JsonNode filter, DeleteOptions options, Callback<DeleteResult> callback) {
        return authHelper.checkValid()
                .thenCompose(result -> deleteManyImp(collection, filter, options, getCallback(callback)));
    }

    private CompletableFuture<DeleteResult> deleteManyImp(String collection, JsonNode filter, DeleteOptions options, Callback<DeleteResult> callback) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                ObjectNode rootNode = JsonNodeFactory.instance.objectNode();
                rootNode.put("collection", collection);
                if(null!=filter) rootNode.put("filter", filter);

                String json = rootNode.toString();
                Response response = ConnectionManager.getHiveVaultApi()
                        .deleteMany(RequestBody.create(MediaType.parse("Content-Type, application/json"), json))
                        .execute();

                int responseCode = ResponseHelper.checkResponseCode(response);
                if (responseCode == 404) {
                    throw new HiveException(HiveException.ITEM_NOT_FOUND);
                } else if (responseCode != 0) {
                    throw new HiveException(HiveException.ERROR);
                }

                DeleteResult deleteResult = new DeleteResult();
                deleteResult.deserialize(ResponseHelper.toString(response));
                callback.onSuccess(deleteResult);
                return deleteResult;
            } catch (Exception e) {
                HiveException exception = new HiveException(e.getLocalizedMessage());
                callback.onError(exception);
                throw new CompletionException(exception);
            }
        });
    }

    private <T> Callback<T> getCallback(Callback<T> callback) {
        return (null == callback ? new NullCallback<T>() : callback);
    }

}
