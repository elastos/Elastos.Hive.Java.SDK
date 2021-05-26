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

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import org.elastos.hive.ServiceEndpoint;
import org.elastos.hive.utils.LogUtil;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.TimeUnit;

public class ConnectionManager {
	private static final int DEFAULT_TIMEOUT = 30;

	private ServiceEndpoint serviceEndpoint;
	private Interceptor authRequestInterceptor;
	private PlainRequestInterceptor plainRequestInterceptor;
	private NormalRequestInterceptor normalRequestInterceptor;

	public ConnectionManager() {
		this.authRequestInterceptor  = new AuthRequestInterceptor();
	}

	public void attach(ServiceEndpoint serviceEndpoint) {
		this.serviceEndpoint = serviceEndpoint;
		this.plainRequestInterceptor = new PlainRequestInterceptor(this.serviceEndpoint);
		this.normalRequestInterceptor = new NormalRequestInterceptor(this.serviceEndpoint);
		this.normalRequestInterceptor.setTokenResolver(this.plainRequestInterceptor.getTokenResolver());
	}

	public ServiceEndpoint getServiceEndpoint() {
		return this.serviceEndpoint;
	}

	public HttpURLConnection openConnection(String path) throws IOException {
		return openConnectionWithUrl("/api/v1" + path, "POST");
	}

	public HttpURLConnection openConnectionWithUrl(String relativeUrl, String method) throws IOException {
		String url = serviceEndpoint.getProviderAddress() + relativeUrl;
		LogUtil.d("open connection with URL: " + url);
		HttpURLConnection httpURLConnection = (HttpURLConnection) new URL(url).openConnection();
		httpURLConnection.setRequestMethod(method);
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
		if (code == 200) {
			BufferedReader reader = new BufferedReader(new InputStreamReader(httpURLConnection.getInputStream()));
			StringBuilder result = new StringBuilder();
			String line = "";
			while ((line = reader.readLine()) != null)
				if (line.length() > 0)
					result.append(line.trim());
			LogUtil.d("connection", "response content: " + result.toString());
		} else {
			throw HiveResponseBody.getHttpExceptionByCode(code, HiveResponseBody.getHttpErrorMessages().get(code));
		}
	}

	/**
	 * Create network API service by service class.
	 * @param serviceClass the class of the service.
	 * @param requiredAuthorization	need authorization when requests.
	 * @param <S> the class of the service.
	 * @return the service instance.
	 */
	public <S> S createService(Class<S> serviceClass, boolean requiredAuthorization) {
		return createRetrofit(requiredAuthorization ? this.plainRequestInterceptor : this.authRequestInterceptor)
				.create(serviceClass);
	}

	public <S> S createService(Class<S> serviceClass) {
		return createRetrofit(normalRequestInterceptor).create(serviceClass);
	}

	private Retrofit createRetrofit(Interceptor requestInterceptor) {
		OkHttpClient.Builder builder;

		builder = new OkHttpClient.Builder()
				.connectTimeout(DEFAULT_TIMEOUT, TimeUnit.SECONDS)
				.readTimeout(DEFAULT_TIMEOUT, TimeUnit.SECONDS);

		builder.interceptors().clear();
		builder.interceptors().add(requestInterceptor);
		builder.interceptors().add(new LoggerInterceptor());

		return new Retrofit.Builder()
				.baseUrl(serviceEndpoint.getProviderAddress())
				// TODO: remove class StringConverterFactory and this line after v2 completes.
				.addConverterFactory(StringConverterFactory.create())
				.addConverterFactory(GsonConverterFactory.create())
				.client(builder.build())
				.build();
	}
}
