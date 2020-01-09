package org.elastos.hive.vendor.onedrive;

import org.elastos.hive.Callback;
import org.elastos.hive.Client;
import org.elastos.hive.NullCallback;
import org.elastos.hive.exception.HiveException;
import org.elastos.hive.interfaces.Files;
import org.elastos.hive.interfaces.IPFS;
import org.elastos.hive.interfaces.KeyValues;
import org.elastos.hive.result.FileList;
import org.elastos.hive.result.Length;
import org.elastos.hive.result.ValueList;
import org.elastos.hive.result.Void;

import java.io.InputStream;
import java.io.OutputStream;
import java.io.Reader;
import java.io.Writer;
import java.lang.reflect.Type;
import java.util.concurrent.CompletableFuture;

final class OneDriveClient extends Client implements Files, KeyValues {
    OneDriveClient(Client.Options options) {
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
    protected Object getInterface(Type type) {
        if (Files.class == type || KeyValues.class == type)
            return this;

        return null;
    }

    @Override
    public void setEncryptKey(String encryptKey) {
    }

    @Override
    public CompletableFuture<Void> put(byte[] from, String remoteFile) {
        return put(from, remoteFile, false);
    }

    @Override
    public CompletableFuture<Void> put(byte[] from, String remoteFile, boolean encrypt) {
        return put(from, remoteFile, encrypt, new NullCallback<Void>());
    }

    @Override
    public CompletableFuture<Void> put(byte[] from, String remoteFile, boolean encrypt, Callback<Void> callback) {
        return null;
    }

    @Override
    public CompletableFuture<Void> put(String localPath, String remoteFile) {
        return null;
    }

    @Override
    public CompletableFuture<Void> put(String localPath, String remoteFile, boolean encrypt) {
        return null;
    }

    @Override
    public CompletableFuture<Void> put(String localPath, String remoteFile, boolean encrypt, Callback<Void> callback) {
        return null;
    }

    @Override
    public CompletableFuture<Void> put(InputStream input, String remoteFile) {
        return null;
    }

    @Override
    public CompletableFuture<Void> put(InputStream input, String remoteFile, boolean encrypt) {
        return null;
    }

    @Override
    public CompletableFuture<Void> put(InputStream input, String remoteFile, boolean encrypt, Callback<Void> callback) {
        return null;
    }

    @Override
    public CompletableFuture<Void> put(Reader reader, String remoteFile) {
        return null;
    }

    @Override
    public CompletableFuture<Void> put(Reader reader, String remoteFile, boolean encrypt) {
        return null;
    }

    @Override
    public CompletableFuture<Void> put(Reader reader, String remoteFile, boolean encrypt, Callback<Void> callback) {
        return null;
    }

    @Override
    public CompletableFuture<Length> size(String remoteFile) {
        return null;
    }

    @Override
    public CompletableFuture<Length> size(String remoteFile, Callback<Length> callback) {
        return null;
    }

    @Override
    public CompletableFuture<Length> get(String remoteFile, byte[] byteArray) {
        return null;
    }

    @Override
    public CompletableFuture<Length> get(String remoteFile, byte[] byteArray, boolean decrypt) {
        return null;
    }

    @Override
    public CompletableFuture<Length> get(String remoteFile, byte[] byteArray, boolean decrypt, Callback<Length> callback) {
        return null;
    }

    @Override
    public CompletableFuture<Length> get(String remoteFile, String localPath) {
        return null;
    }

    @Override
    public CompletableFuture<Length> get(String remoteFile, String localPath, boolean decrypt) {
        return null;
    }

    @Override
    public CompletableFuture<Length> get(String remoteFile, String localPath, boolean decrypt, Callback<Length> callback) {
        return null;
    }

    @Override
    public CompletableFuture<Length> get(String remoteFile, OutputStream output) {
        return null;
    }

    @Override
    public CompletableFuture<Length> get(String remoteFile, OutputStream output, boolean decrypt) {
        return null;
    }

    @Override
    public CompletableFuture<Length> get(String remoteFile, OutputStream output, boolean decrypt, Callback<Length> callback) {
        return null;
    }

    @Override
    public CompletableFuture<Length> get(String remoteFile, Writer writer) {
        return null;
    }

    @Override
    public CompletableFuture<Length> get(String remoteFile, Writer writer, boolean decrypt) {
        return null;
    }

    @Override
    public CompletableFuture<Length> get(String remoteFile, Writer writer, boolean decrypt, Callback<Length> callback) {
        return null;
    }

    @Override
    public CompletableFuture<Void> putValue(String key, byte[] value) {
        return null;
    }

    @Override
    public CompletableFuture<Void> putValue(String key, byte[] value, Callback<Void> callback) {
        return null;
    }

    @Override
    public CompletableFuture<Void> setValue(String key, byte[] value) {
        return null;
    }

    @Override
    public CompletableFuture<Void> setValue(String key, byte[] value, Callback<Void> callback) {
        return null;
    }

    @Override
    public CompletableFuture<ValueList> getValues(String key) {
        return null;
    }

    @Override
    public CompletableFuture<ValueList> getValues(String key, Callback<ValueList> callback) {
        return null;
    }

    @Override
    public CompletableFuture<Void> delete(String remoteFile) {
        return null;
    }

    @Override
    public CompletableFuture<Void> delete(String remoteFile, Callback<Void> callback) {
        return null;
    }

    @Override
    public CompletableFuture<FileList> list() {
        return null;
    }

    @Override
    public CompletableFuture<FileList> list(Callback<FileList> callback) {
        return null;
    }
}
