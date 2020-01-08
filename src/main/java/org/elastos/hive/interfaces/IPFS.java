package org.elastos.hive.interfaces;

import org.elastos.hive.Callback;
import org.elastos.hive.result.Hash;
import org.elastos.hive.result.Length;

import java.io.InputStream;
import java.io.OutputStream;
import java.io.Reader;
import java.io.Writer;
import java.util.concurrent.CompletableFuture;

public interface IPFS {
    CompletableFuture<Hash> put(byte[] data);
    CompletableFuture<Hash> put(byte[] data, boolean encrypt);
    CompletableFuture<Hash> put(byte[] data, boolean encrypt, Callback<Hash> callback);

    CompletableFuture<Hash> put(String localPath);
    CompletableFuture<Hash> put(String localPath, boolean encrypt);
    CompletableFuture<Hash> put(String localPath, boolean encrypt, Callback<Hash> callback);

    CompletableFuture<Hash> put(InputStream input);
    CompletableFuture<Hash> put(InputStream input, boolean encrypt);
    CompletableFuture<Hash> put(InputStream input, boolean encrypt, Callback<Hash> callback);

    CompletableFuture<Hash> put(Reader reader);
    CompletableFuture<Hash> put(Reader reader, boolean encrypt);
    CompletableFuture<Hash> put(Reader reader, boolean encrypt, Callback<Hash> callback);

    CompletableFuture<Length> size(Hash cid);
    CompletableFuture<Length> size(Hash cid, Callback<Length> callback);

    CompletableFuture<Length> get(String cid, byte[] buffer);
    CompletableFuture<Length> get(String cid, byte[] buffer, boolean decrypt);
    CompletableFuture<Length> get(String cid, byte[] buffer, boolean decrypt, Callback<Length> callback);

    CompletableFuture<Length> get(String cid, String localPath);
    CompletableFuture<Length> get(String cid, String localPath, boolean decrypt);
    CompletableFuture<Length> get(String cid, String localPath, boolean decrypt, Callback<Length> callback);

    CompletableFuture<Length> get(String cid, OutputStream output);
    CompletableFuture<Length> get(String cid, OutputStream output, boolean decrypt);
    CompletableFuture<Length> get(String cid, OutputStream output, boolean decrypt, Callback<Length> callback);

    CompletableFuture<Length> get(String cid, Writer writer);
    CompletableFuture<Length> get(String cid, Writer writer, boolean decrypt);
    CompletableFuture<Length> get(String cid, Writer writer, boolean decrypt, Callback<Hash> callback);
}
