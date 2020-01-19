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

    CompletableFuture<String> getAsString(String remoteFile);

    CompletableFuture<String> getAsString(String remoteFile, Callback<String> callback);

    CompletableFuture<byte[]> getAsBuffer(String remoteFile);

    CompletableFuture<byte[]> getAsBuffer(String remoteFile, Callback<byte[]> callback);

    CompletableFuture<Long> get(String remoteFile, OutputStream output);

    CompletableFuture<Long> get(String remoteFile, OutputStream output, Callback<Long> callback);

    CompletableFuture<Long> get(String remoteFile, Writer writer);

    CompletableFuture<Long> get(String remoteFile, Writer writer, Callback<Long> callback);

    CompletableFuture<Void> delete(String remoteFile);

    CompletableFuture<Void> delete(String remoteFile, Callback<Void> callback);

    CompletableFuture<ArrayList<String>> list();

    CompletableFuture<ArrayList<String>> list(Callback<ArrayList<String>> callback);

}
