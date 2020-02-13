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
import org.elastos.hive.vendor.ipfs.network.model.AddFileResponse;
import org.elastos.hive.vendor.ipfs.network.model.ListFileResponse;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Reader;
import java.io.Writer;
import java.util.HashMap;
import java.util.concurrent.CompletableFuture;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okio.Buffer;
import retrofit2.Response;


final class IPFSClient extends Client implements IPFS {
    private IPFSRpc ipfsRpc;

    IPFSClient(Options options) {
        ipfsRpc = new IPFSRpc(((IPFSOptions) options).getRpcNodes());
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
        throw new UnsupportedOperationException();
    }

    @Override
    public IPFS getIPFS() {
        return this;
    }

    private <T> Callback<T> getCallback(Callback<T> callback) {
        return (null == callback ? new NullCallback<T>() : callback);
    }

    @Override
    public KeyValues getKeyValues() {
        throw new UnsupportedOperationException();
    }

    @Override
    public CompletableFuture<String> put(byte[] data) {
        return put(data, null);
    }

    @Override
    public CompletableFuture<String> put(byte[] data, Callback<String> callback) {
        if (null == data)
            throw new IllegalArgumentException();

        return doPutBuffer(data, getCallback(callback));
    }

    @Override
    public CompletableFuture<String> put(String data) {
        return put(data, null);
    }

    @Override
    public CompletableFuture<String> put(String data, Callback<String> callback) {
        if (null == data)
            throw new IllegalArgumentException();

        return doPutBuffer(data.getBytes(), getCallback(callback));
    }

    @Override
    public CompletableFuture<String> put(InputStream input) {
        return put(input, null);
    }

    @Override
    public CompletableFuture<String> put(InputStream input, Callback<String> callback) {
        if (null == input)
            throw new IllegalArgumentException();

        return doPutInputStream(input, getCallback(callback));
    }

    @Override
    public CompletableFuture<String> put(Reader reader) {
        return put(reader, null);
    }

    @Override
    public CompletableFuture<String> put(Reader reader, Callback<String> callback) {
        if (null == reader)
            throw new IllegalArgumentException();

        return doPutReader(reader, getCallback(callback));
    }

    @Override
    public CompletableFuture<Long> size(String cid) {
        return size(cid, null);
    }

    @Override
    public CompletableFuture<Long> size(String cid, Callback<Long> callback) {
        if (null == cid || cid.isEmpty())
            throw new IllegalArgumentException();

        return doGetFileLength(cid, getCallback(callback));
    }

    @Override
    public CompletableFuture<String> getAsString(String cid) {
        return getAsString(cid, null);
    }

    @Override
    public CompletableFuture<String> getAsString(String cid, Callback<String> callback) {
        if (null == cid || cid.isEmpty())
            throw new IllegalArgumentException();

        return doGetAsStr(cid, getCallback(callback));
    }

    @Override
    public CompletableFuture<byte[]> getAsBuffer(String cid) {
        return getAsBuffer(cid, null);
    }

    @Override
    public CompletableFuture<byte[]> getAsBuffer(String cid, Callback<byte[]> callback) {
        if (null == cid || cid.isEmpty())
            throw new IllegalArgumentException();

        return doGetAsBuff(cid, getCallback(callback));
    }

    @Override
    public CompletableFuture<Long> get(String cid, OutputStream output) {
        return get(cid, output, null);
    }

    @Override
    public CompletableFuture<Long> get(String cid, OutputStream output, Callback<Long> callback) {
        if (null == cid || cid.isEmpty() || null == output)
            throw new IllegalArgumentException();

        return doWriteToOutput(cid, output, getCallback(callback));
    }

    @Override
    public CompletableFuture<Long> get(String cid, Writer writer) {
        return get(cid, writer, null);
    }

    @Override
    public CompletableFuture<Long> get(String cid, Writer writer, Callback<Long> callback) {
        if (null == cid || cid.isEmpty()|| null == writer)
            throw new IllegalArgumentException();

        return doWriteToWriter(cid, writer, getCallback(callback));
    }

    ////
    private CompletableFuture<String> doPutBuffer(byte[] data, Callback<String> callback) {
        return CompletableFuture.supplyAsync(() -> {
            String result = null;
            try {
                result = putBufferImpl(data);
                callback.onSuccess(result);
            } catch (Exception e) {
                callback.onError(new HiveException(e.getLocalizedMessage()));
            }
            return result;
        });
    }

    private String putBufferImpl(byte[] data) throws Exception {
        MultipartBody.Part requestBody = createBufferRequestBody(data);
        Response<AddFileResponse> response = ConnectionManager.getIPFSApi().addFile(requestBody).execute();
        if (response == null || response.code() != 200)
            throw new HiveException(HiveException.ERROR);

        AddFileResponse respBody = response.body();
        return respBody != null ? respBody.getHash() : null;
    }

    private MultipartBody.Part createBufferRequestBody(byte[] data) {
        if (data == null || data.length == 0) {
            return null;
        }
        RequestBody requestFile = RequestBody.create(null, data);
        return MultipartBody.Part.createFormData("file", "data", requestFile);
    }

    private MultipartBody.Part createBufferRequestBody(InputStream inputStream) throws IOException {
        Buffer buffer = new Buffer();
        byte[] cache = new byte[1024];
        int length;
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


    private CompletableFuture<String> doPutInputStream(InputStream inputStream, Callback<String> callback) {
        return CompletableFuture.supplyAsync(() -> {
            String cid = null;
            try {
                cid = putInputStreamImpl(inputStream);
                callback.onSuccess(cid);
            } catch (Exception e) {
                callback.onError(new HiveException(e.getLocalizedMessage()));
            }
            return cid;
        });
    }

    private String putInputStreamImpl(InputStream inputStream) throws Exception {
        MultipartBody.Part requestBody = createBufferRequestBody(inputStream);
        Response<AddFileResponse> response = ConnectionManager.getIPFSApi().addFile(requestBody).execute();
        if (response == null || response.code() != 200)
            throw new HiveException(HiveException.ERROR);

        AddFileResponse addFileResponse = response.body();
        return addFileResponse != null ? addFileResponse.getHash() : null;
    }

    private CompletableFuture<String> doPutReader(Reader reader, Callback<String> callback) {
        return CompletableFuture.supplyAsync(() -> {
            String cid = "";
            try {
                cid = putReaderImpl(reader);
                callback.onSuccess(cid);
            } catch (Exception e) {
                callback.onError(new HiveException(e.getLocalizedMessage()));
            }
            return cid;
        });
    }

    private String putReaderImpl(Reader reader) throws Exception {
        MultipartBody.Part requestBody = createBufferRequestBody(reader);
        Response<AddFileResponse> response = ConnectionManager.getIPFSApi().addFile(requestBody).execute();
        if (response == null || response.code() != 200)
            throw new HiveException(HiveException.ERROR);

        AddFileResponse addFileResponse = response.body();
        return addFileResponse != null ? addFileResponse.getHash() : null;
    }

    private CompletableFuture<Long> doGetFileLength(String cid, Callback<Long> callback) {
        return CompletableFuture.supplyAsync(() -> {
            long length = 0;
            try {
                length = getFileLengthImpl(cid);
                callback.onSuccess(length);
            } catch (Exception e) {
                callback.onError(new HiveException(e.getLocalizedMessage()));
            }
            return length;
        });
    }

    private long getFileLengthImpl(String cid) throws Exception {
        Response<ListFileResponse> response = ConnectionManager.getIPFSApi().listFile(cid).execute();

        if (response == null || response.code() != 200)
            throw new HiveException(HiveException.ERROR);

        ListFileResponse listFileResponse = response.body();

        HashMap<String, ListFileResponse.ObjectsBean.Bean> map = listFileResponse != null ? listFileResponse.getObjects() : null;

        if (map == null || map.size() <= 0)
            throw new HiveException(HiveException.ERROR);

        String[] keys = map.keySet().toArray(new String[0]);
        return map.get(keys[0]).getSize();
    }

    private CompletableFuture<String> doGetAsStr(String cid, Callback<String> callback) {
        return CompletableFuture.supplyAsync(() -> {
            String result = null;
            try {
                result = getAsStrImpl(cid);
                callback.onSuccess(result);
            } catch (Exception e) {
                callback.onError(new HiveException(e.getLocalizedMessage()));
            }
            return result;
        });
    }

    private String getAsStrImpl(String cid) throws Exception {
        Response<okhttp3.ResponseBody> response = getFileOrBuffer(cid);

        if (response == null || response.code() != 200)
            throw new HiveException(HiveException.ERROR);

        return ResponseHelper.getString(response);
    }

    private CompletableFuture<byte[]> doGetAsBuff(String cid, Callback<byte[]> callback) {
        return CompletableFuture.supplyAsync(() -> {
            byte[] result = new byte[0];
            try {
                result = getAsBufferImpl(cid);
                callback.onSuccess(result);
            } catch (HiveException e) {
                callback.onError(new HiveException(e.getLocalizedMessage()));
            }
            return result;
        });
    }

    private byte[] getAsBufferImpl(String cid) throws HiveException {
        Response<okhttp3.ResponseBody> response = getFileOrBuffer(cid);

        if (response == null || response.code() != 200)
            throw new HiveException(HiveException.ERROR);

        return ResponseHelper.getBuffer(response);
    }

    private CompletableFuture<Long> doWriteToOutput(String cid, OutputStream outputStream, Callback<Long> callback) {
        return CompletableFuture.supplyAsync(() -> {
            long length = 0;
            try {
                length = writeToOutputImpl(cid, outputStream);
                callback.onSuccess(length);
            } catch (Exception e) {
                callback.onError(new HiveException(e.getLocalizedMessage()));
            }
            return length;
        });
    }

    private long writeToOutputImpl(String cid, OutputStream outputStream) throws Exception {
        Response<okhttp3.ResponseBody> response = getFileOrBuffer(cid);
        if (response == null || response.code() != 200)
            throw new HiveException(HiveException.ERROR);

        return ResponseHelper.writeOutput(response, outputStream);
    }

    private CompletableFuture<Long> doWriteToWriter(String cid, Writer
            writer, Callback<Long> callback) {
        return CompletableFuture.supplyAsync(() -> {
            long length = 0;
            try {
                length = writeToWriterImpl(cid, writer);
                callback.onSuccess(length);
            } catch (Exception e) {
                callback.onError(new HiveException(e.getLocalizedMessage()));
            }
            return length;
        });
    }

    private long writeToWriterImpl(String cid, Writer writer) throws Exception {
        Response<okhttp3.ResponseBody> response = getFileOrBuffer(cid);

        if (response == null || response.code() != 200)
            throw new HiveException(HiveException.ERROR);

        return ResponseHelper.writeDataToWriter(response, writer);
    }

    private Response<okhttp3.ResponseBody> getFileOrBuffer(String cid) throws HiveException {
        Response<okhttp3.ResponseBody> response;
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
