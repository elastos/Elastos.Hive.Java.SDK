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

import okhttp3.OkHttpClient;
import org.elastos.hive.AppContext;
import org.elastos.hive.network.*;
import org.elastos.hive.utils.LogUtil;
import org.jetbrains.annotations.NotNull;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import org.elastos.hive.network.PaymentApi;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.TimeUnit;

public class ConnectionManager {
	private static final int DEFAULT_TIMEOUT = 30;

	private AppContext context;
	private RequestInterceptor authRequestInterceptor;
	private RequestInterceptor plainRequestInterceptor;

	private SubscriptionApi subscriptionApi;
	private PaymentApi paymentApi;
	private DatabaseApi databaseApi;

	private AuthApi authApi;
	private FilesApi filesApi;
	private ScriptingApi scriptingApi;
	private BackupApi backupApi;
	private NodeManageApi nodeManageApi;

	public ConnectionManager(AppContext context) {
		this.context = context;
		this.plainRequestInterceptor = new RequestInterceptor(context, this);
		this.authRequestInterceptor  = new RequestInterceptor(context, this, false);
	}

	public AuthApi getAuthApi() {
		if (authApi == null)
			authApi = createService(AuthApi.class, this.context.getProviderAddress(), this.authRequestInterceptor);

		return authApi;
	}

	public NodeManageApi getNodeManagerApi() {
		if (nodeManageApi == null)
			nodeManageApi = createService(NodeManageApi.class, this.context.getProviderAddress(), this.authRequestInterceptor);

		return nodeManageApi;
	}

	public FilesApi getFilesApi() {
		if (filesApi == null)
			filesApi = createService(FilesApi.class, this.context.getProviderAddress(), this.plainRequestInterceptor);

		return filesApi;
	}

	public SubscriptionApi getSubscriptionApi() {
		if (subscriptionApi == null) {
			subscriptionApi = createService(SubscriptionApi.class, this.context.getProviderAddress(), this.plainRequestInterceptor);
		}
		return subscriptionApi;
	}

	public PaymentApi getPaymentApi() {
		if (paymentApi == null) {
			paymentApi = createService(PaymentApi.class, this.context.getProviderAddress(), this.plainRequestInterceptor);
		}
		return paymentApi;
	}

	public DatabaseApi getDatabaseApi() {
		if (databaseApi == null) {
			databaseApi = createService(DatabaseApi.class, this.context.getProviderAddress(), this.plainRequestInterceptor);
		}
		return databaseApi;
	}

	public ScriptingApi getScriptingApi() {
		if (scriptingApi == null) {
			scriptingApi = createService(ScriptingApi.class, this.context.getProviderAddress(), this.plainRequestInterceptor);
		}
		return scriptingApi;
	}

	public BackupApi getBackupApi() {
		if (backupApi == null) {
			backupApi = createService(BackupApi.class, this.context.getProviderAddress(), this.plainRequestInterceptor);
		}
		return backupApi;
	}

	public HttpURLConnection openConnection(String path) throws IOException {
		String url = this.context.getProviderAddress() + BaseApi.API_VERSION + path;
		LogUtil.d("open connection with URL: " + url);
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
		httpURLConnection.setRequestProperty("Authorization", this.plainRequestInterceptor.getAuthToken().getCanonicalizedAccessToken());

		httpURLConnection.setChunkedStreamingMode(0);
		return httpURLConnection;
	}

	public static void readConnection(HttpURLConnection httpURLConnection) throws IOException {
		int code = httpURLConnection.getResponseCode();
		StringBuilder result = new StringBuilder();
		if (code == 200) {
			InputStream is = httpURLConnection.getInputStream();
			BufferedReader reader = new BufferedReader(new InputStreamReader(is));
			String sCurrentLine = "";
			while ((sCurrentLine = reader.readLine()) != null)
				if (sCurrentLine.length() > 0)
					result.append(sCurrentLine.trim());
			LogUtil.d("connection", "response content: " + result);
		} else {
			result = new StringBuilder("error code:" + code);
			result.append("error code:").append(code).append(";");
			result.append("message:").append(httpURLConnection.getResponseMessage()).append(";");
			throw new IOException("Failed to read connection: " + result);
		}
	}

	private static <S> S createService(Class<S> serviceClass, String baseUrl, RequestInterceptor requestInterceptor) {
		OkHttpClient.Builder clientBuilder;
		Retrofit.Builder retrofitBuilder;

		clientBuilder = new OkHttpClient.Builder()
				.connectTimeout(DEFAULT_TIMEOUT, TimeUnit.SECONDS)
				.readTimeout(DEFAULT_TIMEOUT, TimeUnit.SECONDS);

		clientBuilder.interceptors().clear();
		clientBuilder.interceptors().add(requestInterceptor);
		clientBuilder.interceptors().add(new LoggerInterceptor());

		retrofitBuilder = new Retrofit.Builder()
				.baseUrl(baseUrl)
				.addConverterFactory(StringConverterFactory.create())
				.addConverterFactory(GsonConverterFactory.create());

		return retrofitBuilder.client(clientBuilder.build()).build().create(serviceClass);
	}
}
