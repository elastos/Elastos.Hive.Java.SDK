package org.elastos.hive.interfaces;

import org.elastos.hive.Callback;
import org.elastos.hive.exception.HiveException;

import java.io.InputStream;
import java.io.OutputStream;
import java.io.Reader;
import java.io.Writer;
import java.util.concurrent.CompletableFuture;

public interface IPFS {
    CompletableFuture<String> put(String data);

    CompletableFuture<String> put(String data, Callback<String> callback);

    CompletableFuture<String> put(byte[] data);

    CompletableFuture<String> put(byte[] data, Callback<String> callback);

    CompletableFuture<String> put(InputStream input);

    CompletableFuture<String> put(InputStream input, Callback<String> callback);

    CompletableFuture<String> put(Reader reader);

    CompletableFuture<String> put(Reader reader, Callback<String> callback);

    CompletableFuture<Long> size(String cid);

    CompletableFuture<Long> size(String cid, Callback<Long> callback);

    CompletableFuture<String> getAsString(String cid);

    CompletableFuture<String> getAsString(String cid, Callback<String> callback);

    CompletableFuture<byte[]> getAsBuffer(String cid);

    CompletableFuture<byte[]> getAsBuffer(String cid, Callback<byte[]> callback);

    CompletableFuture<Long> get(String cid, OutputStream output);

    CompletableFuture<Long> get(String cid, OutputStream output, Callback<Long> callback);

    CompletableFuture<Long> get(String cid, Writer writer);

    CompletableFuture<Long> get(String cid, Writer writer, Callback<Long> callback);
}
