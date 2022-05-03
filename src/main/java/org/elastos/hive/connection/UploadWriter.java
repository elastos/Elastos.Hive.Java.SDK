package org.elastos.hive.connection;

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;

/**
 * The UploadOutputStreamWriter is for uploading file by connection.
 */
public class UploadWriter extends OutputStreamWriter {
	private final HttpURLConnection connection;
	private ConnectionClosure connectionClosure;

	public UploadWriter(HttpURLConnection connection, OutputStream output) {
		super(output);
		this.connection = connection;
		this.connectionClosure = new ConnectionClosure(connection, output);
	}

	public String getCid() {
		return this.connectionClosure.getCid();
	}

	@Override
	public void close() throws IOException {
		this.connectionClosure.confirmClosed();
	}
}
