package org.elastos.hive;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.Semaphore;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

public final class AuthServer {
	private final Semaphore authLock;
	private HttpServer server = null;
	private String authCode;

	public AuthServer(Semaphore semph) throws HiveException {
		this.authLock = semph;

		try {
			semph.acquire();

			server = HttpServer.create(new InetSocketAddress("localhost", 44316), 0);
			server.createContext("/", new AuthHandler());
			server.setExecutor(null);

		} catch (InterruptedException e) {
			throw new HiveException(e.getMessage());
		} catch (IOException e) {
			throw new HiveException(e.getMessage());
		}
	}

	public void start() {
		server.start();
	}

	public String getAuthCode() {
		return authCode;
	}

	public void close() {
		server.stop(0);
	}

	private class AuthHandler implements HttpHandler {
		@Override
		public void handle(HttpExchange httpExchange) throws IOException {
			byte[] response = "<script type='text/javascript'>window.close()</script>"
					.getBytes(StandardCharsets.UTF_8);
			httpExchange.sendResponseHeaders(200, response.length);
			OutputStream os = httpExchange.getResponseBody();
			os.write(response);
			os.close();

			String[] query = httpExchange.getRequestURI().getQuery().split("=");

			switch (query[0]) {
				case "code":
					authCode = query[1];
					break;
				case "error":
					throw new IOException("Login error");
				default:
					throw new IOException("Unrecognized Server");
			}
			authLock.release();
		}
	}
}
