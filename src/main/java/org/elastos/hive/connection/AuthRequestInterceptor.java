package org.elastos.hive.connection;

import java.io.IOException;

import org.elastos.hive.exception.HttpFailedException;
import org.elastos.hive.network.response.HiveResponseBody;

import okhttp3.Response;
import okhttp3.Interceptor;

class AuthRequestInterceptor implements Interceptor {
	public AuthRequestInterceptor() {}

	@Override
	public Response intercept(Chain chain) throws IOException {
        return handleResponse(chain.proceed(chain.request()));
    }

    private Response handleResponse(Response response) throws IOException {
        if (!response.isSuccessful())
            handleResponseErrorCode(response.code());
        return response;
    }

    /**
     * All error code comes from node service.
     */
    private void handleResponseErrorCode(int code) throws IOException {
        throw new HttpFailedException(code,
                HiveResponseBody.getHttpErrorMessages().getOrDefault(code, "Unknown error."));
    }
}
