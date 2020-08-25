package org.elastos.hive.vendor.vault;

import org.elastos.hive.Callback;
import org.elastos.hive.NullCallback;
import org.elastos.hive.exception.HiveException;
import org.elastos.hive.interfaces.Database;
import org.elastos.hive.vendor.connection.ConnectionManager;
import org.json.JSONObject;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Response;

class ClientDatabase implements Database {

    private VaultAuthHelper authHelper;

    ClientDatabase(VaultAuthHelper authHelper) {
        this.authHelper = authHelper;
    }

    @Override
    public CompletableFuture<Void> createCol(String collection, String schema) {
        return createCol(collection, schema, null);
    }

    @Override
    public CompletableFuture<Void> createCol(String collection, String schema, Callback<Void> callback) {
        return authHelper.checkValid()
                .thenCompose(result -> createColImp(collection, schema, getCallback(callback)));
    }

    private CompletableFuture<Void> createColImp(String collection, String schema, Callback<Void> callback) {
        return CompletableFuture.runAsync(() -> {
            try {

                JSONObject root = new JSONObject();
                root.put("collection", collection);
                JSONObject schemaObject = new JSONObject(schema);
                root.put("schema", schemaObject);

                Response response = ConnectionManager.getTestHiveVaultApi()
                        .createCollection(RequestBody.create(MediaType.parse("Content-Type, application/json"), root.toString()))
                        .execute();

                int responseCode = checkResponseCode(response);
                if (responseCode == 404) {
                    throw new HiveException(HiveException.ITEM_NOT_FOUND);
                } else if (responseCode != 0) {
                    throw new HiveException(HiveException.ERROR);
                }
                callback.onSuccess(null);
            } catch (Exception e) {
                HiveException exception = new HiveException(e.getLocalizedMessage());
                callback.onError(exception);
                throw new CompletionException(exception);
            }
        });
    }

    @Override
    public CompletableFuture<String> insert(String collection, String item) {
        return insert(collection, item, null);
    }

    @Override
    public CompletableFuture<String> insert(String collection, String item, Callback<String> callback) {
        return authHelper.checkValid()
                .thenCompose(result -> insertImp(collection, item, getCallback(callback)));
    }

    private CompletableFuture<String> insertImp(String collection, String item, Callback<String> callback) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                Response response = ConnectionManager.getTestHiveVaultApi()
                        .post_dbCol(collection, RequestBody.create(MediaType.parse("Content-Type, application/json"), item))
                        .execute();

                int responseCode = checkResponseCode(response);
                if (responseCode == 404) {
                    throw new HiveException(HiveException.ITEM_NOT_FOUND);
                } else if (responseCode != 0) {
                    throw new HiveException(HiveException.ERROR);
                }
                ResponseBody body = (ResponseBody) response.body();
                String ret = body.string();
                callback.onSuccess(ret);
                return ret;
            } catch (Exception e) {
                HiveException exception = new HiveException(e.getLocalizedMessage());
                callback.onError(exception);
                throw new CompletionException(exception);
            }
        });
    }

    @Override
    public CompletableFuture<String> query(String collection, String where) {
        return query(collection, where, null);
    }

    @Override
    public CompletableFuture<String> query(String collection, String where, Callback<String> callback) {
        return authHelper.checkValid()
                .thenCompose(result -> queryImp(collection, where, getCallback(callback)));
    }

    private CompletableFuture<String> queryImp(String collection, String item, Callback<String> callback) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                Response response = ConnectionManager.getTestHiveVaultApi()
                        .get_dbCol(collection, item)
                        .execute();

                int responseCode = checkResponseCode(response);
                if (responseCode == 404) {
                    throw new HiveException(HiveException.ITEM_NOT_FOUND);
                } else if (responseCode != 0) {
                    throw new HiveException(HiveException.ERROR);
                }
                ResponseBody body = (ResponseBody) response.body();
                String ret = body.string();
                callback.onSuccess(ret);
                return ret;
            } catch (Exception e) {
                HiveException exception = new HiveException(e.getLocalizedMessage());
                callback.onError(exception);
                throw new CompletionException(exception);
            }
        });
    }

    @Override
    public CompletableFuture<String> put(String collection, String _id, String etag, String item) {
        return put(collection, _id, etag, item, null);
    }

    @Override
    public CompletableFuture<String> put(String collection, String _id, String etag, String item, Callback<String> callback) {
        return authHelper.checkValid()
                .thenCompose(result -> putImp(collection, _id, etag, item, getCallback(callback)));
    }

    private CompletableFuture<String> putImp(String collection, String _id, String etag, String item, Callback<String> callback) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                Response response = ConnectionManager.getTestHiveVaultApi()
                        .put_dbCol(collection + "/" + _id, etag, RequestBody.create(MediaType.parse("Content-Type, application/json"), item))
                        .execute();

                int responseCode = checkResponseCode(response);
                if (responseCode == 404) {
                    throw new HiveException(HiveException.ITEM_NOT_FOUND);
                } else if (responseCode != 0) {
                    throw new HiveException(HiveException.ERROR);
                }
                ResponseBody body = (ResponseBody) response.body();
                String ret = body.string();
                callback.onSuccess(ret);
                return ret;
            } catch (Exception e) {
                HiveException exception = new HiveException(e.getLocalizedMessage());
                callback.onError(exception);
                throw new CompletionException(exception);
            }
        });
    }

    @Override
    public CompletableFuture<String> patch(String collection, String _id, String etag, String item) {
        return patch(collection, _id, etag, item, null);
    }

    @Override
    public CompletableFuture<String> patch(String collection, String _id, String etag, String item, Callback<String> callback) {
        return authHelper.checkValid()
                .thenCompose(result -> patchImp(collection, _id, etag, item, getCallback(callback)));
    }

    private CompletableFuture<String> patchImp(String collection, String _id, String etag, String item, Callback<String> callback) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                Response response = ConnectionManager.getTestHiveVaultApi()
                        .patch_dbCol(collection + "/" + _id, etag, RequestBody.create(MediaType.parse("Content-Type, application/json"), item))
                        .execute();

                int responseCode = checkResponseCode(response);
                if (responseCode == 404) {
                    throw new HiveException(HiveException.ITEM_NOT_FOUND);
                } else if (responseCode != 0) {
                    throw new HiveException(HiveException.ERROR);
                }

                ResponseBody body = (ResponseBody) response.body();
                String ret = body.string();
                callback.onSuccess(ret);
                return ret;
            } catch (Exception e) {
                HiveException exception = new HiveException(e.getLocalizedMessage());
                callback.onError(exception);
                throw new CompletionException(exception);
            }
        });
    }

    @Override
    public CompletableFuture<String> delete(String collection, String _id, String etag) {
        return delete(collection, _id, etag, null);
    }

    @Override
    public CompletableFuture<String> delete(String collection, String _id, String etag, Callback<String> callback) {
        return authHelper.checkValid()
                .thenCompose(result -> deleteImp(collection, _id, etag, getCallback(callback)));
    }

    private CompletableFuture<String> deleteImp(String collection, String _id, String match, Callback<String> callback) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                Response response = ConnectionManager.getTestHiveVaultApi()
                        .delete_dbCol(collection + "/" + _id, match)
                        .execute();

                int responseCode = checkResponseCode(response);
                if (responseCode == 404) {
                    throw new HiveException(HiveException.ITEM_NOT_FOUND);
                } else if (responseCode != 0) {
                    throw new HiveException(HiveException.ERROR);
                }
                ResponseBody body = (ResponseBody) response.body();
                String ret = body.string();
                callback.onSuccess(ret);
                return ret;
            } catch (Exception e) {
                HiveException exception = new HiveException(e.getLocalizedMessage());
                callback.onError(exception);
                throw new CompletionException(exception);
            }
        });
    }

    private RequestBody createWriteRequestBody(byte[] data) {
        return RequestBody.create(MediaType.parse("multipart/form-data"), data);
    }


    private Response getFileOrBuffer(String destFilePath) throws HiveException {
        Response response;
        try {
            response = ConnectionManager.getHiveVaultApi()
                    .downloader(destFilePath)
                    .execute();

        } catch (Exception ex) {
            throw new HiveException(ex.getMessage());
        }
        return response;
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
