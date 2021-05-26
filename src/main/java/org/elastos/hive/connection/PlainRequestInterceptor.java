package org.elastos.hive.connection;

import java.io.IOException;

import org.elastos.hive.auth.AccessToken;
import org.elastos.hive.exception.HttpFailedException;

import okhttp3.Request;
import okhttp3.Response;
import okhttp3.Interceptor;

class PlainRequestInterceptor implements Interceptor {
	private AccessToken accessToken;

    PlainRequestInterceptor(AccessToken accessToken) {
    	this.accessToken = accessToken;
    }

    @Override
    public Response intercept(Chain chain) throws IOException {
        Request request = chain.request();
        request = request.newBuilder()
        			.addHeader("Authorization", accessToken.getCanonicalizedAccessToken())
                    .build();
        return handleResponse(chain.proceed(request));
    }

    private Response handleResponse(Response response) throws IOException {
        if (!response.isSuccessful())
            handleResponseErrorCode(response.code());
        return response;
    }

    private void handleResponseErrorCode(int code) throws IOException {
        if (code == 401)
        	accessToken.invalidateToken();

        throw new HttpFailedException(code,
                HiveResponseBody.getHttpErrorMessages().getOrDefault(code, "Unknown error."));
    }
}
