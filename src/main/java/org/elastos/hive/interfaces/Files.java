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
    CompletableFuture<Void> put(byte[] from, String remoteFile);
    CompletableFuture<Void> put(byte[] from, String remoteFile, boolean encrypt);
    CompletableFuture<Void> put(byte[] from, String remoteFile, boolean encrypt, Callback<Void> callback);

    CompletableFuture<Void> put(String localPath, String remoteFile);
    CompletableFuture<Void> put(String localPath, String remoteFile, boolean encrypt);
    CompletableFuture<Void> put(String localPath, String remoteFile, boolean encrypt, Callback<Void> callback);

    CompletableFuture<Void> put(InputStream input, String remoteFile);
    CompletableFuture<Void> put(InputStream input, String remoteFile, boolean encrypt);
    CompletableFuture<Void> put(InputStream input, String remoteFile, boolean encrypt, Callback<Void> callback);

    CompletableFuture<Void> put(Reader reader, String remoteFile);
    CompletableFuture<Void> put(Reader reader, String remoteFile, boolean encrypt);
    CompletableFuture<Void> put(Reader reader, String remoteFile, boolean encrypt, Callback<Void> callback);

    CompletableFuture<Length> size(String remoteFile);
    CompletableFuture<Length> size(String remoteFile, Callback<Length> callback);

    CompletableFuture<Length> get(String remoteFile, byte[] byteArray);
    CompletableFuture<Length> get(String remoteFile, byte[] byteArray, boolean decrypt);
    CompletableFuture<Length> get(String remoteFile, byte[] byteArray, boolean decrypt, Callback<Length> callback);

    CompletableFuture<Length> get(String remoteFile, String localPath);
    CompletableFuture<Length> get(String remoteFile, String localPath, boolean decrypt);
    CompletableFuture<Length> get(String remoteFile, String localPath, boolean decrypt, Callback<Length> callback);

    CompletableFuture<Length> get(String remoteFile, OutputStream output);
    CompletableFuture<Length> get(String remoteFile, OutputStream output, boolean decrypt);
    CompletableFuture<Length> get(String remoteFile, OutputStream output, boolean decrypt, Callback<Length> callback);

    CompletableFuture<Length> get(String remoteFile, Writer writer);
    CompletableFuture<Length> get(String remoteFile, Writer writer, boolean decrypt);
    CompletableFuture<Length> get(String remoteFile, Writer writer, boolean decrypt, Callback<Length> callback);

    CompletableFuture<Void> delete(String remoteFile);
    CompletableFuture<Void> delete(String remoteFile, Callback<Void> callback);

    CompletableFuture<FileList> list();
    CompletableFuture<FileList> list(Callback<FileList> callback);
}
