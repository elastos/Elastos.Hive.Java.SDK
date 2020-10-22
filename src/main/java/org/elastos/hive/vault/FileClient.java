package org.elastos.hive.vault;

import org.elastos.hive.Files;
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
    public <T> CompletableFuture<T> upload(String path, Class<T> resultType) {
        return authHelper.checkValid()
                .thenCompose(result -> uploadImp(path, resultType));
    }

    private <T> CompletableFuture<T> uploadImp(String path, Class<T> resultType) {

        return CompletableFuture.supplyAsync(() -> {

            HttpURLConnection httpURLConnection = null;
            try {
                httpURLConnection = ConnectionManager.openURLConnection(path);
                OutputStream rawOutputStream = httpURLConnection.getOutputStream();

                if(null == rawOutputStream) return null;

                UploadOutputStream outputStream = new UploadOutputStream(httpURLConnection, rawOutputStream);

                if(resultType.isAssignableFrom(OutputStream.class)) {
                    return (T) outputStream;
                } else {
                    OutputStreamWriter outputStreamWriter = new OutputStreamWriter(outputStream);
                    return (T) outputStreamWriter;
                }
            } catch (Exception e) {
                ResponseHelper.readConnection(httpURLConnection);
                HiveException exception = new HiveException(e.getLocalizedMessage());
                throw new CompletionException(exception);
            }
        });
    }

    @Override
    public <T> CompletableFuture<T> download(String path, Class<T> resultType) {
        return authHelper.checkValid()
                .thenCompose(result -> downloadImp(path, resultType));
    }

    private <T> CompletableFuture<T> downloadImp(String remoteFile, Class<T> resultType) {

        return CompletableFuture.supplyAsync(() -> {
            try {
                Response response = getFileOrBuffer(remoteFile);

                if (response == null)
                    throw new HiveException(HiveException.ERROR);

                authHelper.checkResponseCode(response);
                if(resultType.isAssignableFrom(Reader.class)) {
                    Reader reader = ResponseHelper.getToReader(response);
                    return (T) reader;
                } else {
                    InputStream inputStream = ResponseHelper.getInputStream(response);
                    return (T) inputStream;
                }
            } catch (Exception e) {
                HiveException exception = new HiveException(e.getLocalizedMessage());
                throw new CompletionException(exception);
            }
        });

    }

    @Override
    public CompletableFuture<Boolean> delete(String remoteFile) {
        return authHelper.checkValid()
                .thenCompose(result -> deleteImp(remoteFile));
    }

    private CompletableFuture<Boolean> deleteImp(String remoteFile) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                Map map = new HashMap<>();
                map.put("path", remoteFile);
                String json = JsonUtil.getJsonFromObject(map);

                Response response = ConnectionManager.getHiveVaultApi()
                        .deleteFolder(createJsonRequestBody(json))
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
    public CompletableFuture<Boolean> move(String src, String dst) {
        return authHelper.checkValid()
                .thenCompose(result -> moveImp(src, dst));
    }

    private CompletableFuture<Boolean> moveImp(String src, String dst) {

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
                return true;
            } catch (Exception e) {
                HiveException exception = new HiveException(e.getLocalizedMessage());
                throw new CompletionException(exception);
            }
        });
    }

    @Override
    public CompletableFuture<Boolean> copy(String src, String dst) {
        return authHelper.checkValid()
                .thenCompose(result -> copyImp(src, dst));
    }

    private CompletableFuture<Boolean> copyImp(String src, String dst) {

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
                return true;
            } catch (Exception e) {
                HiveException exception = new HiveException(e.getLocalizedMessage());
                throw new CompletionException(exception);
            }
        });
    }

    @Override
    public CompletableFuture<String> hash(String remoteFile) {
        return authHelper.checkValid()
                .thenCompose(result -> hashImp(remoteFile));
    }

    private CompletableFuture<String> hashImp(String remoteFile) {

        return CompletableFuture.supplyAsync(() -> {
            try {
                Response<HashResponse> response = ConnectionManager.getHiveVaultApi()
                        .hash(remoteFile)
                        .execute();
                authHelper.checkResponseCode(response);
                String ret = response.body().getSHA256();
                return ret;
            } catch (Exception e) {
                HiveException exception = new HiveException(e.getLocalizedMessage());
                throw new CompletionException(exception);
            }
        });
    }

    @Override
    public CompletableFuture<List<FileInfo>> list(String folder) {
        return authHelper.checkValid()
                .thenCompose(result -> listImp(folder));
    }

    private CompletableFuture<List<FileInfo>> listImp(String folder) {

        return CompletableFuture.supplyAsync(() -> {
            try {
                NodeApi api = ConnectionManager.getHiveVaultApi();
                Response<FilesResponse> response = api.files(folder).execute();

                authHelper.checkResponseCode(response);
                List<FileInfo> list = response.body().getFiles();
                return list;
            } catch (Exception e) {
                HiveException exception = new HiveException(e.getLocalizedMessage());
                throw new CompletionException(exception);
            }
        });
    }

    @Override
    public CompletableFuture<FileInfo> stat(String path) {
        return authHelper.checkValid()
                .thenCompose(result -> statImp(path));
    }

    public CompletableFuture<FileInfo> statImp(String path) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                NodeApi api = ConnectionManager.getHiveVaultApi();
                Response<FileInfo> response = api.getProperties(path).execute();

                authHelper.checkResponseCode(response);
                FileInfo fileInfo = response.body();
                return fileInfo;
            } catch (Exception e) {
                HiveException exception = new HiveException(e.getLocalizedMessage());
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
}
