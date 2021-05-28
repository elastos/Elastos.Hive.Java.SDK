package org.elastos.hive.connection;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;

public class UploadOutputStreamWriter extends OutputStreamWriter {
    private final HttpURLConnection connection;

    public UploadOutputStreamWriter(HttpURLConnection connection, @NotNull OutputStream out) {
        super(out);
        this.connection = connection;
    }

    @Override
    public void close() throws IOException {
        super.close();
        // In order to get the failed message from response body.
        ConnectionManager.readConnection(connection);
    }
}
