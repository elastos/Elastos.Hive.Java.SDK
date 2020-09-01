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
import org.elastos.hive.vendor.vault.network.model.PropertiesResponse;

import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.Writer;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
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
            try {

                URL reslUrl = new URL(path);
                HttpURLConnection conn = (HttpURLConnection) reslUrl.openConnection();
                conn.setDoOutput(true);
                conn.setDoInput(true);
                conn.setUseCaches(false);
                conn.setRequestMethod("POST");
                conn.setRequestProperty("Transfer-Encoding","chunked");
                conn.setRequestProperty("Connection", "Keep-Alive");
                conn.setRequestProperty("Charsert", "UTF-8");
                conn.setConnectTimeout(5000);
                conn.setReadTimeout(5000);
                conn.setChunkedStreamingMode(20*1024); //指定流的大小，当内容达到这个值的时候就把流输出

                OutputStream outputStream = conn.getOutputStream();
                OutputStreamWriter outputStreamWriter = new OutputStreamWriter(outputStream);

                return outputStreamWriter;

            } catch (Exception e) {
                HiveException exception = new HiveException(e.getLocalizedMessage());
                callback.onError(exception);
                throw new CompletionException(exception);
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
                callback.onSuccess(null);
                return ResponseHelper.writeToReader(response);
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
                        .deleteFolder(RequestBody.create(MediaType.parse("Content-Type, application/json"), json))
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

    /*
    @Override
    public CompletableFuture<Boolean> createFolder(String folder) {
        return createFolder(folder, null);
    }

    @Override
    public CompletableFuture<Boolean> createFolder(String folder, Callback<Boolean> callback) {
        return authHelper.checkValid()
                .thenCompose(result -> createFolderImp(folder, getCallback(callback)));
    }
	*/

    private CompletableFuture<Boolean> createFolderImp(String folder, Callback<Boolean> callback) {

        return CompletableFuture.supplyAsync(() -> {
            try {
                Map map = new HashMap<>();
                map.put("name", folder);
                String json = JsonUtil.getJsonFromObject(map);
                Response response = ConnectionManager.getHiveVaultApi()
                        .createFolder(RequestBody.create(MediaType.parse("Content-Type, application/json"), json))
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
        return null;
    }

    @Override
    public CompletableFuture<Boolean> copy(String src, String dst) {
        return copy(src, dst, null);
    }

    @Override
    public CompletableFuture<Boolean> copy(String src, String dst, Callback<Boolean> callback) {
        return null;
    }

    @Override
    public CompletableFuture<String> hash(String remoteFile) {
        return hash(remoteFile, null);
    }

    @Override
    public CompletableFuture<String> hash(String remoteFile, Callback<String> callback) {
        return null;
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
                //TODO need node support
//                List<FileInfo> list = new List<FileInfo>(response.body().getFiles());
                List<FileInfo> list = new ArrayList<FileInfo>();
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
        return null;
    }

    @Override
    public CompletableFuture<FileInfo> stat(String path, Callback<FileInfo> callback) {
        return null;
    }

    public CompletableFuture<Long> statImp(String remoteFile, Callback<Long> callback) {
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
