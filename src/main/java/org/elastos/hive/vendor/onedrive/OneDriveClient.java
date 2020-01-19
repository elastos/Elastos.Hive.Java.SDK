package org.elastos.hive.vendor.onedrive;

import org.elastos.hive.Authenticator;
import org.elastos.hive.Callback;
import org.elastos.hive.Client;
import org.elastos.hive.NullCallback;
import org.elastos.hive.exception.HiveException;
import org.elastos.hive.interfaces.Files;
import org.elastos.hive.interfaces.IPFS;
import org.elastos.hive.interfaces.KeyValues;
import org.elastos.hive.utils.DigitalUtil;
import org.elastos.hive.utils.HeaderUtil;
import org.elastos.hive.utils.ResponseHelper;
import org.elastos.hive.vendor.connection.ConnectionManager;
import org.elastos.hive.vendor.connection.InputStreamRequestBody;
import org.elastos.hive.vendor.onedrive.network.OneDriveApi;
import org.elastos.hive.vendor.onedrive.network.model.DirChildrenResponse;
import org.elastos.hive.vendor.onedrive.network.model.FileOrDirPropResponse;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Reader;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okio.Buffer;
import retrofit2.Response;

final class OneDriveClient extends Client implements Files, KeyValues {
    Authenticator authenticator;
    OneDriveAuthHelper authHelper;

    OneDriveClient(Client.Options options) {
        OneDriveOptions oneDriveOptions = (OneDriveOptions) options;
        authHelper = new OneDriveAuthHelper(oneDriveOptions.clientId(),
                OneDriveConstance.appScope, oneDriveOptions.redirectUrl(), oneDriveOptions.storePath());
        authenticator = oneDriveOptions.authenticator();
    }

    @Override
    public void connect() throws HiveException {
        try {
            authHelper.loginAsync(authenticator).get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
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
        return null;
    }

    @Override
    public KeyValues getKeyValues() {
        return this;
    }

    @Override
    public CompletableFuture<Void> put(byte[] from, String remoteFile) {
        return put(from, remoteFile, new NullCallback<>());
    }

    @Override
    public CompletableFuture<Void> put(byte[] data, String remoteFile, Callback<Void> callback) {
        // TODO:
        return authHelper.checkExpired()
                .thenCompose(result -> writeToBackend(creatDestFilePath(remoteFile), null, data, callback));
    }

    @Override
    public CompletableFuture<Void> put(String data, String remoteFile) {
        return put(data, remoteFile, new NullCallback<>());
    }

    @Override
    public CompletableFuture<Void> put(String data, String remoteFile, Callback<Void> callback) {
        // TODO;
        return authHelper.checkExpired()
                .thenCompose(result -> writeToBackend(creatDestFilePath(remoteFile), null, data.getBytes(), callback));
    }

    @Override
    public CompletableFuture<Void> put(InputStream input, String remoteFile) {
        return put(input, remoteFile, new NullCallback<>());
    }

    @Override
    public CompletableFuture<Void> put(InputStream input, String remoteFile, Callback<Void> callback) {
        return authHelper.checkExpired()
                .thenCompose(result -> doPutInputStream(creatDestFilePath(remoteFile), input, callback));
    }

    @Override
    public CompletableFuture<Void> put(Reader reader, String remoteFile) {
        return put(reader, remoteFile, new NullCallback<>());
    }

    @Override
    public CompletableFuture<Void> put(Reader reader, String remoteFile, Callback<Void> callback) {
        // TODO;
        return authHelper.checkExpired()
                .thenCompose(result -> doputReader(creatDestFilePath(remoteFile), reader, callback));
    }

    @Override
    public CompletableFuture<Long> size(String remoteFile) {
        return size(remoteFile, new NullCallback<>());
    }

    @Override
    public CompletableFuture<Long> size(String remoteFile, Callback<Long> callback) {
        return authHelper.checkExpired()
                .thenCompose(result -> doGetFileLength(creatDestFilePath(remoteFile), callback));
    }

    @Override
    public CompletableFuture<String> getAsString(String remoteFile) {
        return getAsString(remoteFile, new NullCallback<>());
    }

    @Override
    public CompletableFuture<String> getAsString(String remoteFile, Callback<String> callback) {
        return authHelper.checkExpired()
                .thenCompose(result -> doGetString(creatDestFilePath(remoteFile), callback));
    }

    @Override
    public CompletableFuture<byte[]> getAsBuffer(String remoteFile) {
        return getAsBuffer(remoteFile, new NullCallback<>());
    }

    @Override
    public CompletableFuture<byte[]> getAsBuffer(String remoteFile, Callback<byte[]> callback) {
        return authHelper.checkExpired()
                .thenCompose(result -> doGetBuffer(creatDestFilePath(remoteFile), callback));
    }


    @Override
    public CompletableFuture<Void> delete(String remoteFile) {
        return delete(remoteFile, new NullCallback<>());
    }

    @Override
    public CompletableFuture<Void> delete(String remoteFile, Callback<Void> callback) {
        return authHelper.checkExpired()
                .thenCompose(result -> doDeleteFile(creatDestFilePath(remoteFile), callback));
    }

    @Override
    public CompletableFuture<ArrayList<String>> list() {
        return list(new NullCallback<>());
    }

    @Override
    public CompletableFuture<ArrayList<String>> list(Callback<ArrayList<String>> callback) {
        return authHelper.checkExpired()
                .thenCompose(result -> doListFile(callback));
    }

    @Override
    public CompletableFuture<Long> get(String remoteFile, OutputStream output) {
        return get(remoteFile, output, new NullCallback<>());
    }

    @Override
    public CompletableFuture<Long> get(String remoteFile, OutputStream output, Callback<Long> callback) {
        return authHelper.checkExpired()
                .thenCompose(result -> doWriteToOutput(creatDestFilePath(remoteFile), output, callback));
    }

    @Override
    public CompletableFuture<Long> get(String remoteFile, Writer writer) {
        return get(remoteFile, writer, new NullCallback<>());
    }

    @Override
    public CompletableFuture<Long> get(String remoteFile, Writer writer, Callback<Long> callback) {
        return authHelper.checkExpired()
                .thenCompose(result -> doWriteToWriter(creatDestFilePath(remoteFile), writer, callback));
    }

    @Override
    public CompletableFuture<Void> putValue(String key, String value) {
        return putValue(key, value, new NullCallback<>());
    }

    @Override
    public CompletableFuture<Void> putValue(String key, String value, Callback<Void> callback) {
        return authHelper.checkExpired()
                .thenCompose(result -> doPutValue(creatDestKeyPath(key), value.getBytes(), callback));
    }

    @Override
    public CompletableFuture<Void> putValue(String key, byte[] value) {
        return putValue(key, value, new NullCallback<>());
    }

    @Override
    public CompletableFuture<Void> putValue(String key, byte[] value, Callback<Void> callback) {
        return authHelper.checkExpired()
                .thenCompose(result -> doPutValue(creatDestKeyPath(key), value, callback));
    }

    @Override
    public CompletableFuture<Void> setValue(String key, String value) {
        return setValue(key, value, new NullCallback<>());
    }

    @Override
    public CompletableFuture<Void> setValue(String key, String value, Callback<Void> callback) {
        return authHelper.checkExpired()
                .thenCompose(result -> doDeleteFile(creatDestKeyPath(key), new NullCallback()))
                .thenCompose(result -> doMergeLengthAndData(value.getBytes())
                        .thenCompose(data -> writeToBackend(creatDestKeyPath(key), null, data, callback)));
    }

    @Override
    public CompletableFuture<Void> setValue(String key, byte[] value) {
        return setValue(key, value, new NullCallback<>());
    }

    @Override
    public CompletableFuture<Void> setValue(String key, byte[] value, Callback<Void> callback) {
        return authHelper.checkExpired()
                .thenCompose(result -> doDeleteFile(creatDestKeyPath(key), new NullCallback()))
                .thenCompose(result -> doMergeLengthAndData(value)
                        .thenCompose(data -> writeToBackend(creatDestKeyPath(key), null, data, callback)));
    }

    @Override
    public CompletableFuture<ArrayList<byte[]>> getValues(String key) {
        return getValues(key, new NullCallback<>());
    }

    @Override
    public CompletableFuture<ArrayList<byte[]>> getValues(String key, Callback<ArrayList<byte[]>> callback) {
        return authHelper.checkExpired()
                .thenCompose(result -> doGetValue(creatDestKeyPath(key), callback));
    }

    @Override
    public CompletableFuture<Void> deleteKey(String key) {
        return deleteKey(key, new NullCallback<>());
    }

    @Override
    public CompletableFuture<Void> deleteKey(String key, Callback<Void> callback) {
        return authHelper.checkExpired()
                .thenCompose(result -> doDeleteFile(creatDestKeyPath(key), callback));
    }

////

    private CompletableFuture<ArrayList<byte[]>> doGetValue(String key, Callback<ArrayList<byte[]>> callback) {
        CompletableFuture<ArrayList<byte[]>> future = new CompletableFuture<>();
        ArrayList<byte[]> arrayList = new ArrayList<>();
        try {
            CompletableFuture<byte[]> innerFuture = doGetBuffer(key, new Callback<byte[]>() {
                @Override
                public void onError(HiveException e) {
                    if (callback != null) callback.onError(e);
                    future.completeExceptionally(e);
                }

                @Override
                public void onSuccess(byte[] body) {
                }
            });

            byte[] data = innerFuture.get();
            if (innerFuture.isCompletedExceptionally()) return future;

            createValueResult(arrayList, data);

//            ArrayList<byte[]> valueList = new ArrayList<>(arrayList);
            if (callback != null) callback.onSuccess(arrayList);
            future.complete(arrayList);
        } catch (Exception e) {
            e.printStackTrace();
            HiveException exception = new HiveException(HiveException.GET_VALUE_ERROR);
            if (callback != null) callback.onError(exception);
            future.completeExceptionally(exception);
        }
        return future;
    }

    private String creatDestFilePath(String destFileName) {
        return "/Files/" + destFileName;
    }

    private String creatDestKeyPath(String key) {
        return "/KeyValues/" + key;
    }

    private CompletableFuture<Void> doputReader(String remoteFile, Reader reader, Callback<Void> callback) {
        CompletableFuture<Void> future = new CompletableFuture<>();

        if (reader == null) {
            HiveException exception = new HiveException(HiveException.PUT_FILE_ERROR);
            if (callback != null) callback.onError(exception);
            future.completeExceptionally(exception);
            return future;
        }
        try {
            RequestBody requestBody = createWriteRequestBody(reader);
            Response response = ConnectionManager.getOnedriveApi()
                    .write(remoteFile, requestBody)
                    .execute();
            int checkResponseCode = checkResponseCode(response);
            if (checkResponseCode == 0) {
                Void result = null;
                if (callback != null) callback.onSuccess(result);
                future.complete(result);
            } else if (checkResponseCode == 404) {
                HiveException exception = new HiveException(HiveException.ITEM_NOT_FOUND);
                if (callback != null) callback.onError(exception);
                future.completeExceptionally(exception);
            } else {
                HiveException exception = new HiveException(HiveException.PUT_FILE_ERROR);
                if (callback != null) callback.onError(exception);
                future.completeExceptionally(exception);
                return future;
            }
        } catch (Exception e) {
            HiveException exception = new HiveException(HiveException.PUT_FILE_ERROR);
            if (callback != null) callback.onError(exception);
            future.completeExceptionally(exception);
            e.printStackTrace();
        }
        return future;
    }

    private CompletableFuture<Void> doPutInputStream(String destFilePath, InputStream inputStream, org.elastos.hive.Callback<Void> callback) {
        CompletableFuture<Void> future = new CompletableFuture<>();

        if (inputStream == null) {
            HiveException exception = new HiveException(HiveException.PUT_FILE_ERROR);
            if (callback != null) callback.onError(exception);
            future.completeExceptionally(exception);
            return future;
        }
        try {
            RequestBody requestBody = createWriteRequestBody(inputStream);
            Response response = ConnectionManager.getOnedriveApi()
                    .write(destFilePath, requestBody)
                    .execute();
            int checkResponseCode = checkResponseCode(response);
            if (checkResponseCode == 0) {
                Void result = null;
                if (callback != null) callback.onSuccess(result);
                future.complete(result);
            } else if (checkResponseCode == 404) {
                HiveException exception = new HiveException(HiveException.ITEM_NOT_FOUND);
                if (callback != null) callback.onError(exception);
                future.completeExceptionally(exception);
            } else {
                HiveException exception = new HiveException(HiveException.PUT_FILE_ERROR);
                if (callback != null) callback.onError(exception);
                future.completeExceptionally(exception);
                return future;
            }
        } catch (Exception e) {
            HiveException exception = new HiveException(HiveException.PUT_FILE_ERROR);
            if (callback != null) callback.onError(exception);
            future.completeExceptionally(exception);
            e.printStackTrace();
        }
        return future;
    }


    private CompletableFuture<Void> writeToBackend(String destFilePath, String pathname, byte[] data, org.elastos.hive.Callback<Void> callback) {
        CompletableFuture<Void> future = new CompletableFuture<>();

        if (pathname == null && data == null) {
            HiveException exception = new HiveException(HiveException.PUT_FILE_ERROR);
            if (callback != null) callback.onError(exception);
            future.completeExceptionally(exception);
            return future;
        }
        RequestBody requestBody = null;
        if (pathname != null) {
            File file = new File(pathname);
            requestBody = createWriteRequestBody(file);
        } else {
            requestBody = createWriteRequestBody(data);
        }
        try {
            Response response = ConnectionManager.getOnedriveApi()
                    .write(destFilePath, requestBody)
                    .execute();
            int checkResponseCode = checkResponseCode(response);
            if (checkResponseCode == 0) {
                Void result = null;
                if (callback != null) callback.onSuccess(result);
                future.complete(result);
            } else if (checkResponseCode == 404) {
                HiveException exception = new HiveException(HiveException.ITEM_NOT_FOUND);
                if (callback != null) callback.onError(exception);
                future.completeExceptionally(exception);
            } else {
                HiveException exception = new HiveException(HiveException.PUT_FILE_ERROR);
                if (callback != null) callback.onError(exception);
                future.completeExceptionally(exception);
                return future;
            }
        } catch (Exception e) {
            HiveException exception = new HiveException(HiveException.PUT_FILE_ERROR);
            if (callback != null) callback.onError(exception);
            future.completeExceptionally(exception);
            e.printStackTrace();
        }
        return future;
    }

    private RequestBody createWriteRequestBody(File file) {
        return RequestBody.create(MediaType.parse("multipart/form-data"), file);
    }

    private RequestBody createWriteRequestBody(byte[] data) {
        return RequestBody.create(MediaType.parse("multipart/form-data"), data);
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

    private CompletableFuture<Long> doGetFileLength(String destFilePath, org.elastos.hive.Callback<Long> callback) {
        CompletableFuture<Long> future = new CompletableFuture();
        try {
            Response response = requestFileInfo(destFilePath);
            int checkResponseCode = checkResponseCode(response);
            if (checkResponseCode == 0) {
                int fileLength = decodeFileLength(response);
//                Length length = new Length(fileLength);
                if (callback != null) callback.onSuccess((long) fileLength);
                future.complete((long) fileLength);
            } else if (checkResponseCode == 404) {
                HiveException exception = new HiveException(HiveException.ITEM_NOT_FOUND);
                if (callback != null) callback.onError(exception);
                future.completeExceptionally(exception);
                return future;
            } else {
                HiveException exception = new HiveException(HiveException.GET_FILE_LENGTH_ERROR);
                if (callback != null) callback.onError(exception);
                future.completeExceptionally(exception);
                return future;
            }
        } catch (Exception e) {
            HiveException exception = new HiveException(HiveException.GET_FILE_LENGTH_ERROR);
            if (callback != null) callback.onError(exception);
            future.completeExceptionally(exception);
            e.printStackTrace();
        }
        return future;
    }

    private CompletableFuture<Void> doDeleteFile(String destFilePath, org.elastos.hive.Callback callback) {
        CompletableFuture<Void> future = new CompletableFuture<>();
        try {
            Response response = ConnectionManager.getOnedriveApi()
                    .deleteItem(destFilePath)
                    .execute();
            int checkResponseCode = checkResponseCode(response);
            if (checkResponseCode == 0) {
//                Void result = new Void();
                if (callback != null) callback.onSuccess(null);
                future.complete(null);
            } else if (checkResponseCode == 404) {
                HiveException exception = new HiveException(HiveException.ITEM_NOT_FOUND);
                if (callback != null) callback.onError(exception);
                future.completeExceptionally(exception);
                return future;
            } else {
                HiveException hiveException = new HiveException(HiveException.DEL_FILE_ERROR);
                if (callback != null) callback.onError(hiveException);
                future.completeExceptionally(hiveException);
                return future;
            }
        } catch (Exception ex) {
            HiveException exception = new HiveException(HiveException.DEL_FILE_ERROR);
            if (callback != null) callback.onError(exception);
            future.completeExceptionally(exception);
            ex.printStackTrace();
        }
        return future;
    }

    private CompletableFuture<Long> doGetFile(String filename, String storePath, org.elastos.hive.Callback callback) {
        CompletableFuture<Long> future = new CompletableFuture<>();

        try {
            checkFileExist(storePath);
        } catch (HiveException e) {
            HiveException exception = new HiveException(HiveException.FILE_ALREADY_EXIST_ERROR);
            callback.onError(exception);
            future.completeExceptionally(exception);
            e.printStackTrace();
        }

        try {
            Response response = getFileOrBuffer(filename);
            int checkResponseCode = checkResponseCode(response);
            if (checkResponseCode == 404) {
                HiveException exception = new HiveException(HiveException.ITEM_NOT_FOUND);
                if (callback != null) callback.onError(exception);
                future.completeExceptionally(exception);
                return future;
            } else if (checkResponseCode != 0 ||
                    (HeaderUtil.getContentLength(response) == -1
                            && !HeaderUtil.isTrunced(response))) {
                HiveException hiveException = new HiveException(HiveException.DEL_FILE_ERROR);
                if (callback != null) callback.onError(hiveException);
                future.completeExceptionally(hiveException);
                return future;
            }
            long total = ResponseHelper.saveFileFromResponse(storePath, response);
//            Length lengthObj = new Length(total);
            if (callback != null) callback.onSuccess(total);
            future.complete(total);
        } catch (HiveException e) {
            HiveException exception = new HiveException(HiveException.GET_FILE_ERROR);
            if (callback != null) callback.onError(exception);
            future.completeExceptionally(exception);
            e.printStackTrace();
        }
        return future;
    }

    private CompletableFuture<byte[]> doGetBuffer(String fileName, org.elastos.hive.Callback<byte[]> callback) {
        CompletableFuture<byte[]> future = new CompletableFuture<>();
        try {
            Response response = getFileOrBuffer(fileName);

            int checkResponseCode = checkResponseCode(response);
            if (checkResponseCode == 404) {
                HiveException exception = new HiveException(HiveException.ITEM_NOT_FOUND);
                if (callback != null) callback.onError(exception);
                future.completeExceptionally(exception);
                return future;
            } else if (checkResponseCode != 0) {
                HiveException hiveException = new HiveException(HiveException.GET_BUFFER_ERROR);
                if (callback != null) callback.onError(hiveException);
                future.completeExceptionally(hiveException);
                return future;
            }

            byte[] bytes = ResponseHelper.getBuffer(response);
            if (bytes == null) {
//                Data data = new Data(new byte[0]);
                byte[] data = new byte[0];
                if (callback != null) callback.onSuccess(data);
                future.complete(data);
                return future;
            }

            if (callback != null) callback.onSuccess(bytes);
            future.complete(bytes);
        } catch (HiveException e) {
            HiveException exception = new HiveException(HiveException.GET_BUFFER_ERROR);
            if (callback != null) callback.onError(exception);
            future.completeExceptionally(exception);
            e.printStackTrace();
        }
        return future;
    }

    private CompletableFuture<String> doGetString(String fileName, Callback<String> callback) {
        CompletableFuture<String> future = new CompletableFuture<>();
        try {
            Response response = getFileOrBuffer(fileName);

            int checkResponseCode = checkResponseCode(response);
            if (checkResponseCode == 404) {
                HiveException exception = new HiveException(HiveException.ITEM_NOT_FOUND);
                if (callback != null) callback.onError(exception);
                future.completeExceptionally(exception);
                return future;
            } else if (checkResponseCode != 0) {
                HiveException hiveException = new HiveException(HiveException.GET_BUFFER_ERROR);
                if (callback != null) callback.onError(hiveException);
                future.completeExceptionally(hiveException);
                return future;
            }

            byte[] bytes = ResponseHelper.getBuffer(response);
            if (bytes == null || bytes.length == 0) {
//                Data data = new Data(new byte[0]);
//                byte[] data = new byte[0];
                if (callback != null) callback.onSuccess("");
                future.complete("");
                return future;
            }

            String result = new String(bytes);
            if (callback != null) callback.onSuccess(result);
            future.complete(result);
        } catch (HiveException e) {
            HiveException exception = new HiveException(HiveException.GET_BUFFER_ERROR);
            if (callback != null) callback.onError(exception);
            future.completeExceptionally(exception);
            e.printStackTrace();
        }
        return future;
    }

    private CompletableFuture<Long> doWriteToOutput(String remoteFile, OutputStream output, Callback<Long> callback) {
        CompletableFuture<Long> future = new CompletableFuture<>();
        try {
            Response response = getFileOrBuffer(remoteFile);
            if (response != null) {
                long length = ResponseHelper.writeOutput(response, output);
                if (callback != null) callback.onSuccess(length);
                future.complete(length);
            } else {
                HiveException hiveException = new HiveException(HiveException.GET_FILE_ERROR);
                if (callback != null) callback.onError(hiveException);
                future.completeExceptionally(hiveException);
            }
        } catch (Exception e) {
            HiveException hiveException = new HiveException(HiveException.GET_FILE_ERROR);
            if (callback != null) callback.onError(hiveException);
            future.completeExceptionally(hiveException);
            e.printStackTrace();
        }

        return future;
    }

    private CompletableFuture<Long> doWriteToWriter(String remoteFile, Writer writer, Callback<Long> callback) {
        CompletableFuture<Long> future = new CompletableFuture<>();
        try {
            Response response = getFileOrBuffer(remoteFile);
            if (response != null) {
                long length = ResponseHelper.writeDataToWriter(response, writer);
                if (callback != null) callback.onSuccess(length);
                future.complete(length);
            } else {
                HiveException hiveException = new HiveException(HiveException.GET_FILE_ERROR);
                if (callback != null) callback.onError(hiveException);
                future.completeExceptionally(hiveException);
            }
        } catch (Exception e) {
            HiveException hiveException = new HiveException(HiveException.GET_FILE_ERROR);
            if (callback != null) callback.onError(hiveException);
            future.completeExceptionally(hiveException);
            e.printStackTrace();
        }
        return future;
    }

    private void checkFileExist(String storePath) throws HiveException {
        if (storePath != null) {
            File cacheFile = new File(storePath);
            if (cacheFile.exists() && cacheFile.length() > 0) {
                throw new HiveException("File already exist");
            }
        } else {
            throw new HiveException("Store filePath is null");
        }
    }

    private Response getFileOrBuffer(String destFilePath) throws HiveException {
        Response response;
        try {
            response = ConnectionManager.getOnedriveApi()
                    .read("identity", destFilePath)
                    .execute();

        } catch (Exception ex) {
            throw new HiveException(ex.getMessage());
        }
        return response;
    }

    private CompletableFuture<ArrayList<String>> doListFile(Callback<ArrayList<String>> callback) {
        CompletableFuture future = new CompletableFuture();
        try {
            OneDriveApi api = ConnectionManager.getOnedriveApi();
            Response<DirChildrenResponse> response;
            response = api.getChildren("/Files").execute();
            int checkResponseCode = checkResponseCode(response);
            if (checkResponseCode == 404) {
                HiveException exception = new HiveException(HiveException.ITEM_NOT_FOUND);
                if (callback != null) callback.onError(exception);
                future.completeExceptionally(exception);
                return future;
            } else if (checkResponseCode != 0) {
                HiveException exception = new HiveException(HiveException.LIST_FILE_ERROR);
                if (callback != null) callback.onError(exception);
                future.completeExceptionally(exception);
                return future;
            }
            ArrayList<String> files = decodeListFile(response);
            if (callback != null) callback.onSuccess(files);
            future.complete(files);
        } catch (Exception ex) {
            HiveException e = new HiveException(ex.getMessage());
            future.completeExceptionally(e);
            if (callback != null) callback.onError(e);
            e.printStackTrace();
        }
        return future;
    }

    private CompletableFuture<Void> doPutValue(String key, byte[] value, org.elastos.hive.Callback callback) {

        System.out.println("33333333");
        byte[] originData = new byte[0];
        try {
            originData = doGetBuffer(key, null).get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }

        byte[] data = mergeLengthAndData(value);

        byte[] finalData = mergeData(originData, data);

        try {
            checkFileExistInBackEnd(key);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return writeToBackend(key, null, finalData, callback);
    }

    private ArrayList<String> decodeListFile(Response response) {
        ArrayList<String> files = new ArrayList<>();
        DirChildrenResponse dirChildrenResponse = (DirChildrenResponse) response.body();
        List<DirChildrenResponse.ValueBean> list = dirChildrenResponse.getValue();
        for (DirChildrenResponse.ValueBean fileinfo : list) {
            files.add(fileinfo.getName());
        }
        return files;
    }

    private int decodeFileLength(Response response) {
        FileOrDirPropResponse info = (FileOrDirPropResponse) response.body();
        return info.getSize();
    }


    private CompletableFuture<byte[]> doMergeLengthAndData(byte[] data) {
        CompletableFuture<byte[]> future = new CompletableFuture<>();
        byte[] result = mergeLengthAndData(data);
        future.complete(result);
        return future;
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

    private byte[] spliteBytes(byte[] data, int startPos, int length) {
        byte[] tmp = new byte[length];
        System.arraycopy(data, startPos, tmp, 0, length);

        return tmp;
    }

    private int calcuLength(byte[] data, int startPos) {
        int length;
        byte[] lengthByte = new byte[4];

        System.arraycopy(data, startPos, lengthByte, 0, 4);

        return DigitalUtil.byteArrayToInt(lengthByte);
    }

    private void createValueResult(ArrayList<byte[]> arrayList, byte[] data) {
        int total = data.length;
        int dataLength = calcuLength(data, 0);
        byte[] strbytes = spliteBytes(data, 4, dataLength);
        arrayList.add(strbytes);
        int remainingDataLength = total - (dataLength + 4);
        if (remainingDataLength <= 0) {
            return;
        } else {
            byte[] remainingData = new byte[remainingDataLength];
            System.arraycopy(data, dataLength + 4, remainingData, 0, remainingDataLength);
            createValueResult(arrayList, remainingData);
        }
    }

    private void checkFileExistInBackEnd(String destFilePath) throws Exception {
        Response response = requestFileInfo(destFilePath);
    }

    private Response requestFileInfo(String filename) throws Exception {
        Response response = ConnectionManager.getOnedriveApi()
                .getDirAndFileInfo(filename)
                .execute();
        return response;
    }

    private int checkResponseCode(Response response) {
        if (response == null) return -1;
        int code = response.code();
        switch (code) {
            case 200:
            case 201:
            case 202:
            case 203:
            case 204:
            case 205:
            case 206:
                return 0;
            default:
                return code;
        }
    }

    private CompletableFuture unSupportFunction() {
        CompletableFuture completableFuture = new CompletableFuture();
        completableFuture.completeExceptionally(new HiveException(HiveException.UNSUPPORT_FUNCTION));
        return completableFuture;
    }
}
