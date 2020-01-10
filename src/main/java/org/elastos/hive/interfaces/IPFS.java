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
    CompletableFuture<Hash> put(String data);
    CompletableFuture<Hash> put(String data, Callback<Hash> callback);

    CompletableFuture<Hash> put(byte[] data);
    CompletableFuture<Hash> put(byte[] data, Callback<Hash> callback);

    CompletableFuture<Hash> put(InputStream input);
    CompletableFuture<Hash> put(InputStream input, Callback<Hash> callback);

    CompletableFuture<Hash> put(Reader reader);
    CompletableFuture<Hash> put(Reader reader, Callback<Hash> callback);

    CompletableFuture<Length> size(Hash cid);
    CompletableFuture<Length> size(Hash cid, Callback<Length> callback);

    CompletableFuture<Length> get(String cid, StringBuffer buffer);
    CompletableFuture<Length> get(String cid, StringBuffer buffer, Callback<Length> callback);

    CompletableFuture<Length> get(String cid, byte[] buffer);
    CompletableFuture<Length> get(String cid, byte[] buffer, Callback<Length> callback);

    CompletableFuture<Length> get(String cid, OutputStream output);
    CompletableFuture<Length> get(String cid, OutputStream output, Callback<Length> callback);

    CompletableFuture<Length> get(String cid, Writer writer);
    CompletableFuture<Length> get(String cid, Writer writer, Callback<Length> callback);
}
