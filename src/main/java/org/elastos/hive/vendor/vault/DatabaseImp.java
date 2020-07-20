package org.elastos.hive.vendor.vault;

import org.elastos.hive.exception.HiveException;
import org.elastos.hive.interfaces.Database;
import org.elastos.hive.vendor.connection.ConnectionManager;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import retrofit2.Response;

public class DatabaseImp implements Database {
    @Override
    public CompletableFuture<Void> createCol(String collection, String schema) throws Exception {
        return createCollection(collection, schema);
    }

    @Override
    public CompletableFuture<Void> dropCol(String collection) throws Exception {
        return dropCollection(collection);
    }

    @Override
    public CompletableFuture<Map<String, Object>> queryByID(String collection, String id) throws Exception {
        return null;
    }

    @Override
    public CompletableFuture<List<Map<String, Object>>> queryAll(String collection) throws Exception {
        return null;
    }

    @Override
    public CompletableFuture<Boolean> insert(String collection, String doc) throws Exception {
        return null;
    }

    @Override
    public CompletableFuture<Boolean> update(String table, String oldDoc, String newDoc) throws Exception {
        return null;
    }

    private CompletableFuture<Void> createCollection(String collection, String schema) throws Exception {
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

}
