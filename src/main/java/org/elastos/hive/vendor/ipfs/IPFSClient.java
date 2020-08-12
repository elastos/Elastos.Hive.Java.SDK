package org.elastos.hive.vendor.ipfs;

import org.elastos.hive.Callback;
import org.elastos.hive.Client;
import org.elastos.hive.NullCallback;
import org.elastos.hive.exception.HiveException;
import org.elastos.hive.interfaces.Database;
import org.elastos.hive.interfaces.Files;
import org.elastos.hive.interfaces.IPFS;
import org.elastos.hive.interfaces.KeyValues;
import org.elastos.hive.interfaces.VaultFiles;
import org.elastos.hive.utils.ResponseHelper;
import org.elastos.hive.vendor.connection.ConnectionManager;
import org.elastos.hive.vendor.ipfs.network.model.AddFileResponse;
import org.elastos.hive.vendor.ipfs.network.model.ListFileResponse;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Reader;
import java.io.Writer;
import java.util.HashMap;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Response;


final class IPFSClient extends Client implements IPFS {
    private IPFSRpc ipfsRpc;

    IPFSClient(Options options) {
        ipfsRpc = new IPFSRpc(((IPFSOptions) options).getRpcNodes());
    }

    @Override
    public void connect() throws HiveException {
        try {
            ipfsRpc.connectAsync(null).get();
        } catch (Exception e) {
            throw new HiveException(e.getLocalizedMessage());
        }
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
    public VaultFiles getVaultFiles() {
        return null;
    }

    @Override
    public IPFS getIPFS() {
        return this;
    }

    @Override
    public Database getDatabase() {
        return null;
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
        return ipfsRpc.checkValid()
                .thenCompose(isValid -> doPutBuffer(data, getCallback(callback)));
    }

    @Override
    public CompletableFuture<String> put(String data) {
        return put(data, null);
    }

    @Override
    public CompletableFuture<String> put(String data, Callback<String> callback) {
        if (null == data)
            throw new IllegalArgumentException();
        return ipfsRpc.checkValid()
                .thenCompose(isValid -> doPutBuffer(data.getBytes(), getCallback(callback)));
    }

    @Override
    public CompletableFuture<String> put(InputStream input) {
        return put(input, null);
    }

    @Override
    public CompletableFuture<String> put(InputStream input, Callback<String> callback) {
        if (null == input)
            throw new IllegalArgumentException();
        return ipfsRpc.checkValid()
                .thenCompose(isValid -> doPutData(input, getCallback(callback)));
    }

    @Override
    public CompletableFuture<String> put(Reader reader) {
        return put(reader, null);
    }

    @Override
    public CompletableFuture<String> put(Reader reader, Callback<String> callback) {
        if (null == reader)
            throw new IllegalArgumentException();
        return ipfsRpc.checkValid()
                .thenCompose(isValid -> doPutData(reader, getCallback(callback)));
    }

    @Override
    public CompletableFuture<Long> size(String cid) {
        return size(cid, null);
    }

    @Override
    public CompletableFuture<Long> size(String cid, Callback<Long> callback) {
        if (null == cid || cid.isEmpty())
            throw new IllegalArgumentException();
        return ipfsRpc.checkValid()
                .thenCompose(isValid -> doGetLength(cid, getCallback(callback)));
    }

    @Override
    public CompletableFuture<String> getAsString(String cid) {
        return getAsString(cid, null);
    }

    @Override
    public CompletableFuture<String> getAsString(String cid, Callback<String> callback) {
        if (null == cid || cid.isEmpty())
            throw new IllegalArgumentException();
        return ipfsRpc.checkValid()
                .thenCompose(isValid -> doGetDataAsString(cid, getCallback(callback)));
    }

    @Override
    public CompletableFuture<byte[]> getAsBuffer(String cid) {
        return getAsBuffer(cid, null);
    }

    @Override
    public CompletableFuture<byte[]> getAsBuffer(String cid, Callback<byte[]> callback) {
        if (null == cid || cid.isEmpty())
            throw new IllegalArgumentException();
        return ipfsRpc.checkValid()
                .thenCompose(isValid -> doGetDataAsBuffer(cid, getCallback(callback)));
    }

    @Override
    public CompletableFuture<Long> get(String cid, OutputStream output) {
        return get(cid, output, null);
    }

    @Override
    public CompletableFuture<Long> get(String cid, OutputStream output, Callback<Long> callback) {
        if (null == cid || cid.isEmpty() || null == output)
            throw new IllegalArgumentException();
        return ipfsRpc.checkValid()
                .thenCompose(isValid -> doGetData(cid, output, getCallback(callback)));
    }

    @Override
    public CompletableFuture<Long> get(String cid, Writer writer) {
        return get(cid, writer, null);
    }

    @Override
    public CompletableFuture<Long> get(String cid, Writer writer, Callback<Long> callback) {
        if (null == cid || cid.isEmpty() || null == writer)
            throw new IllegalArgumentException();
        return ipfsRpc.checkValid()
                .thenCompose(isValid -> doGetData(cid, writer, getCallback(callback)));
    }

    private CompletableFuture<String> doPutBuffer(byte[] data, Callback<String> callback) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                String result = putBufferImpl(data);
                callback.onSuccess(result);
                return result;
            } catch (Exception e) {
                HiveException exception = new HiveException(e.getLocalizedMessage());
                callback.onError(exception);
                throw new CompletionException(exception);
            }
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
        RequestBody requestFile = RequestBody.create(null, data);
        return MultipartBody.Part.createFormData("file", "data", requestFile);
    }

    private byte[] readData(InputStream input) throws IOException {
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        byte[] data = new byte[1024];
        int length;

        while ((length = input.read(data, 0, data.length)) != -1) {
            buffer.write(data, 0, length);
        }
        buffer.flush();
        return buffer.toByteArray();
    }

    private byte[] readData(Reader reader) throws IOException {
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
        return stringBuffer.toString().getBytes();
    }

    private MultipartBody.Part createBufferRequestBody(InputStream input) throws IOException {

        return createBufferRequestBody(readData(input));
    }

    private MultipartBody.Part createBufferRequestBody(Reader reader) throws IOException {
        return createBufferRequestBody(readData(reader));
    }

    private CompletableFuture<String> doPutData(InputStream input, Callback<String> callback) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                String cid = putDataImpl(input);
                callback.onSuccess(cid);
                return cid;
            } catch (Exception e) {
                HiveException exception = new HiveException(e.getLocalizedMessage());
                callback.onError(exception);
                throw new CompletionException(exception);            }
        });
    }

    private String putDataImpl(InputStream input) throws Exception {
        Response<AddFileResponse> response = ConnectionManager
                .getIPFSApi()
                .addFile(createBufferRequestBody(input))
                .execute();
        if (response == null || response.code() != 200)
            throw new HiveException(HiveException.ERROR);

        AddFileResponse respBody = response.body();
        return respBody != null ? respBody.getHash() : null;
    }

    private CompletableFuture<String> doPutData(Reader reader, Callback<String> callback) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                String cid = putDataImpl(reader);
                callback.onSuccess(cid);
                return cid;
            } catch (Exception e) {
                HiveException exception = new HiveException(e.getLocalizedMessage());
                callback.onError(exception);
                throw new CompletionException(exception);
            }
        });
    }

    private String putDataImpl(Reader reader) throws Exception {
        Response<AddFileResponse> response = ConnectionManager
                .getIPFSApi()
                .addFile(createBufferRequestBody(reader))
                .execute();
        if (response == null || response.code() != 200)
            throw new HiveException(HiveException.ERROR);

        AddFileResponse respBody = response.body();
        return respBody != null ? respBody.getHash() : null;
    }

    private CompletableFuture<Long> doGetLength(String cid, Callback<Long> callback) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                long length = getLengthImpl(cid);
                callback.onSuccess(length);
                return length;
            } catch (Exception e) {
                HiveException exception = new HiveException(e.getLocalizedMessage());
                callback.onError(exception);
                throw new CompletionException(exception);
            }
        });
    }

    private long getLengthImpl(String cid) throws Exception {
        Response<ListFileResponse> response = ConnectionManager
                .getIPFSApi()
                .listFile(cid)
                .execute();
        if (response == null || response.code() != 200)
            throw new HiveException(HiveException.ERROR);

        ListFileResponse respBody = response.body();
        HashMap<String, ListFileResponse.ObjectsBean.Bean> map = respBody != null ? respBody.getObjects() : null;

        if (map == null || map.size() <= 0)
            throw new HiveException(HiveException.ERROR);

        String[] keys = map.keySet().toArray(new String[0]);
        return map.get(keys[0]).getSize();
    }

    private CompletableFuture<String> doGetDataAsString(String cid, Callback<String> callback) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                String result = getDataAsStringImpl(cid);
                callback.onSuccess(result);
                return result;
            } catch (Exception e) {
                HiveException exception = new HiveException(e.getLocalizedMessage());
                callback.onError(exception);
                throw new CompletionException(exception);
            }
        });
    }

    private String getDataAsStringImpl(String cid) throws Exception {
        return ResponseHelper.getString(getResponse(cid));
    }

    private CompletableFuture<byte[]> doGetDataAsBuffer(String cid, Callback<byte[]> callback) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                byte[] result = getDataAsBufferImpl(cid);
                callback.onSuccess(result);
                return result;
            } catch (HiveException e) {
                HiveException exception = new HiveException(e.getLocalizedMessage());
                callback.onError(exception);
                throw new CompletionException(exception);
            }
        });
    }

    private byte[] getDataAsBufferImpl(String cid) throws HiveException {
        return ResponseHelper.getBuffer(getResponse(cid));
    }

    private CompletableFuture<Long> doGetData(String cid, OutputStream output, Callback<Long> callback) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                long length = getDataToOutputImpl(cid, output);
                callback.onSuccess(length);
                return length;
            } catch (Exception e) {
                HiveException exception = new HiveException(e.getLocalizedMessage());
                callback.onError(exception);
                throw new CompletionException(exception);
            }
        });
    }

    private long getDataToOutputImpl(String cid, OutputStream output) throws Exception {
        return ResponseHelper.writeOutput(getResponse(cid), output);
    }

    private CompletableFuture<Long> doGetData(String cid, Writer writer, Callback<Long> callback) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                long length = getDataToWriterImpl(cid, writer);
                callback.onSuccess(length);
                return length;
            } catch (Exception e) {
                HiveException exception = new HiveException(e.getLocalizedMessage());
                callback.onError(exception);
                throw new CompletionException(exception);
            }
        });
    }

    private long getDataToWriterImpl(String cid, Writer writer) throws Exception {
        return ResponseHelper.writeDataToWriter(getResponse(cid), writer);
    }

    private Response<okhttp3.ResponseBody> getResponse(String cid) throws HiveException {
        Response<okhttp3.ResponseBody> response;
        try {
            response = ConnectionManager.getIPFSApi()
                    .catFile(cid)
                    .execute();
        } catch (Exception ex) {
            throw new HiveException(ex.getMessage());
        }

        if (null == response || response.code() != 200)
            throw new HiveException(HiveException.ERROR);

        return response;
    }

}
