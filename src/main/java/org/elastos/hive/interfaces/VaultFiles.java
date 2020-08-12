package org.elastos.hive.interfaces;

import org.elastos.hive.Callback;

import java.io.OutputStream;
import java.io.Writer;
import java.util.ArrayList;
import java.util.concurrent.CompletableFuture;

public interface VaultFiles {

    CompletableFuture<String> createFile(String remoteFile);

    CompletableFuture<String> createFile(String remoteFile, Callback<Long> callback);

    CompletableFuture<Void> upload(String url, byte[] data, String remoteFile);

    CompletableFuture<Void> upload(String url, byte[] data, String remoteFile, Callback<Long> callback);

    CompletableFuture<Long> downloader(String remoteFile, OutputStream output);

    CompletableFuture<Long> downloader(String remoteFile, OutputStream output, Callback<Long> callback);

    CompletableFuture<Long> downloader(String remoteFile, Writer writer);

    CompletableFuture<Long> downloader(String remoteFile, Writer writer, Callback<Long> callback);

    CompletableFuture<Void> deleteFile(String remoteFile);

    CompletableFuture<Void> deleteFile(String remoteFile, Callback<Long> callback);

    CompletableFuture<Void> createFolder(String folder);

    CompletableFuture<Void> createFolder(String folder, Callback<Long> callback);

    CompletableFuture<Void> move(String src, String dst);

    CompletableFuture<Void> move(String src, String dst, Callback<Long> callback);

    CompletableFuture<Void> copy(String src, String dst);

    CompletableFuture<Void> copy(String src, String dst, Callback<Long> callback);

    CompletableFuture<Void> hash(String remoteFile);

    CompletableFuture<Void> hash(String remoteFile, Callback<Long> callback);

    CompletableFuture<ArrayList<String>> list(String folder);

    CompletableFuture<ArrayList<String>> list(String folder, Callback<Long> callback);

    CompletableFuture<Long> size(String remoteFile);

    CompletableFuture<Long> size(String remoteFile, Callback<Long> callback);
}
