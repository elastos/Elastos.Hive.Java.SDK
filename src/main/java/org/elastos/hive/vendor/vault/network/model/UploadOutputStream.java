package org.elastos.hive.vendor.vault.network.model;

import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;

public class UploadOutputStream extends OutputStream {
    private HttpURLConnection connection;
    private OutputStream originalStream;

    public UploadOutputStream(HttpURLConnection connection, OutputStream stream) {
        this.connection = connection;
        this.originalStream = stream;
    }

    @Override
    public void write(int b) throws IOException {
        originalStream.write(b);
    }

    @Override
    public void write(byte[] bytes) throws IOException {
        originalStream.write(bytes, 0, bytes.length);
    }

    @Override
    public void write(byte[] bytes, int offset, int length) throws IOException {
        originalStream.write(bytes, offset, length);
    }

    @Override
    public void flush() throws IOException {
        originalStream.flush();
    }

    @Override
    public void close() throws IOException {
        originalStream.close();
        connection.disconnect();
    }
}
