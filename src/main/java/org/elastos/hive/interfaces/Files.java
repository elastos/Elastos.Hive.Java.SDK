package org.elastos.hive.interfaces;

import org.elastos.hive.Callback;

import java.io.InputStream;
import java.io.OutputStream;
import java.io.Reader;
import java.io.Writer;
import java.util.ArrayList;
import java.util.concurrent.CompletableFuture;

public interface Files {
    CompletableFuture<Void> put(String data, String remoteFile);

    CompletableFuture<Void> put(String data, String remoteFile, Callback<Void> callback);

    CompletableFuture<Void> put(byte[] data, String remoteFile);

    CompletableFuture<Void> put(byte[] data, String remoteFile, Callback<Void> callback);

    CompletableFuture<Void> put(InputStream input, String remoteFile);

    CompletableFuture<Void> put(InputStream input, String remoteFile, Callback<Void> callback);

    CompletableFuture<Void> put(Reader reader, String remoteFile);

    CompletableFuture<Void> put(Reader reader, String remoteFile, Callback<Void> callback);

    CompletableFuture<Long> size(String remoteFile);

    CompletableFuture<Long> size(String remoteFile, Callback<Long> callback);

    CompletableFuture<StringBuffer> getStringBuffer(String remoteFile);

    CompletableFuture<StringBuffer> getStringBuffer(String remoteFile, Callback<StringBuffer> callback);

    CompletableFuture<byte[]> get(String remoteFile);

    CompletableFuture<byte[]> get(String remoteFile, Callback<byte[]> callback);

    CompletableFuture<OutputStream> getOutputStream(String remoteFile);

    CompletableFuture<OutputStream> getOutputStream(String remoteFile, Callback<OutputStream> callback);

    CompletableFuture<Writer> getWriter(String remoteFile);

    CompletableFuture<Writer> getWriter(String remoteFile, Callback<Writer> callback);

    CompletableFuture<Void> delete(String remoteFile);

    CompletableFuture<Void> delete(String remoteFile, Callback<Void> callback);

    CompletableFuture<ArrayList<String>> list();

    CompletableFuture<ArrayList<String>> list(Callback<ArrayList<String>> callback);
}
