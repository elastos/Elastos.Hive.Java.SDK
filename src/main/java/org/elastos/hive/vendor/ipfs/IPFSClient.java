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
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PipedReader;
import java.io.Reader;
import java.io.Writer;
import java.nio.CharBuffer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.CompletableFuture;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okio.Buffer;
import okio.Okio;
import okio.Source;
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
    public CompletableFuture<StringBuffer> getFileToStringBuffer(String cid) {
        return getFileToStringBuffer(cid, new NullCallback<>());
    }

    @Override
    public CompletableFuture<StringBuffer> getFileToStringBuffer(String cid, Callback<StringBuffer> callback) {
        // TODO:
        return doGetFileToStrBuff(cid, callback);
    }

    @Override
    public CompletableFuture<byte[]> getFileToBuffer(String cid) {
        return getFileToBuffer(cid, new NullCallback<>());
    }

    @Override
    public CompletableFuture<byte[]> getFileToBuffer(String cid, Callback<byte[]> callback) {
        // TODO:
        return doGetFileBuff(cid, callback);
    }

    @Override
    public CompletableFuture<OutputStream> getFileToOutputStream(String cid) {
        return getFileToOutputStream(cid, new NullCallback<>());
    }

    @Override
    public CompletableFuture<OutputStream> getFileToOutputStream(String cid, Callback<OutputStream> callback) {
        // TODO:
        return doGetFileOutputSteam(cid, callback);
    }

    @Override
    public CompletableFuture<Writer> getFileToWriter(String cid) {
        return getFileToWriter(cid, new NullCallback<>());
    }

    @Override
    public CompletableFuture<Writer> getFileToWriter(String cid, Callback<Writer> callback) {
        // TODO:
        return doGetFileWriter(cid, callback);
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

    private MultipartBody.Part createBufferRequestBody(InputStream inputStream) {
        RequestBody requestBody = new InputStreamRequestBody(null, inputStream);
        MultipartBody.Part body = MultipartBody.Part.createFormData("file", "data", requestBody);
        return body;
    }

    private MultipartBody.Part createBufferRequestBody(Reader reader) throws IOException {
        return createBufferRequestBody(transReader(reader).toString().getBytes());
    }

    static StringBuffer transReader(Reader reader) throws IOException {
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

    private CompletableFuture<StringBuffer> doGetFileToStrBuff(String cid, Callback<StringBuffer> callback) {
        CompletableFuture<StringBuffer> future = new CompletableFuture<>();
        clientThreadPool.execute(() -> {
            try {
                Response response = getFileOrBuffer(cid);
                if (response != null) {
                    StringBuffer buffer = ResponseHelper.getStringBuffer(response);
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

    private CompletableFuture<OutputStream> doGetFileOutputSteam(String cid, Callback<OutputStream> callback) {
        CompletableFuture<OutputStream> future = new CompletableFuture<>();
        clientThreadPool.execute(() -> {
            try {
                Response response = getFileOrBuffer(cid);
                if (response != null) {
                    OutputStream outputStream = ResponseHelper.getOutputStream(response);
                    if (callback != null) callback.onSuccess(outputStream);
                    future.complete(outputStream);
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

    private CompletableFuture<Writer> doGetFileWriter(String cid, Callback<Writer> callback) {
        CompletableFuture<Writer> future = new CompletableFuture<>();
        clientThreadPool.execute(() -> {
            try {
                Response response = getFileOrBuffer(cid);
                if (response != null) {
                    Writer outputStream = ResponseHelper.getWriter(response);
                    if (callback != null) callback.onSuccess(outputStream);
                    future.complete(outputStream);
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
