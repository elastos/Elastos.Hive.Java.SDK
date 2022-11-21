package org.elastos.hive.connection;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.elastos.hive.exception.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.net.HttpURLConnection;
import java.security.InvalidParameterException;

class ConnectionClosure {
	private static final Logger log = LoggerFactory.getLogger(ConnectionClosure.class);

	private HttpURLConnection connection;
	private OutputStream output;
	private boolean is_closed;
	private Exception exception;
	private String cid;

	public ConnectionClosure(HttpURLConnection connection, OutputStream output) {
		this.connection = connection;
		this.output = output;
		this.is_closed = false;
	}

	private JsonNode getResponseBody() throws IOException {
		InputStream in = this.connection.getErrorStream();
		if (in == null) return null;

		BufferedReader reader = new BufferedReader(new InputStreamReader(in));
		StringBuilder result = new StringBuilder();
		String line = "";
		while ((line = reader.readLine()) != null) {
			line = line.trim();
			if (line.length() > 0)
				result.append(line);
		}
		return new ObjectMapper().readTree(result.toString());
	}

	/**
	 * handle error response.
	 * @return If got an error.
	 * @throws IOException
	 */
	private boolean handleErrorResponse() throws IOException {
		int errorCode = this.connection.getResponseCode();
		if (errorCode >= 200 && errorCode < 300)
			return false;

		JsonNode result = this.getResponseBody();
		if (result == null) return true;

		switch (errorCode) {
			case NodeRPCException.UNAUTHORIZED:
				this.exception = new UnauthorizedException(result.toString());
			case NodeRPCException.FORBIDDEN:
				this.exception = new VaultForbiddenException(result.toString());
			case NodeRPCException.BAD_REQUEST:
				this.exception = new InvalidParameterException(result.toString());
			case NodeRPCException.NOT_FOUND:
				this.exception = new NotFoundException(result.toString());
			default:
				this.exception = new ServerUnknownException(result.toString());
		}
		return true;
	}

	private void handleResponse() {
		if (this.is_closed)
			return;
		try {
			this.is_closed = true;
			this.output.close();

			if (this.handleErrorResponse())
				return;

			JsonNode result = this.getResponseBody();
			if (result != null && result.has("cid"))
				// INFO: ConnectionClosure is also used for uploading file by script.
				this.cid = result.get("cid").asText();
		} catch (IOException e) {
			log.error("Failed to handle the response of the connection: " + e.getMessage());
			this.exception = e;
		}
	}

	public void confirmClosed() {
		this.handleResponse();
	}

	public String getCid() {
		this.handleResponse();
		if (this.exception != null)
			throw new RuntimeException(this.exception);
		if (this.cid == null) {
			throw new RuntimeException("Can not get the cid.");
		}
		return this.cid;
	}
}
