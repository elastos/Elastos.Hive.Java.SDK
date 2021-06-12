package org.elastos.hive.connection;

import java.io.IOException;
import org.elastos.hive.exception.NodeRPCException;

import okhttp3.Response;
import okhttp3.Interceptor;

class AuthRequestInterceptor implements Interceptor {
	public AuthRequestInterceptor() {}

	@Override
	public Response intercept(Chain chain) throws IOException {
		Response response = chain.proceed(chain.request());
		if (!response.isSuccessful()) {
			// TOOD:
			throw new NodeRPCException(response.code(), -1, response.message());
		}

		return response;
	}
}
