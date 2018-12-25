package org.elastos.hive;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ConnectException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Map;


class HttpAPI {
	private static byte[] get(URL target) throws IOException {
		HttpURLConnection conn = (HttpURLConnection) target.openConnection();
		conn.setRequestMethod("GET");
		conn.setRequestProperty("Content-Type", "application/json");

		try {
			InputStream in = conn.getInputStream();
			return read(in);
		} catch (ConnectException e) {
			throw new RuntimeException("Failed to connect to hive daemon at " + target);
		} catch (IOException e) {
			throw new RuntimeException("IOException contacting IPFS daemon");
		}
	}

	private static byte[] post(URL target, byte[] body, Map<String, String> headers) throws IOException {
		HttpURLConnection conn = (HttpURLConnection) target.openConnection();
		for (String key: headers.keySet())
			conn.setRequestProperty(key, headers.get(key));
		conn.setDoOutput(true);
		conn.setChunkedStreamingMode(0);
		conn.setRequestMethod("POST");
		conn.setRequestProperty("Content-Type", "application/json");
		OutputStream out = conn.getOutputStream();
		out.write(body);
		out.flush();
		out.close();

		InputStream in = conn.getInputStream();
		return read(in);
	}

	private static byte[] read(InputStream in) throws IOException {
		ByteArrayOutputStream resp = new ByteArrayOutputStream();
		byte[] buf = new byte[4096];
		int r;
		while ((r=in.read(buf)) >= 0)
			resp.write(buf, 0, r);
		return resp.toByteArray();
	}
}
