package org.elastos.hive.interfaces;

import java.util.concurrent.CompletableFuture;

public interface HiveVault {

    CompletableFuture<String> register(String did, String pwd);

    CompletableFuture<String> login(String did, String pwd);

    CompletableFuture<String> createCollection();

    CompletableFuture<String> uploader();

    CompletableFuture<String> list();

    CompletableFuture<String> downloader();

    CompletableFuture<String> delete();
}
