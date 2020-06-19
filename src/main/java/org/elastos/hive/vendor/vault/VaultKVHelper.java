package org.elastos.hive.vendor.vault;

import org.elastos.hive.Callback;
import org.elastos.hive.NullCallback;
import org.elastos.hive.exception.HiveException;
import org.elastos.hive.vendor.connection.ConnectionManager;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.concurrent.atomic.AtomicBoolean;

import retrofit2.Response;

public class VaultKVHelper {

    private String collection;
    private String scheme;

    private AtomicBoolean isExist = new AtomicBoolean(false);

    // 1.create collection
    // 2.judge exist state
    // 3.cache state

    VaultKVHelper(String collection, String scheme) {
        this.collection = collection;
        this.scheme = scheme;
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


}
