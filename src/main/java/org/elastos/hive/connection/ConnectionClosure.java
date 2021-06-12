package org.elastos.hive.connection;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.HttpURLConnection;

class ConnectionClosure {
	void confirmClosed(HttpURLConnection urlConnection) {
		try {
			if (urlConnection.getResponseCode() != 200)
				return;

			Reader aReader = new InputStreamReader(urlConnection.getInputStream());
			BufferedReader bufReader = new BufferedReader(aReader);
			StringBuilder result = new StringBuilder();
			String line = "";

			while ((line = bufReader.readLine()) != null) {
				line = line.trim();
				if (line.length() > 0)
					result.append(line);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
