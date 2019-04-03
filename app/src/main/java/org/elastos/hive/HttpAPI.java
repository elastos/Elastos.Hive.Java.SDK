package org.elastos.hive;

import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;
import org.kamranzafar.jtar.TarEntry;
import org.kamranzafar.jtar.TarInputStream;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.ConnectException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLDecoder;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

class HttpAPI {
	private static final String TAG = "HttpAPI";

	static JSONObject get(String url) throws RuntimeException {
		try {
			Log.d(TAG, String.format("get url: %s", url));
			URL target = new URL(url);
			HttpURLConnection conn = (HttpURLConnection) target.openConnection();
			conn.setRequestMethod("GET");
			conn.setRequestProperty("Content-Type", "application/json");

			/* 200 represents HTTP OK */
			int responseCode = conn.getResponseCode();
			String value = "";
			if (responseCode == HttpURLConnection.HTTP_OK) {
				byte[] data = streamToValue(conn.getInputStream());
				if (data != null) {
					value = new String(data);
				}

				if (!value.isEmpty()) {
					return new JSONObject(value);
				}

				return null;
			}
			// responseCode: 400
			else if (responseCode == HttpURLConnection.HTTP_BAD_REQUEST) {
				byte[] data = streamToValue(conn.getErrorStream());
				if (data != null) {
					value = new String(data);
				}
			}
			// responseCode: 500
			else if (responseCode == HttpURLConnection.HTTP_INTERNAL_ERROR) {
				JSONObject json = streamToJson(conn.getErrorStream());
				if (json != null) {
					value = json.getString("Message");
				}
			}

			throw new RuntimeException(String.format("Http has error(rc: %d): [%s]" ,responseCode,  value));
		}
		catch (ConnectException e) {
			e.printStackTrace();
			throw new RuntimeException("Failed to connect to hive daemon at " + url);
		}
		catch (JSONException e) {
			throw new RuntimeException("New json object failed");
		}
		catch (IOException e) {
			throw new RuntimeException("IOException contacting IPFS daemon");
		}
	}

	static byte[] getValue(String url) throws RuntimeException {
		try {
			Log.d(TAG, String.format("getString url: %s", url));
			URL target = new URL(url);
			HttpURLConnection conn = (HttpURLConnection) target.openConnection();
			conn.setRequestMethod("GET");
			conn.setRequestProperty("Content-Type", "application/json");

			/* 200 represents HTTP OK */
			int responseCode = conn.getResponseCode();
			if (responseCode == HttpURLConnection.HTTP_OK) {
				if (url.contains(NodeCmd.FILE_GET)) {
					return tarStreamToValue(conn.getInputStream());
				}

				return streamToValue(conn.getInputStream());
			}

			String value = "";
			if (responseCode == HttpURLConnection.HTTP_BAD_REQUEST) {
				byte[] data = streamToValue(conn.getErrorStream());
				if (data != null) {
					value = new String(data);
				}
			}
			else if (responseCode == HttpURLConnection.HTTP_INTERNAL_ERROR) {
				JSONObject json = streamToJson(conn.getErrorStream());
				if (json != null) {
					value = json.getString("Message");
				}
			}

			throw new RuntimeException(String.format("Http has error(rc: %d): [%s]" ,responseCode,  value));
		}
		catch (ConnectException e) {
			throw new RuntimeException("Failed to connect to hive daemon at " + url);
		}
		catch (JSONException e) {
			throw new RuntimeException("Json getString failed");
		}
		catch (IOException e) {
			throw new RuntimeException("IOException contacting IPFS daemon");
		}
	}

	static JSONObject getForVersion(String url) throws RuntimeException {
		try {
			Log.d(TAG, String.format("get url: %s", url));
			URL target = new URL(url);
			HttpURLConnection conn = (HttpURLConnection) target.openConnection();
			conn.setRequestMethod("GET");
			conn.setRequestProperty("Content-Type", "application/json");

			/* 200 represents HTTP OK */
			int responseCode = conn.getResponseCode();
			if (responseCode == HttpURLConnection.HTTP_OK) {
				BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));

				Map<String, String> map = new HashMap<>();
				String lines;
				int pos;
				while ((lines = reader.readLine()) != null) {
					lines = URLDecoder.decode(lines, "utf-8");
					pos = lines.indexOf(":");
					String key = lines.substring(0, pos);
					String value = lines.substring(pos + 1).trim();
					map.put(key, value);
				}
				reader.close();

				return new JSONObject(map);
			}

			throw new RuntimeException("Error: the url is not connected, error code: " + responseCode);
		}
		catch (ConnectException e) {
			throw new RuntimeException("Failed to connect to hive daemon at " + url);
		}
		catch (IOException e) {
			throw new RuntimeException("IOException contacting IPFS");
		}
	}

	static JSONObject post(String baseUrl, String filePath, byte[] data, Map<String, String> paramsMap) {
		DataOutputStream requestStream = null;
		try {
			Log.d(TAG, String.format("get url: %s, filePath: %s", baseUrl, filePath));
			URL url = new URL(baseUrl);

			HttpURLConnection urlConn = (HttpURLConnection) url.openConnection();
			urlConn.setDoOutput(true);
			urlConn.setDoInput(true);
			urlConn.setUseCaches(false);

			//Set connected timeout
			urlConn.setConnectTimeout(5 * 1000);
			//Set read timeout
			urlConn.setReadTimeout(30 * 1000);
			//Set post mode
			urlConn.setRequestMethod("POST");
			//Set Keep-Alive
			urlConn.setRequestProperty("connection", "Keep-Alive");
			//Set Charset
			urlConn.setRequestProperty("Accept-Charset", "UTF-8");

			final String PREFIX = "--";
			final String NEWLINE = "\r\n";
			final String boundary = UUID.randomUUID().toString();
			urlConn.setRequestProperty("Content-Type", "multipart/form-data; boundary=" + boundary);

			requestStream = new DataOutputStream(urlConn.getOutputStream());

			requestStream.writeBytes(PREFIX + boundary + NEWLINE);
			//send parameters
			boolean isFile = false;
			if (filePath != null) {
				isFile = true;
			}

			StringBuilder args = new StringBuilder();
			//sending data or file.
			if (isFile) {
				File file = new File(filePath);
				String name = file.getName();
				args.append("Content-Disposition: form-data; name=\""
						+ name + "\"; filename=\"" + name + "\"; ");
			}
			else {
				args.append("Content-Disposition: form-data;");
			}

			if (paramsMap != null) {
				int pos = 0;
				int size = paramsMap.size();
				for (String key : paramsMap.keySet()) {
					args.append(String.format("%s=\"%s\"", key, paramsMap.get(key)));
					if (pos < size - 1) {
						args.append("; ");
					}
					pos++;
				}
			}

			args.append(NEWLINE);
			args.append("Content-Type: application/octet-stream\r\n");
			args.append(NEWLINE);
			requestStream.writeBytes(args.toString());

			//sending data or file.
			if (isFile) {
				File file = new File(filePath);
				FileInputStream fileInput = new FileInputStream(file);
				int bytesRead;
				byte[] buffer = new byte[1024];
				DataInputStream in = new DataInputStream(fileInput);
				while ((bytesRead = in.read(buffer)) != -1) {
					requestStream.write(buffer, 0, bytesRead);
				}
				fileInput.close();
			}
			else {
				requestStream.write(data, 0, data.length);
			}

			requestStream.writeBytes(NEWLINE);
			requestStream.flush();
			requestStream.writeBytes(PREFIX + boundary + PREFIX + NEWLINE);
			requestStream.flush();

			int responseCode = urlConn.getResponseCode();
			if (responseCode == HttpURLConnection.HTTP_OK) {
				return streamToJson(urlConn.getInputStream());
			}

			String value = "";
			if (responseCode == HttpURLConnection.HTTP_BAD_REQUEST) {
				byte[] result = streamToValue(urlConn.getErrorStream());
				if (result != null) {
					value = new String(result);
				}
			}
			else if (responseCode == HttpURLConnection.HTTP_INTERNAL_ERROR) {
				JSONObject json = streamToJson(urlConn.getErrorStream());
				if (json != null) {
					value = json.getString("Message");
				}
			}

			throw new RuntimeException(String.format("Http has error(rc: %d): [%s]" ,responseCode,  value));
		}
		catch (JSONException e) {
			throw new RuntimeException("Json getString failed");
		}
		catch (IOException e) {
			e.printStackTrace();
			throw new RuntimeException("IOException contacting IPFS");
		}
		finally {
			if (requestStream != null) {
				try {
					requestStream.close();
				}
				catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

	private static JSONObject streamToJson(InputStream is) {
		try {
			BufferedReader reader = new BufferedReader(new InputStreamReader(is));

			String lines;
			StringBuilder builder = new StringBuilder();
			while ((lines = reader.readLine()) != null) {
				lines = URLDecoder.decode(lines, "utf-8");
				builder.append(lines);
			}
			reader.close();

			Log.d("HttpAPI", "json="+builder.toString());
			if (!builder.toString().isEmpty()) {
				return new JSONObject(builder.toString());
			}
		}
		catch (JSONException e) {
			e.printStackTrace();
		}
		catch (IOException e) {
			e.printStackTrace();
			throw new RuntimeException("IOException");
		}

		return null;
	}

	private static byte[] streamToValue(InputStream is) {
		try {
			ByteArrayOutputStream bytestream = new ByteArrayOutputStream();
			int ch;
			while ((ch = is.read()) != -1) {
				bytestream.write(ch);
			}
			byte data[] = bytestream.toByteArray();
			bytestream.close();
			return data;
		}
		catch (IOException e) {
			e.printStackTrace();
		}

		return null;
	}

	private static byte[] tarStreamToValue(InputStream is) {
		try {
			TarInputStream tis = new TarInputStream(is);
			TarEntry entry;

			ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
			while((entry = tis.getNextEntry()) != null) {
				int count;
				byte data[] = new byte[2048];
				while((count = tis.read(data)) != -1) {
					byteStream.write(data, 0, count);
				}
			}

			byte data[] = byteStream.toByteArray();
			tis.close();
			byteStream.close();

			return data;
		}
		catch (IOException e) {
			e.printStackTrace();
		}

		return null;
	}
}
