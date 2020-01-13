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
        return true;
    }

    @Override
    public Files getFiles() {
        return null;
    }

    @Override
    public IPFS getIPFS() {
        return (IPFS)this;
    }

    @Override
    public KeyValues getKeyValues() {
        return null;
    }

    @Override
    public void setEncryptKey(String encryptKey) {
    }

    @Override
    public CompletableFuture<Hash> put(byte[] data) {
        return put(data, new NullCallback<Hash>());
    }

    @Override
    public CompletableFuture<Hash> put(byte[] data, Callback<Hash> callback) {
        // TODO:
        return null;
    }

    @Override
    public CompletableFuture<Hash> put(String data) {
        return put(data, new NullCallback<Hash>());
    }

    @Override
    public CompletableFuture<Hash> put(String data, Callback<Hash> callback) {
        // TODO:
        return null;
    }

    @Override
    public CompletableFuture<Hash> put(InputStream input) {
        return put(input, new NullCallback<Hash>());
    }

    @Override
    public CompletableFuture<Hash> put(InputStream input, Callback<Hash> callback) {
        // TODO:
        return null;
    }

    @Override
    public CompletableFuture<Hash> put(Reader reader) {
        return put(reader, new NullCallback<Hash>());
    }

    @Override
    public CompletableFuture<Hash> put(Reader reader, Callback<Hash> callback) {
        // TODO:
        return null;
    }

    @Override
    public CompletableFuture<Length> size(Hash cid) {
        return size(cid, new NullCallback<Length>());
    }

    @Override
    public CompletableFuture<Length> size(Hash cid, Callback<Length> callback) {
        // TODO:
        return null;
    }

    @Override
    public CompletableFuture<Length> get(String cid, StringBuffer buffer) {
        return get(cid, buffer, new NullCallback<Length>());
    }

    @Override
    public CompletableFuture<Length> get(String cid, StringBuffer buffer, Callback<Length> callback) {
        // TODO:
        return null;
    }

    @Override
    public CompletableFuture<Length> get(String cid, byte[] buffer) {
        return get(cid, buffer, new NullCallback<Length>());
    }

    @Override
    public CompletableFuture<Length> get(String cid, byte[] buffer, Callback<Length> callback) {
        // TODO:
        return null;
    }

    @Override
    public CompletableFuture<Length> get(String cid, OutputStream output) {
        return get(cid, output, new NullCallback<Length>());
    }

    @Override
    public CompletableFuture<Length> get(String cid, OutputStream output, Callback<Length> callback) {
        // TODO:
        return null;
    }

    @Override
    public CompletableFuture<Length> get(String cid, Writer writer) {
        return get(cid, writer, new NullCallback<Length>());
    }

    @Override
    public CompletableFuture<Length> get(String cid, Writer writer, Callback<Length> callback) {
        // TODO:
        return null;
    }
}
