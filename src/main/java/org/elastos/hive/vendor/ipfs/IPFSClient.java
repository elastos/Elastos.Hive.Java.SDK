package org.elastos.hive.vendor.ipfs;

import org.elastos.hive.Callback;
import org.elastos.hive.Client;
import org.elastos.hive.NullCallback;
import org.elastos.hive.exception.HiveException;
import org.elastos.hive.interfaces.Files;
import org.elastos.hive.interfaces.IPFS;
import org.elastos.hive.interfaces.KeyValues;
import org.elastos.hive.result.Hash;
import org.elastos.hive.result.Length;

import java.io.InputStream;
import java.io.OutputStream;
import java.io.Reader;
import java.io.Writer;
import java.lang.reflect.Type;
import java.util.concurrent.CompletableFuture;

final class IPFSClient extends Client implements IPFS {
    IPFSClient(Options options) {
        // TODO;
    }

    @Override
    public void connect() throws HiveException {
    }

    @Override
    public void disconnect() {
    }

    @Override
    public boolean isConnected() {
        return false;
    }

    @Override
    public Object getInterface(Type type) {
        if (IPFS.class == type)
            return this;

        return null;
    }

    @Override
    public void setEncryptKey(String encryptKey) {
    }

    @Override
    public CompletableFuture<Hash> put(byte[] data) {
        return put(data, false);
    }

    @Override
    public CompletableFuture<Hash> put(byte[] data, boolean encrypt) {
        return put(data, encrypt, new NullCallback<Hash>());
    }

    @Override
    public CompletableFuture<Hash> put(byte[] data, boolean encrypt, Callback<Hash> callback) {
        return null;
    }

    @Override
    public CompletableFuture<Hash> put(String localPath) {
        return put(localPath, false);
    }

    @Override
    public CompletableFuture<Hash> put(String localPath, boolean encrypt) {
        return put(localPath, encrypt, new NullCallback<Hash>());
    }

    @Override
    public CompletableFuture<Hash> put(String localPath, boolean encrypt, Callback<Hash> callback) {
        return null;
    }

    @Override
    public CompletableFuture<Hash> put(InputStream input) {
        return put(input, false);
    }

    @Override
    public CompletableFuture<Hash> put(InputStream input, boolean encrypt) {
        return put(input, false, new NullCallback<Hash>());
    }

    @Override
    public CompletableFuture<Hash> put(InputStream input, boolean encrypt, Callback<Hash> callback) {
        return null;
    }

    @Override
    public CompletableFuture<Hash> put(Reader reader) {
        return put(reader, false);
    }

    @Override
    public CompletableFuture<Hash> put(Reader reader, boolean encrypt) {
        return put(reader, false, new NullCallback<Hash>());
    }

    @Override
    public CompletableFuture<Hash> put(Reader reader, boolean encrypt, Callback<Hash> callback) {
        return null;
    }

    @Override
    public CompletableFuture<Length> size(Hash cid) {
        return size(cid, new NullCallback<Length>());
    }

    @Override
    public CompletableFuture<Length> size(Hash cid, Callback<Length> callback) {
        return null;
    }

    @Override
    public CompletableFuture<Length> get(String cid, byte[] buffer) {
        return get(cid, buffer, false);
    }

    @Override
    public CompletableFuture<Length> get(String cid, byte[] buffer, boolean decrypt) {
        return get(cid, buffer, decrypt, new NullCallback<Length>());
    }

    @Override
    public CompletableFuture<Length> get(String cid, byte[] buffer, boolean decrypt, Callback<Length> callback) {
        return null;
    }

    @Override
    public CompletableFuture<Length> get(String cid, String localPath) {
        return get(cid, localPath, false);
    }

    @Override
    public CompletableFuture<Length> get(String cid, String localPath, boolean decrypt) {
        return get(cid, localPath, decrypt, new NullCallback<Length>());
    }

    @Override
    public CompletableFuture<Length> get(String cid, String localPath, boolean decrypt, Callback<Length> callback) {
        return null;
    }

    @Override
    public CompletableFuture<Length> get(String cid, OutputStream output) {
        return get(cid, output, false);
    }

    @Override
    public CompletableFuture<Length> get(String cid, OutputStream output, boolean decrypt) {
        return get(cid, output, false, new NullCallback<Length>());
    }

    @Override
    public CompletableFuture<Length> get(String cid, OutputStream output, boolean decrypt, Callback<Length> callback) {
        return null;
    }

    @Override
    public CompletableFuture<Length> get(String cid, Writer writer) {
        return get(cid, writer, false);
    }

    @Override
    public CompletableFuture<Length> get(String cid, Writer writer, boolean decrypt) {
        return get(cid, writer, decrypt, new NullCallback<>());
    }

    @Override
    public CompletableFuture<Length> get(String cid, Writer writer, boolean decrypt, Callback<Hash> callback) {
        return null;
    }
}
