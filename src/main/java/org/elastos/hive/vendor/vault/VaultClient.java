package org.elastos.hive.vendor.vault;

import org.elastos.hive.Authenticator;
import org.elastos.hive.Callback;
import org.elastos.hive.Client;
import org.elastos.hive.NullCallback;
import org.elastos.hive.exception.HiveException;
import org.elastos.hive.interfaces.Database;
import org.elastos.hive.interfaces.Files;
import org.elastos.hive.interfaces.IPFS;
import org.elastos.hive.interfaces.KeyValues;
import org.elastos.hive.interfaces.VaultFiles;
import org.elastos.hive.utils.ResponseHelper;
import org.elastos.hive.vendor.connection.ConnectionManager;
import org.elastos.hive.vendor.vault.network.VaultApi;
import org.elastos.hive.vendor.vault.network.model.FilesResponse;
import org.elastos.hive.vendor.vault.network.model.PropertiesResponse;
import org.elastos.hive.vendor.vault.network.model.UploadResponse;
import org.json.JSONObject;

import java.io.OutputStream;
import java.io.Writer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Response;

public class VaultClient extends Client implements VaultFiles, Database {

    private Authenticator authenticator;
    private VaultAuthHelper authHelper;

    VaultClient(Client.Options options) {
        VaultOptions opts = (VaultOptions) options;
        authHelper = new VaultAuthHelper(opts.nodeUrl(),
                opts.authToken(),
                opts.storePath(),
                opts.clientId(),
                opts.clientSecret(),
                opts.redirectURL(),
                VaultConstance.SCOPE);
        authenticator = opts.authenticator();
    }


    @Override
    public void connect() throws HiveException {
        try {
            authHelper.connectAsync(authenticator).get();
        } catch (Exception e) {
            throw new HiveException(e.getLocalizedMessage());
        }
    }

    @Override
    public void disconnect() {
        authHelper.dissConnect();
    }

    @Override
    public boolean isConnected() {
        return authHelper.getConnectState();
    }

    @Override
    public Files getFiles() {
        return null;
    }


    @Override
    public VaultFiles getVaultFiles() {
        return this;
    }

    @Override
    public IPFS getIPFS() {
        return null;
    }

    @Override
    public Database getDatabase() {
        return this;
    }

    @Override
    public KeyValues getKeyValues() {
        return null;
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

    @Override
    public CompletableFuture<String> createFile(String remoteFile) {
        return authHelper.checkValid()
                .thenCompose(result -> createFileImp(remoteFile, null));
    }

    @Override
    public CompletableFuture<String> createFile(String remoteFile, Callback<Long> callback) {
        return authHelper.checkValid()
                .thenCompose(result -> createFileImp(remoteFile, getCallback(callback)));
    }

    private CompletableFuture<String> createFileImp(String remoteFile, Callback<Long> callback) {

        return CompletableFuture.supplyAsync(() -> {
            try {
                Map map = new HashMap<>();
                map.put("name", remoteFile);
                String json = new JSONObject(map).toString();
                Response<UploadResponse> response = ConnectionManager.getHiveVaultApi()
                        .createFile(RequestBody.create(MediaType.parse("Content-Type, application/json"), json))
                        .execute();
                int responseCode = checkResponseCode(response);
                if (responseCode == 404) {
                    throw new HiveException(HiveException.ITEM_NOT_FOUND);
                } else if (responseCode != 0) {
                    throw new HiveException(HiveException.ERROR);
                }
                callback.onSuccess(null);
                return response.body().getUpload_file_url();
            } catch (Exception e) {
                HiveException exception = new HiveException(e.getLocalizedMessage());
                callback.onError(exception);
                throw new CompletionException(exception);
            }
        });

    }

    @Override
    public CompletableFuture<Void> upload(String url, byte[] data, String remoteFile) {
        return upload(url, data, remoteFile, null);

    }

    @Override
    public CompletableFuture<Void> upload(String url, byte[] data, String remoteFile, Callback<Long> callback) {
        return authHelper.checkValid()
                .thenCompose(result -> uploadImp(url, remoteFile, data, callback));
    }

    private CompletableFuture<Void> uploadImp(String url, String remoteFile, byte[] data, Callback<Long> callback) {

        return CompletableFuture.runAsync(() -> {
            try {
                RequestBody requestBody = createWriteRequestBody(data);
                MultipartBody.Part multipartBody = MultipartBody.Part.createFormData("file", remoteFile, requestBody);
                Response response = ConnectionManager.getHiveVaultApi()
                        .uploadFile(url, multipartBody)
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
    public CompletableFuture<Long> downloader(String remoteFile, OutputStream output) {
        return downloader(remoteFile, output, null);
    }

    @Override
    public CompletableFuture<Long> downloader(String remoteFile, OutputStream output, Callback<Long> callback) {
        return authHelper.checkValid()
                .thenCompose(result -> downloaderStreamImp(remoteFile, output, getCallback(callback)));
    }

    private CompletableFuture<Long> downloaderStreamImp(String remoteFile, OutputStream output, Callback<Long> callback) {

        return CompletableFuture.supplyAsync(() -> {
            try {
                Response response = getFileOrBuffer(remoteFile);

                if (response == null)
                    throw new HiveException(HiveException.ERROR);

                int responseCode = checkResponseCode(response);
                if (responseCode == 404) {
                    throw new HiveException(HiveException.ITEM_NOT_FOUND);
                } else if (responseCode != 0) {
                    throw new HiveException(HiveException.ERROR);
                }
                ResponseBody body = (ResponseBody) response.body();
                callback.onSuccess(null);
                return ResponseHelper.writeOutput(response, output);
            } catch (Exception e) {
                HiveException exception = new HiveException(e.getLocalizedMessage());
                callback.onError(exception);
                throw new CompletionException(exception);
            }
        });

    }

    @Override
    public CompletableFuture<Long> downloader(String remoteFile, Writer writer) {
        return downloader(remoteFile, writer, null);
    }

    @Override
    public CompletableFuture<Long> downloader(String remoteFile, Writer writer, Callback<Long> callback) {
        return authHelper.checkValid()
                .thenCompose(result -> downloaderWriterImp(remoteFile, writer, getCallback(callback)));
    }

    private CompletableFuture<Long> downloaderWriterImp(String remoteFile, Writer writer, Callback<Long> callback) {

        return CompletableFuture.supplyAsync(() -> {
            try {
                Response response = getFileOrBuffer(remoteFile);

                if (response == null)
                    throw new HiveException(HiveException.ERROR);

                int responseCode = checkResponseCode(response);
                if (responseCode == 404) {
                    throw new HiveException(HiveException.ITEM_NOT_FOUND);
                } else if (responseCode != 0) {
                    throw new HiveException(HiveException.ERROR);
                }
                callback.onSuccess(null);
                return ResponseHelper.writeDataToWriter(response, writer);
            } catch (Exception e) {
                HiveException exception = new HiveException(e.getLocalizedMessage());
                callback.onError(exception);
                throw new CompletionException(exception);
            }
        });

    }

    @Override
    public CompletableFuture<Void> deleteFile(String remoteFile) {
        return deleteFile(remoteFile, null);
    }

    @Override
    public CompletableFuture<Void> deleteFile(String remoteFile, Callback<Long> callback) {
        return authHelper.checkValid()
                .thenCompose(result -> deleteFileImp(remoteFile, getCallback(callback)));
    }

    private CompletableFuture<Void> deleteFileImp(String remoteFile, Callback<Long> callback) {
        return CompletableFuture.runAsync(() -> {
            try {
                Map map = new HashMap<>();
                map.put("name", remoteFile);
                String json = new JSONObject(map).toString();
                Response response = ConnectionManager.getHiveVaultApi()
                        .deleteFolder(RequestBody.create(MediaType.parse("Content-Type, application/json"), json))
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
    public CompletableFuture<Void> createFolder(String folder) {
        return createFolder(folder, null);
    }

    @Override
    public CompletableFuture<Void> createFolder(String folder, Callback<Long> callback) {
        return null;
    }

    @Override
    public CompletableFuture<Void> move(String src, String dst) {
        return move(src, dst, null);
    }

    @Override
    public CompletableFuture<Void> move(String src, String dst, Callback<Long> callback) {
        return null;
    }

    @Override
    public CompletableFuture<Void> copy(String src, String dst) {
        return copy(src, dst, null);
    }

    @Override
    public CompletableFuture<Void> copy(String src, String dst, Callback<Long> callback) {
        return null;
    }

    @Override
    public CompletableFuture<Void> hash(String remoteFile) {
        return hash(remoteFile, null);
    }

    @Override
    public CompletableFuture<Void> hash(String remoteFile, Callback<Long> callback) {
        return null;
    }

    @Override
    public CompletableFuture<ArrayList<String>> list(String folder) {
        return list(folder, null);
    }

    @Override
    public CompletableFuture<ArrayList<String>> list(String folder, Callback<Long> callback) {
        return authHelper.checkValid()
                .thenCompose(result -> listImp(folder, getCallback(callback)));
    }

    private CompletableFuture<ArrayList<String>> listImp(String folder, Callback<Long> callback) {

        return CompletableFuture.supplyAsync(() -> {
            try {
                VaultApi api = ConnectionManager.getHiveVaultApi();
                Response<FilesResponse> response = api.files(folder).execute();

                int responseCode = checkResponseCode(response);
                if (responseCode == 404) {
                    throw new HiveException(HiveException.ITEM_NOT_FOUND);
                } else if (responseCode != 0) {
                    throw new HiveException(HiveException.ERROR);
                }
                callback.onSuccess(null);
                return new ArrayList<>(response.body().getFiles());
            } catch (Exception e) {
                HiveException exception = new HiveException(e.getLocalizedMessage());
                callback.onError(exception);
                throw new CompletionException(exception);
            }
        });
    }

    @Override
    public CompletableFuture<Long> size(String remoteFile) {
        return authHelper.checkValid()
                .thenCompose(result -> sizeImp(remoteFile, null));
    }

    @Override
    public CompletableFuture<Long> size(String remoteFile, Callback<Long> callback) {
        return authHelper.checkValid()
                .thenCompose(result -> sizeImp(remoteFile, getCallback(callback)));
    }

    public CompletableFuture<Long> sizeImp(String remoteFile, Callback<Long> callback) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                VaultApi api = ConnectionManager.getHiveVaultApi();
                Response<PropertiesResponse> response = api.getProperties(remoteFile).execute();

                int responseCode = checkResponseCode(response);
                if (responseCode == 404) {
                    throw new HiveException(HiveException.ITEM_NOT_FOUND);
                } else if (responseCode != 0) {
                    throw new HiveException(HiveException.ERROR);
                }
                callback.onSuccess(null);
                return response.body().getSt_size();
            } catch (Exception e) {
                HiveException exception = new HiveException(e.getLocalizedMessage());
                callback.onError(exception);
                throw new CompletionException(exception);
            }
        });
    }

    @Override
    public CompletableFuture<Void> createCol(String collection, String schema) {
        return createCol(collection, schema, null);
    }

    @Override
    public CompletableFuture<Void> createCol(String collection, String schema, Callback<Long> callback) {
        return authHelper.checkValid()
                .thenCompose(result -> createColImp(collection, schema, getCallback(callback)));
    }

    private CompletableFuture<Void> createColImp(String collection, String schema, Callback<Long> callback) {
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
    public CompletableFuture<Void> dropCol(String collection) {
        return dropCol(collection, null);
    }

    @Override
    public CompletableFuture<Void> dropCol(String collection, Callback<Long> callback) {
        return authHelper.checkValid()
                .thenCompose(result -> dropColImp(collection, getCallback(callback)));
    }

    private CompletableFuture<Void> dropColImp(String collection, Callback<Long> callback) {
        return CompletableFuture.runAsync(() -> {
            try {
                Map map = new HashMap<>();
                map.put("collection", collection);
                String json = new JSONObject(map).toString();
                Response response = ConnectionManager.getTestHiveVaultApi()
                        .auth(RequestBody.create(MediaType.parse("Content-Type, application/json"), json))
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
    public CompletableFuture<String> insert(String collection, String item, Callback<Long> callback) {
        return authHelper.checkValid()
                .thenCompose(result -> insertImp(collection, item, getCallback(callback)));
    }

    private CompletableFuture<String> insertImp(String collection, String item, Callback<Long> callback) {
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
                callback.onSuccess(null);
                ResponseBody body = (ResponseBody) response.body();
                return body.string();
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
    public CompletableFuture<String> query(String collection, String where, Callback<Long> callback) {
        return authHelper.checkValid()
                .thenCompose(result -> queryImp(collection, where, getCallback(callback)));
    }

    private CompletableFuture<String> queryImp(String collection, String item, Callback<Long> callback) {
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
                callback.onSuccess(null);
                ResponseBody body = (ResponseBody) response.body();
                return body.string();
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
    public CompletableFuture<String> put(String collection, String _id, String etag, String item, Callback<Long> callback) {
        return authHelper.checkValid()
                .thenCompose(result -> putImp(collection, _id, etag, item, getCallback(callback)));
    }

    private CompletableFuture<String> putImp(String collection, String _id, String etag, String item, Callback<Long> callback) {
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
                callback.onSuccess(null);
                ResponseBody body = (ResponseBody) response.body();
                return body.string();
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
    public CompletableFuture<String> patch(String collection, String _id, String etag, String item, Callback<Long> callback) {
        return authHelper.checkValid()
                .thenCompose(result -> patchImp(collection, _id, etag, item, getCallback(callback)));
    }

    private CompletableFuture<String> patchImp(String collection, String _id, String etag, String item, Callback<Long> callback) {
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
                return body.string();
            } catch (Exception e) {
                HiveException exception = new HiveException(e.getLocalizedMessage());
                throw new CompletionException(exception);
            }
        });
    }

    @Override
    public CompletableFuture<String> delete(String collection, String _id, String etag) {
        return delete(collection, _id, etag, null);
    }

    @Override
    public CompletableFuture<String> delete(String collection, String _id, String etag, Callback<Long> callback) {
        return authHelper.checkValid()
                .thenCompose(result -> deleteImp(collection, _id, etag, getCallback(callback)));
    }

    private CompletableFuture<String> deleteImp(String collection, String _id, String match, Callback<Long> callback) {
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
                callback.onSuccess(null);
                ResponseBody body = (ResponseBody) response.body();
                return body.string();
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
