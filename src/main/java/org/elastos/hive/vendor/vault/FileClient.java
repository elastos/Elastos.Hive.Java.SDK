package org.elastos.hive.vendor.vault;

import org.elastos.hive.Callback;
import org.elastos.hive.Files;
import org.elastos.hive.NullCallback;
import org.elastos.hive.exception.HiveException;
import org.elastos.hive.file.FileInfo;
import org.elastos.hive.utils.JsonUtil;
import org.elastos.hive.utils.ResponseHelper;
import org.elastos.hive.vendor.connection.ConnectionManager;
import org.elastos.hive.vendor.vault.network.VaultApi;
import org.elastos.hive.vendor.vault.network.model.FilesResponse;

import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.Writer;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import retrofit2.Response;

public class FileClient implements Files {

    private VaultAuthHelper authHelper;

    public FileClient(VaultAuthHelper authHelper) {
        this.authHelper = authHelper;
    }

    @Override
    public CompletableFuture<Writer> upload(String path) {
        return upload(path, null);

    }

    @Override
    public CompletableFuture<Writer> upload(String path, Callback<Writer> callback) {
        return authHelper.checkValid()
                .thenCompose(result -> uploadImp(path, callback));
    }

    private CompletableFuture<Writer> uploadImp(String path, Callback<Writer> callback) {

        return CompletableFuture.supplyAsync(() -> {
            HttpURLConnection httpURLConnection = null;
            try {
                String url = ConnectionManager.getHivevaultBaseUrl()+ "/api/v1/files/upload/"+ path;
                URL reslUrl = new URL(url);
                httpURLConnection = (HttpURLConnection) reslUrl.openConnection();

                httpURLConnection.setRequestMethod("POST");
                httpURLConnection.setConnectTimeout(5000);
                httpURLConnection.setReadTimeout(5000);

                httpURLConnection.setDoOutput(true);
                httpURLConnection.setDoInput(true);
                httpURLConnection.setUseCaches(false);
                httpURLConnection.setRequestProperty("Transfer-Encoding", "chunked");
                httpURLConnection.setRequestProperty("Connection", "Keep-Alive");
                httpURLConnection.setRequestProperty("Charsert", "UTF-8");
                httpURLConnection.setRequestProperty("Authorization", "token " + ConnectionManager.getAccessToken());
                httpURLConnection.setChunkedStreamingMode(4096); //指定流的大小，当内容达到这个值的时候就把流输出

//                String method = httpURLConnection.getRequestMethod();
//                Map headers = httpURLConnection.getHeaderFields();
//                String contentType = httpURLConnection.getContentType();
//                Map properties = httpURLConnection.getRequestProperties();

                OutputStream outputStream = httpURLConnection.getOutputStream();
                OutputStreamWriter outputStreamWriter = new OutputStreamWriter(outputStream);

                callback.onSuccess(outputStreamWriter);
                return outputStreamWriter;
            } catch (Exception e) {
                HiveException exception = new HiveException(e.getLocalizedMessage());
                callback.onError(exception);
                throw new CompletionException(exception);
            } finally {
                if (httpURLConnection != null)
                    httpURLConnection.disconnect();
            }
        });
    }

    @Override
    public CompletableFuture<Reader> download(String path) {
        return download(path, null);
    }

    @Override
    public CompletableFuture<Reader> download(String path, Callback<Reader> callback) {
        return authHelper.checkValid()
                .thenCompose(result -> downloadImp(path, getCallback(callback)));
    }

    private CompletableFuture<Reader> downloadImp(String remoteFile, Callback<Reader> callback) {

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
                Reader reader = ResponseHelper.writeToReader(response);
                callback.onSuccess(reader);
                return reader;
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
                map.put("name", remoteFile);
                String json = JsonUtil.getJsonFromObject(map);

                Response response = ConnectionManager.getHiveVaultApi()
                        .deleteFolder(createJsonRequestBody(json))
                        .execute();
                int responseCode = checkResponseCode(response);
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
    public CompletableFuture<Boolean> move(String src, String dst) {
        return move(src, dst, null);
    }

    @Override
    public CompletableFuture<Boolean> move(String src, String dst, Callback<Boolean> callback) {
        return moveImp(src, dst, getCallback(callback));
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
                int responseCode = checkResponseCode(response);
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
    public CompletableFuture<Boolean> copy(String src, String dst) {
        return copy(src, dst, null);
    }

    @Override
    public CompletableFuture<Boolean> copy(String src, String dst, Callback<Boolean> callback) {
        return copyImp(src, dst, getCallback(callback));
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
                int responseCode = checkResponseCode(response);
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
    public CompletableFuture<String> hash(String remoteFile) {
        return hash(remoteFile, null);
    }

    @Override
    public CompletableFuture<String> hash(String remoteFile, Callback<String> callback) {
        return hashImp(remoteFile, getCallback(callback));
    }

    private CompletableFuture<String> hashImp(String remoteFile, Callback<String> callback) {

        return CompletableFuture.supplyAsync(() -> {
            try {
                Response response = ConnectionManager.getHiveVaultApi()
                        .hash(remoteFile)
                        .execute();
                int responseCode = checkResponseCode(response);
                if (responseCode == 404) {
                    throw new HiveException(HiveException.ITEM_NOT_FOUND);
                } else if (responseCode != 0) {
                    throw new HiveException(HiveException.ERROR);
                }
                String ret = ResponseHelper.toString(response);
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
                VaultApi api = ConnectionManager.getHiveVaultApi();
                Response<FilesResponse> response = api.files(folder).execute();

                int responseCode = checkResponseCode(response);
                if (responseCode == 404) {
                    throw new HiveException(HiveException.ITEM_NOT_FOUND);
                } else if (responseCode != 0) {
                    throw new HiveException(HiveException.ERROR);
                }

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
        return statImp(path, getCallback(callback));
    }

    public CompletableFuture<FileInfo> statImp(String path, Callback<FileInfo> callback) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                VaultApi api = ConnectionManager.getHiveVaultApi();
                Response<FileInfo> response = api.getProperties(path).execute();

                int responseCode = checkResponseCode(response);
                if (responseCode == 404) {
                    throw new HiveException(HiveException.ITEM_NOT_FOUND);
                } else if (responseCode != 0) {
                    throw new HiveException(HiveException.ERROR);
                }
                callback.onSuccess(null);
                return response.body();
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
