package org.elastos.hive.vendor.vault;

import org.elastos.hive.exception.HiveException;
import org.elastos.hive.interfaces.Database;
import org.elastos.hive.vendor.connection.ConnectionManager;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import retrofit2.Response;

public class DatabaseImp implements Database {

    private CompletableFuture<Void> createCollection(String collection, String schema) {
        return CompletableFuture.runAsync(() -> {
            try {
                Map map = new HashMap<>();
                map.put("collection", collection);
                map.put("schema", schema);
                String json = new JSONObject(map).toString();
                Response response = ConnectionManager.getHiveVaultApi()
                        .auth(RequestBody.create(MediaType.parse("Content-Type, application/json"), json))
                        .execute();
            } catch (Exception e) {
                HiveException exception = new HiveException(e.getLocalizedMessage());
                throw new CompletionException(exception);
            }
        });
    }

    private CompletableFuture<Void> dropCollection(String collection) {
        return CompletableFuture.runAsync(() -> {
            try {
                Map map = new HashMap<>();
                map.put("collection", collection);
                String json = new JSONObject(map).toString();
                Response response = ConnectionManager.getHiveVaultApi()
                        .auth(RequestBody.create(MediaType.parse("Content-Type, application/json"), json))
                        .execute();
            } catch (Exception e) {
                HiveException exception = new HiveException(e.getLocalizedMessage());
                throw new CompletionException(exception);
            }
        });
    }

    private CompletableFuture<String> deleteImp(String collection, String _id, String match) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                Response response = ConnectionManager.getHiveVaultApi()
                        .delete_dbCol(collection + "/" + _id, match)
                        .execute();
                return response.body().toString();
            } catch (Exception e) {
                HiveException exception = new HiveException(e.getLocalizedMessage());
                throw new CompletionException(exception);
            }
        });
    }

    private CompletableFuture<String> patchImp(String collection, String _id, String etag, String item) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                Response response = ConnectionManager.getHiveVaultApi()
                        .patch_dbCol(collection + "/" + _id, etag, RequestBody.create(MediaType.parse("Content-Type, application/json"), item))
                        .execute();
                return response.body().toString();
            } catch (Exception e) {
                HiveException exception = new HiveException(e.getLocalizedMessage());
                throw new CompletionException(exception);
            }
        });
    }

    private CompletableFuture<String> putImp(String collection, String _id, String etag, String item) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                Response response = ConnectionManager.getHiveVaultApi()
                        .put_dbCol(collection + "/" + _id, etag, RequestBody.create(MediaType.parse("Content-Type, application/json"), item))
                        .execute();
                return response.body().toString();
            } catch (Exception e) {
                HiveException exception = new HiveException(e.getLocalizedMessage());
                throw new CompletionException(exception);
            }
        });
    }

    private CompletableFuture<String> queryImp(String collection, String item) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                Response response = ConnectionManager.getHiveVaultApi()
                        .get_dbCol(collection, item)
                        .execute();
                return response.body().toString();
            } catch (Exception e) {
                HiveException exception = new HiveException(e.getLocalizedMessage());
                throw new CompletionException(exception);
            }
        });
    }

    private CompletableFuture<String> insertImp(String collection, String item) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                Response response = ConnectionManager.getHiveVaultApi()
                        .post_dbCol(collection, RequestBody.create(MediaType.parse("Content-Type, application/json"), item))
                        .execute();
                return response.body().toString();
            } catch (Exception e) {
                HiveException exception = new HiveException(e.getLocalizedMessage());
                throw new CompletionException(exception);
            }
        });
    }

    @Override
    public CompletableFuture<Void> createCol(String collection, String schema) {
        return createCollection(collection, schema);
    }

    @Override
    public CompletableFuture<Void> dropCol(String collection) {
        return dropCollection(collection);
    }

    @Override
    public CompletableFuture<String> post(String collection, String item) {
        return insertImp(collection, item);
    }

    @Override
    public CompletableFuture<String> get(String collection, String params) {
        return queryImp(collection, params);
    }

    @Override
    public CompletableFuture<String> put(String collection, String _id, String etag, String item) {
        return putImp(collection, _id, etag, item);
    }

    @Override
    public CompletableFuture<String> patch(String collection, String _id, String etag, String item) {
        return patchImp(collection, _id, etag, item);
    }

    @Override
    public CompletableFuture<String> delete(String collection, String _id, String etag) {
        return deleteImp(collection, _id, etag);
    }
}
