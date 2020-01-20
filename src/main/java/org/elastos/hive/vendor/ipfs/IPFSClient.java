package org.elastos.hive.vendor.ipfs;

import org.elastos.hive.Callback;
import org.elastos.hive.Client;
import org.elastos.hive.NullCallback;
import org.elastos.hive.exception.HiveException;
import org.elastos.hive.interfaces.Files;
import org.elastos.hive.interfaces.IPFS;
import org.elastos.hive.interfaces.KeyValues;
import org.elastos.hive.utils.ResponseHelper;
import org.elastos.hive.vendor.connection.ConnectionManager;
import org.elastos.hive.vendor.connection.InputStreamRequestBody;
import org.elastos.hive.vendor.ipfs.network.model.AddFileResponse;
import org.elastos.hive.vendor.ipfs.network.model.ListFileResponse;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Reader;
import java.io.Writer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.CompletableFuture;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okio.Buffer;
import retrofit2.Response;


final class IPFSClient extends Client implements IPFS {
    private IPFSRpc ipfsRpc;

    private void checkNull(ArrayList<IPFSOptions.RpcNode> nodes) throws HiveException {
        if (nodes == null)
            throw new HiveException(HiveException.RPC_NODE_NULL);
    }

    IPFSClient(Options options) {

        ArrayList<IPFSOptions.RpcNode> nodes = ((IPFSOptions) options).getRpcNodes();
        try {
            checkNull(nodes);
        } catch (HiveException e) {
            e.printStackTrace();
        }

        ipfsRpc = new IPFSRpc(nodes);
    }

    @Override
    public void connect() throws HiveException {
        ipfsRpc.checkReachable();
    }

    @Override
    public void disconnect() {
        ipfsRpc.dissConnect();
    }

    @Override
    public boolean isConnected() {
        return ipfsRpc.getConnectState();
    }

    @Override
    public Files getFiles() {
        return null;
    }

    @Override
    public IPFS getIPFS() {
        return (IPFS) this;
    }

    @Override
    public KeyValues getKeyValues() {
        return null;
    }

    @Override
    public CompletableFuture<String> put(byte[] data) {
        return put(data, new NullCallback<>());
    }

    @Override
    public CompletableFuture<String> put(byte[] data, Callback<String> callback) {
        return doPutBuffer(data, callback);
    }

    @Override
    public CompletableFuture<String> put(String data) {
        return put(data, new NullCallback<>());
    }

    @Override
    public CompletableFuture<String> put(String data, Callback<String> callback) {
        return doPutBuffer(data.getBytes(), callback);
    }

    @Override
    public CompletableFuture<String> put(InputStream input) {
        return put(input, new NullCallback<>());
    }

    @Override
    public CompletableFuture<String> put(InputStream input, Callback<String> callback) {
        return doPutInputStream(input, callback);
    }

    @Override
    public CompletableFuture<String> put(Reader reader) {
        return put(reader, new NullCallback<>());
    }

    @Override
    public CompletableFuture<String> put(Reader reader, Callback<String> callback) {
        return doPutReader(reader, callback);
    }

    @Override
    public CompletableFuture<Long> size(String cid) {
        return size(cid, new NullCallback<>());
    }

    @Override
    public CompletableFuture<Long> size(String cid, Callback<Long> callback) {
        return doGetFileLength(cid, callback);
    }

    @Override
    public CompletableFuture<String> getAsString(String cid) {
        return getAsString(cid, new NullCallback<>());
    }

    @Override
    public CompletableFuture<String> getAsString(String cid, Callback<String> callback) {
        // TODO:
        return doGetFileStr(cid, callback);
    }

    @Override
    public CompletableFuture<byte[]> getAsBuffer(String cid) {
        return getAsBuffer(cid, new NullCallback<>());
    }

    @Override
    public CompletableFuture<byte[]> getAsBuffer(String cid, Callback<byte[]> callback) {
        // TODO:
        return doGetFileBuff(cid, callback);
    }

    @Override
    public CompletableFuture<Long> get(String cid, OutputStream output) {
        return get(cid, output, new NullCallback<>());
    }

    @Override
    public CompletableFuture<Long> get(String cid, OutputStream output, Callback<Long> callback) {
        return doWriteToOutput(cid, output, callback);
    }

    @Override
    public CompletableFuture<Long> get(String cid, Writer writer) {
        return get(cid, writer, new NullCallback<>());
    }

    @Override
    public CompletableFuture<Long> get(String cid, Writer writer, Callback<Long> callback) {
        return doWriteToWriter(cid, writer, callback);
    }

    ////
    private CompletableFuture<String> doPutBuffer(byte[] data, Callback<String> callback) {
        CompletableFuture<String> future = new CompletableFuture<>();
        clientThreadPool.execute(() -> {
            try {
                MultipartBody.Part requestBody = createBufferRequestBody(data);
                Response response = ConnectionManager.getIPFSApi().addFile(requestBody).execute();
                if (response == null || response.code() != 200) {
                    HiveException exception = new HiveException(HiveException.PUT_BUFFER_ERROR);
                    if (callback != null) callback.onError(exception);
                    future.completeExceptionally(exception);
                    return;
                }

                AddFileResponse addFileResponse = (AddFileResponse) response.body();
                String cid = addFileResponse.getHash();
                if (callback != null) callback.onSuccess(cid);
                future.complete(cid);
            } catch (Exception e) {
                HiveException exception = new HiveException(HiveException.PUT_BUFFER_ERROR);
                if (callback != null) callback.onError(exception);
                future.completeExceptionally(exception);
                e.printStackTrace();
            }
        });
        return future;
    }

    private MultipartBody.Part createBufferRequestBody(byte[] data) {
        if (data == null || data.length == 0) {
            return null;
        }
        RequestBody requestFile = RequestBody.create(null, data);
        MultipartBody.Part body = MultipartBody.Part.createFormData("file", "data", requestFile);
        return body;
    }

    private MultipartBody.Part createBufferRequestBody(InputStream inputStream) throws IOException {
        Buffer buffer = new Buffer();
        byte[] cache = new byte[1024];
        int length = 0;
        while ((length = inputStream.read(cache)) != -1) {
            buffer.write(cache, 0, length);
        }
        return createBufferRequestBody(buffer.readByteArray());
    }

    private MultipartBody.Part createBufferRequestBody(Reader reader) throws IOException {
        return createBufferRequestBody(transReader(reader).toString().getBytes());
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


    private CompletableFuture<String> doPutInputStream(InputStream inputStream, Callback callback) {
        CompletableFuture future = new CompletableFuture();
        clientThreadPool.execute(() -> {
            try {
                MultipartBody.Part requestBody = createBufferRequestBody(inputStream);
                Response response = ConnectionManager.getIPFSApi().addFile(requestBody).execute();
                if (response == null || response.code() != 200) {
                    HiveException exception = new HiveException(HiveException.PUT_BUFFER_ERROR);
                    if (callback != null) callback.onError(exception);
                    future.completeExceptionally(exception);
                    return;
                }

                AddFileResponse addFileResponse = (AddFileResponse) response.body();
                String cid = addFileResponse.getHash();
                if (callback != null) callback.onSuccess(cid);
                future.complete(cid);
            } catch (Exception e) {
                HiveException exception = new HiveException(HiveException.PUT_BUFFER_ERROR);
                if (callback != null) callback.onError(exception);
                future.completeExceptionally(exception);
                e.printStackTrace();
            }
        });
        return future;
    }

    private CompletableFuture<String> doPutReader(Reader reader, Callback callback) {
        CompletableFuture future = new CompletableFuture();
        clientThreadPool.execute(() -> {
            try {
                MultipartBody.Part requestBody = createBufferRequestBody(reader);
                Response response = ConnectionManager.getIPFSApi().addFile(requestBody).execute();
                if (response == null || response.code() != 200) {
                    HiveException exception = new HiveException(HiveException.PUT_BUFFER_ERROR);
                    if (callback != null) callback.onError(exception);
                    future.completeExceptionally(exception);
                    return;
                }

                AddFileResponse addFileResponse = (AddFileResponse) response.body();
                String cid = addFileResponse.getHash();
                if (callback != null) callback.onSuccess(cid);
                future.complete(cid);
            } catch (Exception e) {
                HiveException exception = new HiveException(HiveException.PUT_BUFFER_ERROR);
                if (callback != null) callback.onError(exception);
                future.completeExceptionally(exception);
                e.printStackTrace();
            }
        });

        return future;
    }

    private CompletableFuture<Long> doGetFileLength(String cid, Callback<Long> callback) {
        CompletableFuture<Long> future = new CompletableFuture();
        clientThreadPool.execute(() -> {
            long size = 0;
            Response response = null;
            try {
                response = ConnectionManager.getIPFSApi().listFile(cid).execute();
            } catch (Exception e) {
                future.completeExceptionally(new HiveException(HiveException.GET_FILE_LENGTH_ERROR));
            }
            if (response == null || response.code() != 200) {
                HiveException exception = new HiveException(HiveException.GET_FILE_LENGTH_ERROR);
                if (callback != null) callback.onError(exception);
                future.completeExceptionally(exception);
                return;
            }

            ListFileResponse listFileResponse = (ListFileResponse) response.body();

            HashMap<String, ListFileResponse.ObjectsBean.Bean> map = listFileResponse.getObjects();

            if (null != map && map.size() > 0) {
                for (String key : map.keySet()) {
                    size = map.get(key).getSize();
                    break;//if result only one
                }
                if (callback != null) callback.onSuccess(size);
                future.complete(size);
            } else {
                HiveException exception = new HiveException(HiveException.GET_FILE_LENGTH_ERROR);
                callback.onError(exception);
                future.completeExceptionally(exception);
            }
        });

        return future;
    }

    private CompletableFuture<String> doGetFileStr(String cid, Callback<String> callback) {
        CompletableFuture<String> future = new CompletableFuture<>();
        clientThreadPool.execute(() -> {
            try {
                Response response = getFileOrBuffer(cid);
                if (response != null) {
                    String result = ResponseHelper.getString(response);
                    if (callback != null) callback.onSuccess(result);
                    future.complete(result);
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
        });
        return future;
    }

    private CompletableFuture<byte[]> doGetFileBuff(String cid, Callback<byte[]> callback) {
        CompletableFuture<byte[]> future = new CompletableFuture<>();
        clientThreadPool.execute(() -> {
            try {
                Response response = getFileOrBuffer(cid);
                if (response != null) {
                    byte[] buffer = ResponseHelper.getBuffer(response);
                    if (callback != null) callback.onSuccess(buffer);
                    future.complete(buffer);
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
        });
        return future;
    }

    private CompletableFuture<InputStream> doGetFileStream(String cid, Callback<InputStream> callback) {
        CompletableFuture<InputStream> future = new CompletableFuture<>();
        clientThreadPool.execute(() -> {
            try {
                Response response = getFileOrBuffer(cid);
                if (response != null) {
                    InputStream inputStream = ResponseHelper.getStream(response);
                    if (callback != null) callback.onSuccess(inputStream);
                    future.complete(inputStream);
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
        });

        return future;
    }

    private CompletableFuture<Long> doWriteToOutput(String cid, OutputStream outputStream, Callback<Long> callback) {
        CompletableFuture<Long> future = new CompletableFuture<>();
        clientThreadPool.execute(() -> {
            try {
                Response response = getFileOrBuffer(cid);
                System.out.println(Thread.currentThread().getName());
                if (response != null) {
                    long length = ResponseHelper.writeOutput(response, outputStream);
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
        });

        return future;
    }


    private CompletableFuture<Reader> doGetFileReader(String cid, Callback<Reader> callback) {
        CompletableFuture<Reader> future = new CompletableFuture<>();
        clientThreadPool.execute(() -> {
            try {
                Response response = getFileOrBuffer(cid);
                if (response != null) {
                    Reader reader = ResponseHelper.getReader(response);
                    if (callback != null) callback.onSuccess(reader);
                    future.complete(reader);
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
        });
        return future;
    }

    private CompletableFuture<Long> doWriteToWriter(String cid, Writer writer, Callback<Long> callback) {
        CompletableFuture<Long> future = new CompletableFuture<>();
        clientThreadPool.execute(() -> {
            try {
                Response response = getFileOrBuffer(cid);
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
        });
        return future;
    }

    private Response getFileOrBuffer(String cid) throws HiveException {
        Response response;
        try {
            response = ConnectionManager.getIPFSApi()
                    .catFile(cid)
                    .execute();
        } catch (Exception ex) {
            throw new HiveException(ex.getMessage());
        }
        return response;
    }

}
