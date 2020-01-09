package org.elastos.hive.interfaces;

import org.elastos.hive.Callback;
import org.elastos.hive.result.ValueList;
import org.elastos.hive.result.Void;

import java.util.concurrent.CompletableFuture;

public interface KeyValues {
    CompletableFuture<Void> putValue(String key, byte[] value);
    CompletableFuture<Void> putValue(String key, byte[] value, Callback<Void> callback);

    CompletableFuture<Void> setValue(String key, byte[] value);
    CompletableFuture<Void> setValue(String key, byte[] value, Callback<Void> callback);

    CompletableFuture<ValueList> getValues(String key);
    CompletableFuture<ValueList> getValues(String key, Callback<ValueList> callback);

    CompletableFuture<Void> delete(String key);
    CompletableFuture<Void> delete(String key, Callback<Void> callback);
}
