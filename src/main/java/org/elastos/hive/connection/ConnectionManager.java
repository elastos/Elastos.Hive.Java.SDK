/*
 * Copyright (c) 2019 Elastos Foundation
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package org.elastos.hive.connection;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

import org.elastos.hive.Constance;
import org.elastos.hive.connection.model.BaseServiceConfig;
import org.elastos.hive.network.AuthApi;
import org.elastos.hive.network.DatabaseApi;
import org.elastos.hive.network.FileApi;
import org.elastos.hive.network.NodeApi;
import org.elastos.hive.network.ScriptingApi;
import org.elastos.hive.network.VersionApi;

public class ConnectionManager {

	private AuthApi authApi;
	private FileApi fileApi;
	private DatabaseApi databaseApi;
	private VersionApi versionApi;
	private ScriptingApi scriptingApi;

	private NodeApi vaultAPI;
	private String vaultBaseUrl;
	private BaseServiceConfig vaultConfig = new BaseServiceConfig.Builder().build() ;

	public ConnectionManager(String baseUrl, BaseServiceConfig baseServiceConfig) {
		resetVaultApi(baseUrl, baseServiceConfig);
	}

	public AuthApi getAuthApi() {
		if (authApi == null)
			authApi = BaseServiceUtil.createService(AuthApi.class,
					this.vaultBaseUrl, this.vaultConfig);
		return authApi;
	}

	public FileApi getFileApi() {
		if (fileApi == null)
			fileApi = BaseServiceUtil.createService(FileApi.class,
					this.vaultBaseUrl, this.vaultConfig);
		return fileApi;
	}

	public DatabaseApi getDatabaseApi() {
		if (databaseApi == null)
			databaseApi = BaseServiceUtil.createService(DatabaseApi.class,
					this.vaultBaseUrl, this.vaultConfig);
		return databaseApi;
	}

	public ScriptingApi getScriptingApi() {
		if (scriptingApi == null)
			scriptingApi = BaseServiceUtil.createService(ScriptingApi.class,
					this.vaultBaseUrl, this.vaultConfig);
		return scriptingApi;
	}

	public VersionApi getVersionApi() {
		if (versionApi == null)
			versionApi = BaseServiceUtil.createService(VersionApi.class,
					this.vaultBaseUrl, this.vaultConfig);
		return versionApi;
	}

	public NodeApi getVaultApi() {
		if (vaultAPI == null)
			vaultAPI = BaseServiceUtil.createService(NodeApi.class,
					this.vaultBaseUrl, this.vaultConfig);
		return vaultAPI;
	}

	private void updateVaultConfig(BaseServiceConfig vaultConfig) {
		this.vaultConfig = vaultConfig;
	}

	private void updateVaultBaseUrl(String vaultBaseUrl) {
		this.vaultBaseUrl = vaultBaseUrl;
	}

	public void resetVaultApi(String baseUrl, BaseServiceConfig baseServiceConfig) {
		authApi = null;
		fileApi = null;
		vaultAPI = null;
		databaseApi = null;
		versionApi = null;
		scriptingApi = null;
		updateVaultBaseUrl(baseUrl);
		updateVaultConfig(baseServiceConfig);
	}

	public String getVaultBaseUrl() {
		return this.vaultBaseUrl;
	}

	public String getAccessToken() {
		return this.vaultConfig.getHeaderConfig().getAuthToken().getAccessToken();
	}

	public HttpURLConnection openURLConnection(String path) throws IOException {
		String url = this.getVaultBaseUrl() + Constance.API_PATH +"/files/upload/" + path;
		HttpURLConnection httpURLConnection = (HttpURLConnection) new URL(url).openConnection();
		httpURLConnection.setRequestMethod("POST");
		httpURLConnection.setRequestProperty("User-Agent",
				"Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.11 (KHTML, like Gecko) Chrome/23.0.1271.95 Safari/537.11");
		httpURLConnection.setConnectTimeout(5000);
		httpURLConnection.setReadTimeout(5000);

		httpURLConnection.setDoOutput(true);
		httpURLConnection.setDoInput(true);
		httpURLConnection.setUseCaches(false);
		httpURLConnection.setRequestProperty("Transfer-Encoding", "chunked");
		httpURLConnection.setRequestProperty("Connection", "Keep-Alive");
		httpURLConnection.setRequestProperty("Authorization", "token " + this.getAccessToken());

		httpURLConnection.setChunkedStreamingMode(0);

		return httpURLConnection;
	}

}
