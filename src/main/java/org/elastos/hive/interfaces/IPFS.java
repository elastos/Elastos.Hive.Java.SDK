package org.elastos.hive.interfaces;

import org.elastos.hive.Callback;
import org.elastos.hive.exception.HiveException;

import java.io.InputStream;
import java.io.OutputStream;
import java.io.Reader;
import java.io.Writer;
import java.util.concurrent.CompletableFuture;

public interface IPFS {
    CompletableFuture<String> put(String data) throws HiveException;

    CompletableFuture<String> put(String data, Callback<String> callback) throws HiveException;

    CompletableFuture<String> put(byte[] data) throws HiveException;

    CompletableFuture<String> put(byte[] data, Callback<String> callback) throws HiveException;

    CompletableFuture<String> put(InputStream input) throws HiveException;

    CompletableFuture<String> put(InputStream input, Callback<String> callback) throws HiveException;

    CompletableFuture<String> put(Reader reader) throws HiveException;

    CompletableFuture<String> put(Reader reader, Callback<String> callback) throws HiveException;

    CompletableFuture<Long> size(String cid) throws HiveException;

    CompletableFuture<Long> size(String cid, Callback<Long> callback) throws HiveException;

    CompletableFuture<String> getAsString(String cid) throws HiveException;

    CompletableFuture<String> getAsString(String cid, Callback<String> callback) throws HiveException;

    CompletableFuture<byte[]> getAsBuffer(String cid) throws HiveException;

    CompletableFuture<byte[]> getAsBuffer(String cid, Callback<byte[]> callback) throws HiveException;

    CompletableFuture<Long> get(String cid, OutputStream output) throws HiveException;

    CompletableFuture<Long> get(String cid, OutputStream output, Callback<Long> callback) throws HiveException;

    CompletableFuture<Long> get(String cid, Writer writer) throws HiveException;

    CompletableFuture<Long> get(String cid, Writer writer, Callback<Long> callback) throws HiveException;
}
