package org.elastos.hive.vendor.vault;

import com.fasterxml.jackson.databind.JsonNode;

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
import org.elastos.hive.vendor.connection.ConnectionManager;
import org.elastos.hive.utils.JsonUtil;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import retrofit2.Response;

class ClientDatabase implements Database {

    private VaultAuthHelper authHelper;

    ClientDatabase(VaultAuthHelper authHelper) {
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

                int responseCode = checkResponseCode(response);
                if (responseCode == 404) {
                    throw new HiveException(HiveException.ITEM_NOT_FOUND);
                } else if (responseCode != 0) {
                    throw new HiveException(HiveException.ERROR);
                }
                callback.onSuccess(null);
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
        return deleteCollection(name);
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

                int responseCode = checkResponseCode(response);
                if (responseCode == 404) {
                    throw new HiveException(HiveException.ITEM_NOT_FOUND);
                } else if (responseCode != 0) {
                    throw new HiveException(HiveException.ERROR);
                }
                callback.onSuccess(null);
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
                Map map = new HashMap<>();
                map.put("collection", collection);
                String json = JsonUtil.getJsonFromObject(map);

                Response response = ConnectionManager.getHiveVaultApi()
                        .insertOne(RequestBody.create(MediaType.parse("Content-Type, application/json"), json))
                        .execute();

                int responseCode = checkResponseCode(response);
                if (responseCode == 404) {
                    throw new HiveException(HiveException.ITEM_NOT_FOUND);
                } else if (responseCode != 0) {
                    throw new HiveException(HiveException.ERROR);
                }
                callback.onSuccess(null);
                return null;
            } catch (Exception e) {
                HiveException exception = new HiveException(e.getLocalizedMessage());
                callback.onError(exception);
                throw new CompletionException(exception);
            }
        });
    }

    @Override
    public CompletableFuture<InsertResult> insertMany(String collection, List<JsonNode> docs, InsertOptions options) {
        return insertMany(collection, docs, options);
    }

    @Override
    public CompletableFuture<InsertResult> insertMany(String collection, List<JsonNode> docs, InsertOptions options, Callback<InsertResult> callback) {
        return authHelper.checkValid()
                .thenCompose(result -> insertManyImp(collection, docs, options, getCallback(callback)));
    }

    private CompletableFuture<InsertResult> insertManyImp(String collection, List<JsonNode> docs, InsertOptions options, Callback<InsertResult> callback) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                Map map = new HashMap<>();
                map.put("collection", collection);
                String json = JsonUtil.getJsonFromObject(map);

                Response response = ConnectionManager.getHiveVaultApi()
                        .insertMany(RequestBody.create(MediaType.parse("Content-Type, application/json"), json))
                        .execute();

                int responseCode = checkResponseCode(response);
                if (responseCode == 404) {
                    throw new HiveException(HiveException.ITEM_NOT_FOUND);
                } else if (responseCode != 0) {
                    throw new HiveException(HiveException.ERROR);
                }
                callback.onSuccess(null);
                return null;
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
                Map map = new HashMap<>();
                map.put("collection", collection);
                String json = JsonUtil.getJsonFromObject(map);

                Response response = ConnectionManager.getHiveVaultApi()
                        .countDocs(RequestBody.create(MediaType.parse("Content-Type, application/json"), json))
                        .execute();

                int responseCode = checkResponseCode(response);
                if (responseCode == 404) {
                    throw new HiveException(HiveException.ITEM_NOT_FOUND);
                } else if (responseCode != 0) {
                    throw new HiveException(HiveException.ERROR);
                }
                callback.onSuccess(null);
                return null;
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
                Map map = new HashMap<>();
                map.put("collection", collection);
                String json = JsonUtil.getJsonFromObject(map);

                Response response = ConnectionManager.getHiveVaultApi()
                        .findOne(RequestBody.create(MediaType.parse("Content-Type, application/json"), json))
                        .execute();

                int responseCode = checkResponseCode(response);
                if (responseCode == 404) {
                    throw new HiveException(HiveException.ITEM_NOT_FOUND);
                } else if (responseCode != 0) {
                    throw new HiveException(HiveException.ERROR);
                }
                callback.onSuccess(null);
                return null;
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
                Map map = new HashMap<>();
                map.put("collection", collection);
                String json = JsonUtil.getJsonFromObject(map);

                Response response = ConnectionManager.getHiveVaultApi()
                        .findMany(RequestBody.create(MediaType.parse("Content-Type, application/json"), json))
                        .execute();

                int responseCode = checkResponseCode(response);
                if (responseCode == 404) {
                    throw new HiveException(HiveException.ITEM_NOT_FOUND);
                } else if (responseCode != 0) {
                    throw new HiveException(HiveException.ERROR);
                }
                callback.onSuccess(null);
                return null;
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
                Map map = new HashMap<>();
                map.put("collection", collection);
                String json = JsonUtil.getJsonFromObject(map);

                Response response = ConnectionManager.getHiveVaultApi()
                        .updateOne(RequestBody.create(MediaType.parse("Content-Type, application/json"), json))
                        .execute();

                int responseCode = checkResponseCode(response);
                if (responseCode == 404) {
                    throw new HiveException(HiveException.ITEM_NOT_FOUND);
                } else if (responseCode != 0) {
                    throw new HiveException(HiveException.ERROR);
                }
                callback.onSuccess(null);
                return null;
            } catch (Exception e) {
                HiveException exception = new HiveException(e.getLocalizedMessage());
                callback.onError(exception);
                throw new CompletionException(exception);
            }
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
                Map map = new HashMap<>();
                map.put("collection", collection);
                String json = JsonUtil.getJsonFromObject(map);

                Response response = ConnectionManager.getHiveVaultApi()
                        .updateMany(RequestBody.create(MediaType.parse("Content-Type, application/json"), json))
                        .execute();

                int responseCode = checkResponseCode(response);
                if (responseCode == 404) {
                    throw new HiveException(HiveException.ITEM_NOT_FOUND);
                } else if (responseCode != 0) {
                    throw new HiveException(HiveException.ERROR);
                }
                callback.onSuccess(null);
                return null;
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
                Map map = new HashMap<>();
                map.put("collection", collection);
                String json = JsonUtil.getJsonFromObject(map);

                Response response = ConnectionManager.getHiveVaultApi()
                        .deleteOne(RequestBody.create(MediaType.parse("Content-Type, application/json"), json))
                        .execute();

                int responseCode = checkResponseCode(response);
                if (responseCode == 404) {
                    throw new HiveException(HiveException.ITEM_NOT_FOUND);
                } else if (responseCode != 0) {
                    throw new HiveException(HiveException.ERROR);
                }
                callback.onSuccess(null);
                return null;
            } catch (Exception e) {
                HiveException exception = new HiveException(e.getLocalizedMessage());
                callback.onError(exception);
                throw new CompletionException(exception);
            }
        });
    }

    @Override
    public CompletableFuture<DeleteResult> deleteMany(String collection, JsonNode filter, DeleteOptions options) {
        return deleteMany(collection, filter, options);
    }

    @Override
    public CompletableFuture<DeleteResult> deleteMany(String collection, JsonNode filter, DeleteOptions options, Callback<DeleteResult> callback) {
        return authHelper.checkValid()
                .thenCompose(result -> deleteManyImp(collection, filter, options, getCallback(callback)));
    }

    private CompletableFuture<DeleteResult> deleteManyImp(String collection, JsonNode filter, DeleteOptions options, Callback<DeleteResult> callback) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                Map map = new HashMap<>();
                map.put("collection", collection);
                String json = JsonUtil.getJsonFromObject(map);

                Response response = ConnectionManager.getHiveVaultApi()
                        .deleteMany(RequestBody.create(MediaType.parse("Content-Type, application/json"), json))
                        .execute();

                int responseCode = checkResponseCode(response);
                if (responseCode == 404) {
                    throw new HiveException(HiveException.ITEM_NOT_FOUND);
                } else if (responseCode != 0) {
                    throw new HiveException(HiveException.ERROR);
                }
                callback.onSuccess(null);
                return null;
            } catch (Exception e) {
                HiveException exception = new HiveException(e.getLocalizedMessage());
                callback.onError(exception);
                throw new CompletionException(exception);
            }
        });
    }

    private int checkResponseCode(Response response) {
        if (response == null)
            return -1;

        int code = response.code();
        if (code < 300 && code >= 200)
            return 0;

        return code;
    }


    private <T> Callback<T> getCallback(Callback<T> callback) {
        return (null == callback ? new NullCallback<T>() : callback);
    }

}
