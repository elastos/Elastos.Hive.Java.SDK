package org.elastos.hive.interfaces;

import java.io.OutputStream;
import java.io.Writer;
import java.util.ArrayList;
import java.util.concurrent.CompletableFuture;

public interface VaultFiles {

    CompletableFuture<String> createFile(String remoteFile);

    CompletableFuture<Void> upload(String url, byte[] data, String remoteFile);

    CompletableFuture<Long> downloader(String remoteFile, OutputStream output);

    CompletableFuture<Long> downloader(String remoteFile, Writer writer);

    CompletableFuture<Void> deleteFile(String remoteFile);

    CompletableFuture<Void> createFolder(String folder);

    CompletableFuture<Void> move(String src, String dst);

    CompletableFuture<Void> copy(String src, String dst);

    CompletableFuture<Void> hash(String remoteFile);

    CompletableFuture<ArrayList<String>> list(String folder);

    CompletableFuture<Long> size(String remoteFile);
}
