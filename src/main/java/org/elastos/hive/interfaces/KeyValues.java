package org.elastos.hive.interfaces;

import org.elastos.hive.Callback;
import org.elastos.hive.exception.HiveException;

import java.util.ArrayList;
import java.util.concurrent.CompletableFuture;

public interface KeyValues {
    CompletableFuture<Void> putValue(String key, String value) throws HiveException;

    CompletableFuture<Void> putValue(String key, String value, Callback<Void> callback) throws HiveException;

    CompletableFuture<Void> putValue(String key, byte[] value) throws HiveException;

    CompletableFuture<Void> putValue(String key, byte[] value, Callback<Void> callback) throws HiveException;

    CompletableFuture<Void> setValue(String key, String value) throws HiveException;

    CompletableFuture<Void> setValue(String key, String value, Callback<Void> callback) throws HiveException;

    CompletableFuture<Void> setValue(String key, byte[] value) throws HiveException;

    CompletableFuture<Void> setValue(String key, byte[] value, Callback<Void> callback) throws HiveException;

    CompletableFuture<ArrayList<byte[]>> getValues(String key) throws HiveException;

    CompletableFuture<ArrayList<byte[]>> getValues(String key, Callback<ArrayList<byte[]>> callback) throws HiveException;

    CompletableFuture<Void> deleteKey(String key) throws HiveException;

    CompletableFuture<Void> deleteKey(String key, Callback<Void> callback) throws HiveException;
}
