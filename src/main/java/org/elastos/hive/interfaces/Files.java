package org.elastos.hive.interfaces;

import org.elastos.hive.Callback;
import org.elastos.hive.exception.HiveException;

import java.io.InputStream;
import java.io.OutputStream;
import java.io.Reader;
import java.io.Writer;
import java.util.ArrayList;
import java.util.concurrent.CompletableFuture;

public interface Files {
    CompletableFuture<Void> put(String data, String remoteFile) throws HiveException;

    CompletableFuture<Void> put(String data, String remoteFile, Callback<Void> callback) throws HiveException;

    CompletableFuture<Void> put(byte[] data, String remoteFile) throws HiveException;

    CompletableFuture<Void> put(byte[] data, String remoteFile, Callback<Void> callback) throws HiveException;

    CompletableFuture<Void> put(InputStream input, String remoteFile) throws HiveException;

    CompletableFuture<Void> put(InputStream input, String remoteFile, Callback<Void> callback) throws HiveException;

    CompletableFuture<Void> put(Reader reader, String remoteFile) throws HiveException;

    CompletableFuture<Void> put(Reader reader, String remoteFile, Callback<Void> callback) throws HiveException;

    CompletableFuture<Long> size(String remoteFile) throws HiveException;

    CompletableFuture<Long> size(String remoteFile, Callback<Long> callback) throws HiveException;

    CompletableFuture<String> getAsString(String remoteFile) throws HiveException;

    CompletableFuture<String> getAsString(String remoteFile, Callback<String> callback) throws HiveException;

    CompletableFuture<byte[]> getAsBuffer(String remoteFile) throws HiveException;

    CompletableFuture<byte[]> getAsBuffer(String remoteFile, Callback<byte[]> callback) throws HiveException;

    CompletableFuture<Long> get(String remoteFile, OutputStream output) throws HiveException;

    CompletableFuture<Long> get(String remoteFile, OutputStream output, Callback<Long> callback) throws HiveException;

    CompletableFuture<Long> get(String remoteFile, Writer writer) throws HiveException;

    CompletableFuture<Long> get(String remoteFile, Writer writer, Callback<Long> callback) throws HiveException;

    CompletableFuture<Void> delete(String remoteFile) throws HiveException;

    CompletableFuture<Void> delete(String remoteFile, Callback<Void> callback) throws HiveException;

    CompletableFuture<ArrayList<String>> list() throws HiveException;

    CompletableFuture<ArrayList<String>> list(Callback<ArrayList<String>> callback) throws HiveException;

}
