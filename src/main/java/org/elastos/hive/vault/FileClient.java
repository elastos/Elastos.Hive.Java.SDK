package org.elastos.hive.vault;

import org.elastos.hive.Callback;
import org.elastos.hive.Files;
import org.elastos.hive.NullCallback;
import org.elastos.hive.exception.HiveException;
import org.elastos.hive.file.FileInfo;
import org.elastos.hive.utils.JsonUtil;
import org.elastos.hive.utils.ResponseHelper;
import org.elastos.hive.connection.ConnectionManager;
import org.elastos.hive.vault.network.NodeApi;
import org.elastos.hive.vault.network.model.FilesResponse;
import org.elastos.hive.vault.network.model.HashResponse;
import org.elastos.hive.vault.network.model.UploadOutputStream;

import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.net.HttpURLConnection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import retrofit2.Response;

public class FileClient implements Files {

    private AuthHelper authHelper;

    public FileClient(AuthHelper authHelper) {
        this.authHelper = authHelper;
    }

    @Override
    public <T> CompletableFuture<T> upload(String path, Class<T> resultType) throws HiveException {
        return upload(path, resultType, null);
    }

    @Override
    public <T> CompletableFuture<T> upload(String path, Class<T> resultType, Callback<T> callback) throws HiveException {
        return authHelper.checkValid()
                .thenCompose(result -> uploadImp(path, resultType, getCallback(callback)));
    }

    private <T> CompletableFuture<T> uploadImp(String path, Class<T> resultType, Callback<T> callback) {

        return CompletableFuture.supplyAsync(() -> {

            HttpURLConnection httpURLConnection = null;
            try {
                httpURLConnection = ConnectionManager.openURLConnection(path);
                OutputStream rawOutputStream = httpURLConnection.getOutputStream();

                if(null == rawOutputStream) return null;

                UploadOutputStream outputStream = new UploadOutputStream(httpURLConnection, rawOutputStream);

                if(resultType.isAssignableFrom(OutputStream.class)) {
                    callback.onSuccess((T) outputStream);
                    return (T) outputStream;
                } else {
                    OutputStreamWriter outputStreamWriter = new OutputStreamWriter(outputStream);
                    callback.onSuccess((T) outputStreamWriter);
                    return (T) outputStreamWriter;
                }
            } catch (Exception e) {
                ResponseHelper.readConnection(httpURLConnection);
                HiveException exception = new HiveException(e.getLocalizedMessage());
                callback.onError(exception);
                throw new CompletionException(exception);
            }
        });
    }

    @Override
    public <T> CompletableFuture<T> download(String path, Class<T> resultType) throws HiveException {
        return download(path, resultType, null);
    }

    @Override
    public <T> CompletableFuture<T> download(String path, Class<T> resultType, Callback<T> callback) throws HiveException {
        return authHelper.checkValid()
                .thenCompose(result -> downloadImp(path, resultType, getCallback(callback)));
    }

    private <T> CompletableFuture<T> downloadImp(String remoteFile, Class<T> resultType, Callback<T> callback) {

        return CompletableFuture.supplyAsync(() -> {
            try {
                Response response = getFileOrBuffer(remoteFile);

                if (response == null)
                    throw new HiveException(HiveException.ERROR);

                authHelper.checkResponseCode(response);
                if(resultType.isAssignableFrom(Reader.class)) {
                    Reader reader = ResponseHelper.getToReader(response);
                    callback.onSuccess((T) reader);
                    return (T) reader;
                } else {
                    InputStream inputStream = ResponseHelper.getInputStream(response);
                    callback.onSuccess((T) inputStream);
                    return (T) inputStream;
                }
            } catch (Exception e) {
                HiveException exception = new HiveException(e.getLocalizedMessage());
                callback.onError(exception);
                throw new CompletionException(exception);
            }
        });

    }

    @Override
    public CompletableFuture<Boolean> delete(String remoteFile) {
        return delete(remoteFile, null);
    }

    @Override
    public CompletableFuture<Boolean> delete(String remoteFile, Callback<Boolean> callback) {
        return authHelper.checkValid()
                .thenCompose(result -> deleteImp(remoteFile, getCallback(callback)));
    }

    private CompletableFuture<Boolean> deleteImp(String remoteFile, Callback<Boolean> callback) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                Map map = new HashMap<>();
                map.put("path", remoteFile);
                String json = JsonUtil.getJsonFromObject(map);

                Response response = ConnectionManager.getHiveVaultApi()
                        .deleteFolder(createJsonRequestBody(json))
                        .execute();
                authHelper.checkResponseCode(response);
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
    public CompletableFuture<Boolean> move(String src, String dst) {
        return move(src, dst, null);
    }

    @Override
    public CompletableFuture<Boolean> move(String src, String dst, Callback<Boolean> callback) {
        return authHelper.checkValid()
                .thenCompose(result -> moveImp(src, dst, getCallback(callback)));
    }

    private CompletableFuture<Boolean> moveImp(String src, String dst, Callback<Boolean> callback) {

        return CompletableFuture.supplyAsync(() -> {
            try {
                Map map = new HashMap<>();
                map.put("src_path", src);
                map.put("dst_path", dst);
                String json = JsonUtil.getJsonFromObject(map);
                Response response = ConnectionManager.getHiveVaultApi()
                        .move(createJsonRequestBody(json))
                        .execute();
                authHelper.checkResponseCode(response);
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
    public CompletableFuture<Boolean> copy(String src, String dst) {
        return copy(src, dst, null);
    }

    @Override
    public CompletableFuture<Boolean> copy(String src, String dst, Callback<Boolean> callback) {
        return authHelper.checkValid()
                .thenCompose(result -> copyImp(src, dst, getCallback(callback)));
    }

    private CompletableFuture<Boolean> copyImp(String src, String dst, Callback<Boolean> callback) {

        return CompletableFuture.supplyAsync(() -> {
            try {
                Map map = new HashMap<>();
                map.put("src_path", src);
                map.put("dst_path", dst);
                String json = JsonUtil.getJsonFromObject(map);
                Response response = ConnectionManager.getHiveVaultApi()
                        .copy(createJsonRequestBody(json))
                        .execute();
                authHelper.checkResponseCode(response);
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
    public CompletableFuture<String> hash(String remoteFile) {
        return hash(remoteFile, null);
    }

    @Override
    public CompletableFuture<String> hash(String remoteFile, Callback<String> callback) {
        return authHelper.checkValid()
                .thenCompose(result -> hashImp(remoteFile, getCallback(callback)));
    }

    private CompletableFuture<String> hashImp(String remoteFile, Callback<String> callback) {

        return CompletableFuture.supplyAsync(() -> {
            try {
                Response<HashResponse> response = ConnectionManager.getHiveVaultApi()
                        .hash(remoteFile)
                        .execute();
                authHelper.checkResponseCode(response);
                String ret = response.body().getSHA256();
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
    public CompletableFuture<List<FileInfo>> list(String folder) {
        return list(folder, null);
    }

    @Override
    public CompletableFuture<List<FileInfo>> list(String folder, Callback<List<FileInfo>> callback) {
        return authHelper.checkValid()
                .thenCompose(result -> listImp(folder, getCallback(callback)));
    }

    private CompletableFuture<List<FileInfo>> listImp(String folder, Callback<List<FileInfo>> callback) {

        return CompletableFuture.supplyAsync(() -> {
            try {
                NodeApi api = ConnectionManager.getHiveVaultApi();
                Response<FilesResponse> response = api.files(folder).execute();

                authHelper.checkResponseCode(response);
                List<FileInfo> list = response.body().getFiles();
                callback.onSuccess(list);
                return list;
            } catch (Exception e) {
                HiveException exception = new HiveException(e.getLocalizedMessage());
                callback.onError(exception);
                throw new CompletionException(exception);
            }
        });
    }

    @Override
    public CompletableFuture<FileInfo> stat(String path) {
        return stat(path, null);
    }

    @Override
    public CompletableFuture<FileInfo> stat(String path, Callback<FileInfo> callback) {
        return authHelper.checkValid()
                .thenCompose(result -> statImp(path, getCallback(callback)));
    }

    public CompletableFuture<FileInfo> statImp(String path, Callback<FileInfo> callback) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                NodeApi api = ConnectionManager.getHiveVaultApi();
                Response<FileInfo> response = api.getProperties(path).execute();

                authHelper.checkResponseCode(response);
                FileInfo fileInfo = response.body();
                callback.onSuccess(fileInfo);
                return fileInfo;
            } catch (Exception e) {
                HiveException exception = new HiveException(e.getLocalizedMessage());
                callback.onError(exception);
                throw new CompletionException(exception);
            }
        });
    }

    private RequestBody createJsonRequestBody(String json) {
        return RequestBody.create(MediaType.parse("Content-Type, application/json"), json);
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

    private <T> Callback<T> getCallback(Callback<T> callback) {
        return (null == callback ? new NullCallback<T>() : callback);
    }
}
