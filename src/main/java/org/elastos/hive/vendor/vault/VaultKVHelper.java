package org.elastos.hive.vendor.vault;

import org.elastos.hive.Callback;
import org.elastos.hive.Client;
import org.elastos.hive.NullCallback;
import org.elastos.hive.exception.HiveException;
import org.elastos.hive.utils.DigitalUtil;
import org.elastos.hive.utils.ResponseHelper;
import org.elastos.hive.vendor.connection.ConnectionManager;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.concurrent.atomic.AtomicBoolean;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import retrofit2.Response;

public class VaultKVHelper {

    private VaultAuthHelper authHelper;

    private String collection;
    private String scheme;

    private AtomicBoolean isExist = new AtomicBoolean(false);

    // 1.create collection
    // 2.judge exist state
    // 3.cache state

    VaultKVHelper(String collection, String scheme, Client.Options options) {
        this.collection = collection;
        this.scheme = scheme;

        VaultOptions opts = (VaultOptions) options;
//        authHelper = new VaultAuthHelper(opts.did(),
//                opts.storePass(),
//                opts.storePath(),
//                opts.nodeUrl());
    }

    private void createCollection(String collection, String scheme) throws Exception {
        Map map = new HashMap<>();
        map.put("collection", collection);
        map.put("schema", scheme);
        Response response = ConnectionManager.getHiveVaultApi()
                .createCollection(map)
                .execute();
//        BaseResponse baseResponse = (BaseResponse) response.body();
//        if(baseResponse!=null
//                && baseResponse.get_error()==null) {
//
//        }
    }


    public CompletableFuture<Void> checkValid() {
        return checkValid(new NullCallback<>());
    }

    public CompletableFuture<Void> checkValid(Callback<Void> callback) {
        return CompletableFuture.runAsync(() -> {
            try {
                doCheck();
                callback.onSuccess(null);
            } catch (Exception e) {
                HiveException exception = new HiveException(e.getLocalizedMessage());
                callback.onError(exception);
                throw new CompletionException(exception);
            }
        });
    }

    private void doCheck() throws Exception {
        if(!isExist.get() && collection!=null && scheme!=null) {
            createCollection(collection, scheme);
            isExist.set(true);
        }
    }

    public CompletableFuture<ArrayList<byte[]>> getValues(String key) {
        return getValues(key, null);
    }

    public CompletableFuture<ArrayList<byte[]>> getValues(String key, Callback<ArrayList<byte[]>> callback) {
        if (null == key || key.isEmpty())
            throw new IllegalArgumentException();

        return checkValid().thenCompose(result -> authHelper.checkValid()
                .thenCompose(result1 -> doGetValue(key, callback)));

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

    private Response getFileOrBuffer(String key) throws HiveException {
        Response<okhttp3.ResponseBody> response;
        try {
            Map map = new HashMap<>();
            map.put("key", key);
            String json = new JSONObject(map).toString();
            response = ConnectionManager.getHiveVaultApi()
                    .get_dbCol(collection, json)
                    .execute();

        } catch (Exception ex) {
            throw new HiveException(ex.getMessage());
        }
        return response;
    }

    public CompletableFuture<Void> putValue(String key, String value) {
        return putValue(key, value, null);
    }

    public CompletableFuture<Void> putValue(String key, String value, Callback<Void> callback) {
        if (null == key || key.isEmpty() || null == value || value.isEmpty())
            throw new IllegalArgumentException();

        return checkValid().thenCompose(result -> authHelper.checkValid()
                .thenCompose(result1 ->
                        doPutValue(key, value.getBytes(), callback)));
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
        Map map = new HashMap<>();
        map.put("key", key);
        map.put("value", new String(value));
        Response response = ConnectionManager.getHiveVaultApi()
                .post_dbCol(collection, RequestBody.create(MediaType.parse("Content-Type, application/json"), new JSONObject(map).toString()))
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

    private int checkResponseCode(Response response) {
        if (response == null)
            return -1;

        int code = response.code();
        if (code < 300 && code >= 200)
            return 0;

        return code;
    }

}
