package org.elastos.hive.interfaces;

import org.elastos.hive.Callback;
import org.elastos.hive.result.FileList;
import org.elastos.hive.result.Length;
import org.elastos.hive.result.Void;

import java.io.InputStream;
import java.io.OutputStream;
import java.io.Reader;
import java.io.Writer;
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

    CompletableFuture<Length> size(String remoteFile);
    CompletableFuture<Length> size(String remoteFile, Callback<Length> callback);

    CompletableFuture<Length> get(String remoteFile, StringBuffer buffer);
    CompletableFuture<Length> get(String remoteFile, StringBuffer buffer, Callback<Length> callback);

    CompletableFuture<Length> get(String remoteFile, byte[] byteArray);
    CompletableFuture<Length> get(String remoteFile, byte[] byteArray, Callback<Length> callback);

    CompletableFuture<Length> get(String remoteFile, OutputStream output);
    CompletableFuture<Length> get(String remoteFile, OutputStream output, Callback<Length> callback);

    CompletableFuture<Length> get(String remoteFile, Writer writer);
    CompletableFuture<Length> get(String remoteFile, Writer writer, Callback<Length> callback);

    CompletableFuture<Void> delete(String remoteFile);
    CompletableFuture<Void> delete(String remoteFile, Callback<Void> callback);

    CompletableFuture<FileList> list();
    CompletableFuture<FileList> list(Callback<FileList> callback);
}
