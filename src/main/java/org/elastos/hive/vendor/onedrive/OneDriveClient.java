package org.elastos.hive.vendor.onedrive;

import org.elastos.hive.Callback;
import org.elastos.hive.Client;
import org.elastos.hive.NullCallback;
import org.elastos.hive.exception.HiveException;
import org.elastos.hive.interfaces.Files;
import org.elastos.hive.interfaces.IPFS;
import org.elastos.hive.interfaces.KeyValues;

import java.io.InputStream;
import java.io.OutputStream;
import java.io.Reader;
import java.io.Writer;
import java.util.ArrayList;
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
    public Files getFiles() {
        return (Files) this;
    }

    @Override
    public IPFS getIPFS() {
        return null;
    }

    @Override
    public KeyValues getKeyValues() {
        return (KeyValues) this;
    }

    @Override
    public CompletableFuture<Void> put(byte[] from, String remoteFile) {
        return put(from, remoteFile, new NullCallback<Void>());
    }

    @Override
    public CompletableFuture<Void> put(byte[] data, String remoteFile, Callback<Void> callback) {
        // TODO:
        return null;
    }

    @Override
    public CompletableFuture<Void> put(String data, String remoteFile) {
        return put(data, remoteFile, new NullCallback<Void>());
    }

    @Override
    public CompletableFuture<Void> put(String data, String remoteFile, Callback<Void> callback) {
        // TODO;
        return null;
    }

    @Override
    public CompletableFuture<Void> put(InputStream input, String remoteFile) {
        return put(input, remoteFile, new NullCallback<Void>());
    }

    @Override
    public CompletableFuture<Void> put(InputStream input, String remoteFile, Callback<Void> callback) {
        // TODO;
        return null;
    }

    @Override
    public CompletableFuture<Void> put(Reader reader, String remoteFile) {
        return put(reader, remoteFile, new NullCallback<Void>());
    }

    @Override
    public CompletableFuture<Void> put(Reader reader, String remoteFile, Callback<Void> callback) {
        // TODO;
        return null;
    }

    @Override
    public CompletableFuture<Long> size(String remoteFile) {
        return null;
    }

    @Override
    public CompletableFuture<Long> size(String remoteFile, Callback<Long> callback) {
        return null;
    }

    @Override
    public CompletableFuture<StringBuffer> getStringBuffer(String remoteFile) {
        return null;
    }

    @Override
    public CompletableFuture<StringBuffer> getStringBuffer(String remoteFile, Callback<StringBuffer> callback) {
        return null;
    }

    @Override
    public CompletableFuture<byte[]> get(String remoteFile) {
        return null;
    }

    @Override
    public CompletableFuture<byte[]> get(String remoteFile, Callback<byte[]> callback) {
        return null;
    }

    @Override
    public CompletableFuture<OutputStream> getOutputStream(String remoteFile) {
        return null;
    }

    @Override
    public CompletableFuture<OutputStream> getOutputStream(String remoteFile, Callback<OutputStream> callback) {
        return null;
    }

    @Override
    public CompletableFuture<Writer> getWriter(String remoteFile) {
        return null;
    }

    @Override
    public CompletableFuture<Writer> getWriter(String remoteFile, Callback<Writer> callback) {
        return null;
    }

    @Override
    public CompletableFuture<Void> putValue(String key, String value) {
        return null;
    }

    @Override
    public CompletableFuture<Void> putValue(String key, String value, Callback<Void> callback) {
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
    public CompletableFuture<Void> setValue(String key, String value) {
        return null;
    }

    @Override
    public CompletableFuture<Void> setValue(String key, String value, Callback<Void> callback) {
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
    public CompletableFuture<ArrayList<byte[]>> getValues(String key) {
        return null;
    }

    @Override
    public CompletableFuture<ArrayList<byte[]>> getValues(String key, Callback<ArrayList<byte[]>> callback) {
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
    public CompletableFuture<ArrayList<String>> list() {
        return null;
    }

    @Override
    public CompletableFuture<ArrayList<String>> list(Callback<ArrayList<String>> callback) {
        return null;
    }

//    @Override
//    public CompletableFuture<Length> size(String remoteFile) {
//        return size(remoteFile, new NullCallback<Length>());
//    }
//
//    @Override
//    public CompletableFuture<Length> size(String remoteFile, Callback<Length> callback) {
//        // TODO;
//        return null;
//    }
//
//    @Override
//    public CompletableFuture<Length> get(String remoteFile, StringBuffer buffer) {
//        return get(remoteFile, buffer, new NullCallback<Length>());
//    }
//
//    @Override
//    public CompletableFuture<Length> get(String remoteFile, StringBuffer buffer, Callback<Length> callback) {
//        // TODO;
//        return null;
//    }
//
//    @Override
//    public CompletableFuture<Length> get(String remoteFile, byte[] byteArray) {
//        return get(remoteFile, byteArray, new NullCallback<Length>());
//    }
//
//    @Override
//    public CompletableFuture<Length> get(String remoteFile, byte[] byteArray, Callback<Length> callback) {
//        // TODO;
//        return null;
//    }
//
//    @Override
//    public CompletableFuture<Length> get(String remoteFile, OutputStream output) {
//        return get(remoteFile, output, new NullCallback<Length>());
//    }
//
//    @Override
//    public CompletableFuture<Length> get(String remoteFile, OutputStream output, Callback<Length> callback) {
//        // TODO;
//        return null;
//    }
//
//    @Override
//    public CompletableFuture<Length> get(String remoteFile, Writer writer) {
//        return get(remoteFile, writer, new NullCallback<Length>());
//    }
//
//    @Override
//    public CompletableFuture<Length> get(String remoteFile, Writer writer, Callback<Length> callback) {
//        // TODO;
//        return null;
//    }
//
//    @Override
//    public CompletableFuture<Void> putValue(String key, String value) {
//        return putValue(key, value, new NullCallback<Void>());
//    }
//
//    @Override
//    public CompletableFuture<Void> putValue(String key, String value, Callback<Void> callback) {
//        // TODO;
//        return null;
//    }
//
//    @Override
//    public CompletableFuture<Void> putValue(String key, byte[] value) {
//        return putValue(key, value, new NullCallback<Void>());
//    }
//
//    @Override
//    public CompletableFuture<Void> putValue(String key, byte[] value, Callback<Void> callback) {
//        // TODO;
//        return null;
//    }
//
//    @Override
//    public CompletableFuture<Void> setValue(String key, String value) {
//        return setValue(key, value, new NullCallback<Void>());
//    }
//
//    @Override
//    public CompletableFuture<Void> setValue(String key, String value, Callback<Void> callback) {
//        // TODO:
//        return null;
//    }
//
//    @Override
//    public CompletableFuture<Void> setValue(String key, byte[] value) {
//        return setValue(key, value, new NullCallback<Void>());
//    }
//
//    @Override
//    public CompletableFuture<Void> setValue(String key, byte[] value, Callback<Void> callback) {
//        // TODO;
//        return null;
//    }
//
//    @Override
//    public CompletableFuture<ValueList> getValues(String key) {
//        return getValues(key, new NullCallback<ValueList>());
//    }
//
//    @Override
//    public CompletableFuture<ValueList> getValues(String key, Callback<ValueList> callback) {
//        // TODO;
//        return null;
//    }
//
//    @Override
//    public CompletableFuture<Void> delete(String remoteFile) {
//        return delete(remoteFile, new NullCallback<Void>());
//    }
//
//    @Override
//    public CompletableFuture<Void> delete(String remoteFile, Callback<Void> callback) {
//        // TODO:
//        return null;
//    }
//
//    @Override
//    public CompletableFuture<FileList> list() {
//        return list(new NullCallback<FileList>());
//    }
//
//    @Override
//    public CompletableFuture<FileList> list(Callback<FileList> callback) {
//        // TODO;
//        return null;
//    }
}
