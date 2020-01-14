package org.elastos.hive.interfaces;

import org.elastos.hive.Callback;

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

    CompletableFuture<StringBuffer> getFileToStringBuffer(String cid);

    CompletableFuture<StringBuffer> getFileToStringBuffer(String cid, Callback<StringBuffer> callback);

    CompletableFuture<byte[]> getFileToBuffer(String cid);

    CompletableFuture<byte[]> getFileToBuffer(String cid, Callback<byte[]> callback);

    CompletableFuture<OutputStream> getFileToOutputStream(String cid);

    CompletableFuture<OutputStream> getFileToOutputStream(String cid, Callback<OutputStream> callback);

    CompletableFuture<Writer> getFileToWriter(String cid);

    CompletableFuture<Writer> getFileToWriter(String cid, Callback<Writer> callback);
}
