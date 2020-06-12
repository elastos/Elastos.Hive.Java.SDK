package org.elastos.hive.vendor.hivevault;

import org.elastos.hive.Authenticator;
import org.elastos.hive.Callback;
import org.elastos.hive.Client;
import org.elastos.hive.NullCallback;
import org.elastos.hive.exception.HiveException;
import org.elastos.hive.interfaces.Files;
import org.elastos.hive.interfaces.IPFS;
import org.elastos.hive.interfaces.KeyValues;
import org.elastos.hive.utils.ResponseHelper;
import org.elastos.hive.vendor.connection.ConnectionManager;
import org.elastos.hive.vendor.hivevault.network.HiveVaultApi;
import org.elastos.hive.vendor.hivevault.network.model.FilesResponse;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Reader;
import java.io.Writer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import okio.Buffer;
import retrofit2.Response;

public class HiveVaultClient extends Client implements Files, KeyValues{

    private Authenticator authenticator;
    private HiveVaultAuthHelper authHelper;

    HiveVaultClient(Client.Options options) {
        HiveVaultOptions opts = (HiveVaultOptions) options;
        authHelper = new HiveVaultAuthHelper(opts.did(),
                opts.password(),
                opts.storePath(),
                ((HiveVaultOptions) options).expiration());
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
        return this;
    }

    @Override
    public IPFS getIPFS() {
        throw new UnsupportedOperationException();
    }

    @Override
    public KeyValues getKeyValues() {
        return this;
    }

    @Override
    public CompletableFuture<Void> put(String data, String remoteFile) {
        return put(data, remoteFile, null);
    }

    @Override
    public CompletableFuture<Void> put(String data, String remoteFile, Callback<Void> callback) {
        if (null == data || data.isEmpty() || null == remoteFile || remoteFile.isEmpty())
            throw new IllegalArgumentException();

        return authHelper.checkValid()
                .thenCompose(result -> doPutBuffer(toRemoteFilePath(remoteFile), data.getBytes(), getCallback(callback)));
    }

    private CompletableFuture<Void> doPutBuffer(String destFilePath, byte[] data, Callback<Void> callback) {
        return CompletableFuture.runAsync(() -> {
            try {
                writeBuffer(destFilePath, data);
                callback.onSuccess(null);
            } catch (Exception e) {
                HiveException exception = new HiveException(e.getLocalizedMessage());
                callback.onError(exception);
                throw new CompletionException(exception);
            }
        });
    }

    private void writeBuffer(String remoteFile, byte[] data) throws Exception {
        RequestBody requestBody = createWriteRequestBody(data);
        Response response = ConnectionManager.getHiveVaultApi()
                .uploader(remoteFile, requestBody)
                .execute();
        if (response == null)
            throw new HiveException(HiveException.ERROR);

        int responseCode = checkResponseCode(response);
        if (responseCode == 404) {
            throw new HiveException(HiveException.ITEM_NOT_FOUND);
        } else if (responseCode != 0) {
            throw new HiveException(HiveException.ERROR);
        }
    }

    private RequestBody createWriteRequestBody(byte[] data) {
        return RequestBody.create(MediaType.parse("multipart/form-data"), data);
    }

    @Override
    public CompletableFuture<Void> put(byte[] data, String remoteFile) {
        return put(data, remoteFile, null);
    }

    @Override
    public CompletableFuture<Void> put(byte[] data, String remoteFile, Callback<Void> callback) {
        if (null == data || data.length == 0 || null == remoteFile || remoteFile.isEmpty())
            throw new IllegalArgumentException();

        return authHelper.checkValid()
                .thenCompose(result -> doPutBuffer(toRemoteFilePath(remoteFile), data, getCallback(callback)));
    }

    @Override
    public CompletableFuture<Void> put(InputStream input, String remoteFile) {
        return put(input, remoteFile, null);
    }

    @Override
    public CompletableFuture<Void> put(InputStream input, String remoteFile, Callback<Void> callback) {
        if (null == input || null == remoteFile || remoteFile.isEmpty())
            throw new IllegalArgumentException();

        return authHelper.checkValid()
                .thenCompose(result -> doPutInputStream(toRemoteFilePath(remoteFile), input, getCallback(callback)));
    }

    private CompletableFuture<Void> doPutInputStream(String destFilePath, InputStream inputStream, Callback<Void> callback) {
        return CompletableFuture.runAsync(() -> {
            try {
                writeInputStream(destFilePath, inputStream);
                callback.onSuccess(null);
            } catch (Exception e) {
                HiveException exception = new HiveException(e.getLocalizedMessage());
                callback.onError(exception);
                throw new CompletionException(exception);
            }
        });
    }

    private void writeInputStream(String destFilePath, InputStream inputStream) throws Exception {
        RequestBody requestBody = createWriteRequestBody(inputStream);
        Response response = ConnectionManager.getHiveVaultApi()
                .uploader(destFilePath, requestBody)
                .execute();

        int responseCode = checkResponseCode(response);
        if (responseCode == 404) {
            throw new HiveException(HiveException.ITEM_NOT_FOUND);
        } else if (responseCode != 0) {
            throw new HiveException(HiveException.ERROR);
        }
    }

    private RequestBody createWriteRequestBody(InputStream inputStream) throws IOException {
        Buffer buffer = new Buffer();
        byte[] cache = new byte[1024];
        int len;
        while ((len = inputStream.read(cache)) != -1) {
            buffer.write(cache, 0, len);
        }
        return createWriteRequestBody(buffer.readByteArray());
    }

    @Override
    public CompletableFuture<Void> put(Reader reader, String remoteFile) {
        return put(reader, remoteFile);
    }

    @Override
    public CompletableFuture<Void> put(Reader reader, String remoteFile, Callback<Void> callback) {
        if (null == reader || null == remoteFile || remoteFile.isEmpty())
            throw new IllegalArgumentException();

        return authHelper.checkValid()
                .thenCompose(result -> doPutReader(toRemoteFilePath(remoteFile), reader, getCallback(callback)));
    }

    private CompletableFuture<Void> doPutReader(String remoteFile, Reader reader, Callback<Void> callback) {
        return CompletableFuture.runAsync(() -> {
            try {
                writeReader(remoteFile, reader);
                callback.onSuccess(null);
            } catch (Exception e) {
                HiveException exception = new HiveException(e.getLocalizedMessage());
                callback.onError(exception);
                throw new CompletionException(exception);
            }
        });
    }

    private void writeReader(String remoteFile, Reader reader) throws Exception {
        RequestBody requestBody = createWriteRequestBody(reader);
        Response response = ConnectionManager.getHiveVaultApi()
                .uploader(remoteFile, requestBody)
                .execute();

        int responseCode = checkResponseCode(response);
        if (responseCode == 404) {
            throw new HiveException(HiveException.ITEM_NOT_FOUND);
        } else if (responseCode != 0) {
            throw new HiveException(HiveException.ERROR);
        }
    }

    private RequestBody createWriteRequestBody(Reader reader) throws IOException {
        return createWriteRequestBody(transReader(reader).toString().getBytes());
    }

    private StringBuffer transReader(Reader reader) throws IOException {
        BufferedReader bufferedReader = new BufferedReader(reader);
        StringBuffer stringBuffer = new StringBuffer();
        String line;
        boolean flag = false;
        while ((line = bufferedReader.readLine()) != null) {
            if (!flag) {
                flag = true;
                stringBuffer.append(line);
            } else {
                stringBuffer.append("\n");
                stringBuffer.append(line);
            }

        }
        reader.close();
        return stringBuffer;
    }

    @Override
    public CompletableFuture<Long> size(String remoteFile) {
        return size(remoteFile, null);
    }

    @Override
    public CompletableFuture<Long> size(String remoteFile, Callback<Long> callback) {
        return null;
    }

    @Override
    public CompletableFuture<String> getAsString(String remoteFile) {
        return getAsString(remoteFile, null);
    }

    @Override
    public CompletableFuture<String> getAsString(String remoteFile, Callback<String> callback) {
        return null;
    }

    @Override
    public CompletableFuture<byte[]> getAsBuffer(String remoteFile) {
        return getAsBuffer(remoteFile, null);
    }

    @Override
    public CompletableFuture<byte[]> getAsBuffer(String remoteFile, Callback<byte[]> callback) {
        return null;
    }

    @Override
    public CompletableFuture<Long> get(String remoteFile, OutputStream output) {
        return get(remoteFile, output, null);
    }

    @Override
    public CompletableFuture<Long> get(String remoteFile, OutputStream output, Callback<Long> callback) {
        if (null == remoteFile || remoteFile.isEmpty() || null == output)
            throw new IllegalArgumentException();

        return authHelper.checkValid()
                .thenCompose(result -> doWriteToOutput(toRemoteFilePath(remoteFile), output, getCallback(callback)));
    }

    private CompletableFuture<Long> doWriteToOutput(String remoteFile, OutputStream outputStream, Callback<Long> callback) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                long length = writeToOutput(remoteFile, outputStream);
                callback.onSuccess(length);
                return length;
            } catch (Exception e) {
                HiveException exception = new HiveException(e.getLocalizedMessage());
                callback.onError(exception);
                throw new CompletionException(exception);
            }
        });
    }

    private long writeToOutput(String remoteFile, OutputStream outputStream) throws Exception {
        Response response = getFileOrBuffer(remoteFile);
        if (response == null)
            throw new HiveException(HiveException.ERROR);

        int responseCode = checkResponseCode(response);
        if (responseCode == 404) {
            throw new HiveException(HiveException.ITEM_NOT_FOUND);
        } else if (responseCode != 0) {
            throw new HiveException(HiveException.ERROR);
        }

        return ResponseHelper.writeOutput(response, outputStream);
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

    @Override
    public CompletableFuture<Long> get(String remoteFile, Writer writer) {
        return get(remoteFile, writer, null);
    }

    @Override
    public CompletableFuture<Long> get(String remoteFile, Writer writer, Callback<Long> callback) {
        if (null == remoteFile || remoteFile.isEmpty() || null == writer)
            throw new IllegalArgumentException();

        return authHelper.checkValid()
                .thenCompose(result -> doWriteToWriter(toRemoteFilePath(remoteFile), writer, getCallback(callback)));
    }

    private CompletableFuture<Long> doWriteToWriter(String remoteFile, Writer writer, Callback<Long> callback) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                long length = writeToWriter(remoteFile, writer);
                callback.onSuccess(length);
                return length;
            } catch (Exception e) {
                HiveException exception = new HiveException(e.getLocalizedMessage());
                callback.onError(exception);
                throw new CompletionException(exception);
            }
        });
    }

    private long writeToWriter(String remoteFile, Writer writer) throws Exception {
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
    }

    @Override
    public CompletableFuture<Void> delete(String remoteFile) {
        return delete(remoteFile, null);
    }

    @Override
    public CompletableFuture<Void> delete(String remoteFile, Callback<Void> callback) {
        if (null == remoteFile || remoteFile.isEmpty())
            throw new IllegalArgumentException();

        return authHelper.checkValid()
                .thenCompose(result -> doDeleteFile(toRemoteFilePath(remoteFile), getCallback(callback)));
    }

    private CompletableFuture<Void> doDeleteFile(String destFilePath, Callback<Void> callback) {
        return CompletableFuture.runAsync(() -> {
            try {
                deleteFileImpl(destFilePath);
                callback.onSuccess(null);
            } catch (Exception e) {
                HiveException exception = new HiveException(e.getLocalizedMessage());
                callback.onError(exception);
                throw new CompletionException(exception);
            }
        });
    }

    private void deleteFileImpl(String remoteFile) throws Exception {
        Map map = new HashMap<>();
        map.put("file_name", remoteFile);
        Response response = ConnectionManager.getHiveVaultApi()
                .delete(map)
                .execute();
        int responseCode = checkResponseCode(response);
        if (responseCode == 404) {
            throw new HiveException(HiveException.ITEM_NOT_FOUND);
        } else if (responseCode != 0) {
            throw new HiveException(HiveException.ERROR);
        }
    }

    @Override
    public CompletableFuture<ArrayList<String>> list() {
        return list(null);
    }

    @Override
    public CompletableFuture<ArrayList<String>> list(Callback<ArrayList<String>> callback) {
        return authHelper.checkValid()
                .thenCompose(result -> doListFile(getCallback(callback)));
    }

    private String toRemoteFilePath(String destFileName) {
        StringBuilder builder = new StringBuilder(512)
                .append(HiveVaultConstance.FILES_ROOT_PATH)
                .append("/")
                .append(destFileName);

        return builder.toString();
    }

    private CompletableFuture<ArrayList<String>> doListFile(Callback<ArrayList<String>> callback) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                ArrayList<String> list = listFile();
                callback.onSuccess(list);
                return list;
            } catch (Exception e) {
                HiveException exception = new HiveException(e.getLocalizedMessage());
                callback.onError(exception);
                throw new CompletionException(exception);
            }
        });
    }

    private ArrayList<String> listFile() throws Exception {
        HiveVaultApi api = ConnectionManager.getHiveVaultApi();
        Response<FilesResponse> response = api.files().execute();

        int responseCode = checkResponseCode(response);
        if (responseCode == 404) {
            throw new HiveException(HiveException.ITEM_NOT_FOUND);
        } else if (responseCode != 0) {
            throw new HiveException(HiveException.ERROR);
        }

        return new ArrayList<>(response.body().getFiles());
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

    @Override
    public CompletableFuture<Void> putValue(String key, String value) {
        return null;
    }

    @Override
    public CompletableFuture<Void> putValue(String key, String value, Callback<Void> callback) {
        return null;
    }

    @Override
    public CompletableFuture<Void> putValue(String key, byte[] value) {
        return null;
    }

    @Override
    public CompletableFuture<Void> putValue(String key, byte[] value, Callback<Void> callback) {
        return null;
    }

    @Override
    public CompletableFuture<Void> setValue(String key, String value) {
        return null;
    }

    @Override
    public CompletableFuture<Void> setValue(String key, String value, Callback<Void> callback) {
        return null;
    }

    @Override
    public CompletableFuture<Void> setValue(String key, byte[] value) {
        return null;
    }

    @Override
    public CompletableFuture<Void> setValue(String key, byte[] value, Callback<Void> callback) {
        return null;
    }

    @Override
    public CompletableFuture<ArrayList<byte[]>> getValues(String key) {
        return null;
    }

    @Override
    public CompletableFuture<ArrayList<byte[]>> getValues(String key, Callback<ArrayList<byte[]>> callback) {
        return null;
    }

    @Override
    public CompletableFuture<Void> deleteKey(String key) {
        return null;
    }

    @Override
    public CompletableFuture<Void> deleteKey(String key, Callback<Void> callback) {
        return null;
    }
}
