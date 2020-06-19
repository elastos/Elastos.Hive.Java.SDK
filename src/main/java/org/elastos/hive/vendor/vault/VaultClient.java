package org.elastos.hive.vendor.vault;

import org.elastos.hive.Authenticator;
import org.elastos.hive.Callback;
import org.elastos.hive.Client;
import org.elastos.hive.NullCallback;
import org.elastos.hive.exception.HiveException;
import org.elastos.hive.interfaces.Files;
import org.elastos.hive.interfaces.IPFS;
import org.elastos.hive.interfaces.KeyValues;
import org.elastos.hive.utils.DigitalUtil;
import org.elastos.hive.utils.ResponseHelper;
import org.elastos.hive.vendor.connection.ConnectionManager;
import org.elastos.hive.vendor.vault.network.VaultApi;
import org.elastos.hive.vendor.vault.network.model.FilesResponse;
import org.json.JSONObject;

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
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okio.Buffer;
import retrofit2.Response;

public class VaultClient extends Client implements Files, KeyValues{

    private Authenticator authenticator;
    private VaultAuthHelper authHelper;

    VaultClient(Client.Options options) {
        VaultOptions opts = (VaultOptions) options;
        authHelper = new VaultAuthHelper(opts.did(),
                opts.password(),
                opts.storePath());
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
                .thenCompose(result -> doPutBuffer(/*toRemoteFilePath(remoteFile)*/remoteFile, data.getBytes(), getCallback(callback)));
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
        MultipartBody.Part multipartBody = MultipartBody.Part.createFormData("file", remoteFile, requestBody);
        Response response = ConnectionManager.getHiveVaultApi()
                .uploader(multipartBody)
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
                .thenCompose(result -> doPutBuffer(/*toRemoteFilePath(remoteFile)*/remoteFile, data, getCallback(callback)));
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
                .thenCompose(result -> doPutInputStream(/*toRemoteFilePath(remoteFile)*/remoteFile, input, getCallback(callback)));
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
        MultipartBody.Part multipartBody = MultipartBody.Part.createFormData("file", destFilePath, requestBody);
        Response response = ConnectionManager.getHiveVaultApi()
                .uploader(multipartBody)
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
                .thenCompose(result -> doPutReader(/*toRemoteFilePath(remoteFile)*/remoteFile, reader, getCallback(callback)));
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
        MultipartBody.Part multipartBody = MultipartBody.Part.createFormData("file", remoteFile, requestBody);
        Response response = ConnectionManager.getHiveVaultApi()
                .uploader(multipartBody)
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
        if (null == remoteFile || remoteFile.isEmpty())
            throw new IllegalArgumentException();

        return authHelper.checkValid()
                .thenCompose(result -> doGetString(/*toRemoteFilePath(remoteFile)*/remoteFile, getCallback(callback)));
    }

    private CompletableFuture<String> doGetString(String remoteFile, Callback<String> callback) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                String result = getString(remoteFile);
                callback.onSuccess(result);
                return result;
            } catch (HiveException e) {
                HiveException exception = new HiveException(e.getLocalizedMessage());
                callback.onError(exception);
                throw new CompletionException(exception);
            }
        });
    }

    private String getString(String remoteFile) throws HiveException {
        byte[] bytes = getBufferImpl(remoteFile);
        if (bytes == null || bytes.length == 0)
            return "";

        return new String(bytes);
    }

    @Override
    public CompletableFuture<byte[]> getAsBuffer(String remoteFile) {
        return getAsBuffer(remoteFile, null);
    }

    @Override
    public CompletableFuture<byte[]> getAsBuffer(String remoteFile, Callback<byte[]> callback) {
        if (null == remoteFile || remoteFile.isEmpty())
            throw new IllegalArgumentException();

        return authHelper.checkValid()
                .thenCompose(result -> doGetBuffer(/*toRemoteFilePath(remoteFile)*/remoteFile, getCallback(callback)));
    }

    private CompletableFuture<byte[]> doGetBuffer(String remoteFile, Callback<byte[]> callback) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                byte[] bytes = getBufferImpl(remoteFile);
                callback.onSuccess(bytes);
                return bytes;
            } catch (HiveException e) {
                HiveException exception = new HiveException(e.getLocalizedMessage());
                callback.onError(exception);
                throw new CompletionException(exception);
            }
        });
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
                .thenCompose(result -> doWriteToOutput(/*toRemoteFilePath(remoteFile)*/remoteFile, output, getCallback(callback)));
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
                .thenCompose(result -> doWriteToWriter(/*toRemoteFilePath(remoteFile)*/remoteFile, writer, getCallback(callback)));
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
                .thenCompose(result -> doDeleteFile(/*toRemoteFilePath(remoteFile)*/remoteFile, getCallback(callback)));
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
        String json = new JSONObject(map).toString();
        Response response = ConnectionManager.getHiveVaultApi()
                .delete(RequestBody.create(MediaType.parse("Content-Type, application/json"), json))
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
                .append(VaultConstance.FILES_ROOT_PATH)
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
        VaultApi api = ConnectionManager.getHiveVaultApi();
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
        return putValue(key, value, null);
    }

    @Override
    public CompletableFuture<Void> putValue(String key, String value, Callback<Void> callback) {
        if (null == key || key.isEmpty() || null == value || value.isEmpty())
            throw new IllegalArgumentException();

        return authHelper.checkValid()
                .thenCompose(result ->
                        doPutValue(key, value.getBytes(), getCallback(callback)));
    }

    private CompletableFuture<Void> doPutValue(String key, byte[] value, Callback<Void> callback) {
        return CompletableFuture.runAsync(() -> {
            try {
                putValueImpl(key, value);
                callback.onSuccess(null);
            } catch (Exception e) {
                HiveException exception = new HiveException(e.getLocalizedMessage());
                callback.onError(exception);
                throw new CompletionException(exception);
            }
        });
    }

    private void putValueImpl(String key, byte[] value) throws Exception {
        RequestBody requestBody = createWriteRequestBody(value);
        MultipartBody.Part multipartBody = MultipartBody.Part.createFormData("file", key, requestBody);
        Response response = ConnectionManager.getHiveVaultApi()
                .uploader(multipartBody)
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

    @Override
    public CompletableFuture<Void> putValue(String key, byte[] value) {
        return putValue(key, value);
    }

    @Override
    public CompletableFuture<Void> putValue(String key, byte[] value, Callback<Void> callback) {
        if (null == key || key.isEmpty() || null == value || value.length == 0)
            throw new IllegalArgumentException();

        return authHelper.checkValid()
                .thenCompose(result ->
                        doPutValue(key, value, getCallback(callback)));
    }

    @Override
    public CompletableFuture<Void> setValue(String key, String value) {
        return setValue(key, value);
    }

    @Override
    public CompletableFuture<Void> setValue(String key, String value, Callback<Void> callback) {
        if (null == key || key.isEmpty() || null == value || value.isEmpty())
            throw new IllegalArgumentException();

        return authHelper.checkValid()
                .thenCompose(result -> doSetValue(toRemoteKeyPath(key), value.getBytes(), getCallback(callback)));
    }

    @Override
    public CompletableFuture<Void> setValue(String key, byte[] value) {
        return setValue(key, value);
    }

    @Override
    public CompletableFuture<Void> setValue(String key, byte[] value, Callback<Void> callback) {
        if (null == key || key.isEmpty() || null == value || value.length == 0)
            throw new IllegalArgumentException();

        return authHelper.checkValid()
                .thenCompose(result -> doSetValue(toRemoteKeyPath(key), value, getCallback(callback)));
    }

    private CompletableFuture<Void> doSetValue(String remoteFile, byte[] data, Callback<Void> callback) {
        return CompletableFuture.runAsync(() -> {
            try {
                deleteFileImpl(remoteFile);
                writeBuffer(remoteFile, mergeLengthAndData(data));
                callback.onSuccess(null);
            } catch (Exception e) {
                HiveException exception = new HiveException(e.getLocalizedMessage());
                callback.onError(exception);
                throw new CompletionException(exception);
            }
        });
    }

    private byte[] mergeLengthAndData(byte[] data) {
        byte[] lengthByte = DigitalUtil.intToByteArray(data.length);
        return mergeData(lengthByte, data);
    }

    private byte[] mergeData(byte[] bytes1, byte[] bytes2) {
        byte[] tmp = new byte[bytes1.length + bytes2.length];
        System.arraycopy(bytes1, 0, tmp, 0, bytes1.length);
        System.arraycopy(bytes2, 0, tmp, bytes1.length, bytes2.length);
        return tmp;
    }

    @Override
    public CompletableFuture<ArrayList<byte[]>> getValues(String key) {
        return getValues(key, null);
    }

    @Override
    public CompletableFuture<ArrayList<byte[]>> getValues(String key, Callback<ArrayList<byte[]>> callback) {
        if (null == key || key.isEmpty())
            throw new IllegalArgumentException();

        return authHelper.checkValid()
                .thenCompose(result -> doGetValue(toRemoteKeyPath(key), getCallback(callback)));
    }

    private String toRemoteKeyPath(String key) {
        StringBuilder builder = new StringBuilder(512)
                .append(VaultConstance.KEYVALUES_ROOT_PATH)
                .append("/")
                .append(key);

        return builder.toString();
    }

    private CompletableFuture<ArrayList<byte[]>> doGetValue(String key, Callback<ArrayList<byte[]>> callback) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                ArrayList<byte[]> list = getValueImpl(key);
                callback.onSuccess(list);
                return list;
            } catch (HiveException e) {
                HiveException exception = new HiveException(e.getLocalizedMessage());
                callback.onError(exception);
                throw new CompletionException(exception);
            }
        });
    }

    private ArrayList<byte[]> getValueImpl(String key) throws HiveException {
        ArrayList<byte[]> arrayList = new ArrayList<>();
        byte[] data = getBufferImpl(key);
        createValueResult(arrayList, data);
        return arrayList;
    }

    private byte[] spliteBytes(byte[] data, int length) {
        byte[] tmp = new byte[length];
        System.arraycopy(data, 4, tmp, 0, length);

        return tmp;
    }

    private int calcuLength(byte[] data) {
        byte[] lengthByte = new byte[4];

        System.arraycopy(data, 0, lengthByte, 0, 4);

        return DigitalUtil.byteArrayToInt(lengthByte);
    }

    private void createValueResult(ArrayList<byte[]> arrayList, byte[] data) {
        int total = data.length;
        int dataLength = calcuLength(data);
        byte[] strbytes = spliteBytes(data, dataLength);
        arrayList.add(strbytes);
        int remainingDataLength = total - (dataLength + 4);
        if (remainingDataLength <= 0)
            return;

        byte[] remainingData = new byte[remainingDataLength];
        System.arraycopy(data, dataLength + 4, remainingData, 0, remainingDataLength);
        createValueResult(arrayList, remainingData);
    }

    private byte[] getBufferImpl(String remoteFile) throws HiveException {
        Response response = getFileOrBuffer(remoteFile);

        int responseCode = checkResponseCode(response);
        if (responseCode == 404) {
            throw new HiveException(HiveException.ITEM_NOT_FOUND);
        } else if (responseCode != 0) {
            throw new HiveException(HiveException.ERROR);
        }

        byte[] bytes = ResponseHelper.getBuffer(response);
        if (bytes == null)
            return new byte[0];

        return bytes;
    }

    @Override
    public CompletableFuture<Void> deleteKey(String key) {
        return deleteKey(key);
    }

    @Override
    public CompletableFuture<Void> deleteKey(String key, Callback<Void> callback) {
        if (null == key || key.isEmpty())
            throw new IllegalArgumentException();

        return authHelper.checkValid()
                .thenCompose(result -> doDeleteFile(toRemoteKeyPath(key), getCallback(callback)));
    }
}
