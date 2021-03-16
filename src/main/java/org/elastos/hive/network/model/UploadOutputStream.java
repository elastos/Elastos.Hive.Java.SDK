package org.elastos.hive.network.model;

import org.elastos.hive.connection.ConnectionManager;

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
		originalStream.write(bytes);
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
		// In order for uploads to complete successfully in chunk mode, we have to read the server response.
		// This doesn't seem to behave identically on all devices. Some devices work without this. But some devices
		// don't terminate the api call until the server response is read.
		//
		// This close() method on the output stream is the only location where we know user has finished writing his file.
		ConnectionManager.readConnection(connection);
		originalStream.close();
	}
}
