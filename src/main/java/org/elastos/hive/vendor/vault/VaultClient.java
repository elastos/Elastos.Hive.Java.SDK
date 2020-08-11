package org.elastos.hive.vendor.vault;

import org.elastos.hive.Authenticator;
import org.elastos.hive.Client;
import org.elastos.hive.exception.HiveException;
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
import retrofit2.Response;

public class VaultClient extends Client implements VaultFiles {

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
    public KeyValues getKeyValues() {
        return null;
    }

    private RequestBody createWriteRequestBody(byte[] data) {
        return RequestBody.create(MediaType.parse("multipart/form-data"), data);
    }



    private Response getFileOrBuffer(String destFilePath) throws HiveException {
        Response<okhttp3.ResponseBody> response;
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
                .thenCompose(result -> createFileImp(remoteFile));
    }

    private CompletableFuture<String> createFileImp(String remoteFile) {

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
                return response.body().getUpload_file_url();
            } catch (Exception e) {
                HiveException exception = new HiveException(e.getLocalizedMessage());
                throw new CompletionException(exception);
            }
        });

    }

    @Override
    public CompletableFuture<Void> upload(String url, byte[] data, String remoteFile) {

        return authHelper.checkValid()
                .thenCompose(result -> uploadImp(url, remoteFile, data));

    }

    private CompletableFuture<Void> uploadImp(String url, String remoteFile, byte[] data) {

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
            } catch (Exception e) {
                HiveException exception = new HiveException(e.getLocalizedMessage());
                throw new CompletionException(exception);
            }
        });
    }

    @Override
    public CompletableFuture<Long> downloader(String remoteFile, OutputStream output) {
        return downloaderStreamImp(remoteFile, output);
    }

    private CompletableFuture<Long> downloaderStreamImp(String remoteFile, OutputStream output) {

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

                return ResponseHelper.writeOutput(response, output);
            } catch (Exception e) {
                HiveException exception = new HiveException(e.getLocalizedMessage());
                throw new CompletionException(exception);
            }
        });

    }

    @Override
    public CompletableFuture<Long> downloader(String remoteFile, Writer writer) {
        return downloaderWriterImp(remoteFile, writer);
    }

    private CompletableFuture<Long> downloaderWriterImp(String remoteFile, Writer writer) {

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

                return ResponseHelper.writeDataToWriter(response, writer);
            } catch (Exception e) {
                HiveException exception = new HiveException(e.getLocalizedMessage());
                throw new CompletionException(exception);
            }
        });

    }

    @Override
    public CompletableFuture<Void> deleteFile(String remoteFile) {
        return deleteFileImp(remoteFile);
    }

    private CompletableFuture<Void> deleteFileImp(String remoteFile) {
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
            } catch (Exception e) {
                HiveException exception = new HiveException(e.getLocalizedMessage());
                throw new CompletionException(exception);
            }
        });
    }

    @Override
    public CompletableFuture<Void> createFolder(String folder) {
        return null;
    }

    @Override
    public CompletableFuture<Void> move(String src, String dst) {
        return null;
    }

    @Override
    public CompletableFuture<Void> copy(String src, String dst) {
        return null;
    }

    @Override
    public CompletableFuture<Void> hash(String remoteFile) {
        return null;
    }

    @Override
    public CompletableFuture<ArrayList<String>> list(String folder) {
        return authHelper.checkValid()
                .thenCompose(result -> listImp(folder));
    }

    private CompletableFuture<ArrayList<String>> listImp(String folder) {

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

                return new ArrayList<>(response.body().getFiles());
            } catch (Exception e) {
                HiveException exception = new HiveException(e.getLocalizedMessage());
                throw new CompletionException(exception);
            }
        });
    }

    @Override
    public CompletableFuture<Long> size(String remoteFile) {
        return sizeImp(remoteFile);
    }

    public CompletableFuture<Long> sizeImp(String remoteFile) {
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

                return response.body().getSt_size();
            } catch (Exception e) {
                HiveException exception = new HiveException(e.getLocalizedMessage());
                throw new CompletionException(exception);
            }
        });
    }
}
