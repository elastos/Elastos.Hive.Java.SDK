package org.elastos.hive.connection;

import java.io.IOException;

import org.elastos.hive.ServiceEndpoint;
import org.elastos.hive.auth.AuthToken;
import org.elastos.hive.auth.LocalResolver;
import org.elastos.hive.auth.RemoteResolver;
import org.elastos.hive.auth.TokenResolver;
import org.elastos.hive.exception.HttpFailedException;
import org.elastos.hive.network.response.HiveResponseBody;

import okhttp3.Request;
import okhttp3.Response;
import okhttp3.Interceptor;

class PlainRequestInterceptor implements Interceptor {
	private TokenResolver tokenResolver;

    PlainRequestInterceptor(ServiceEndpoint endpoint) {
        this.tokenResolver = new LocalResolver(endpoint);
        this.tokenResolver.setNextResolver(new RemoteResolver(endpoint));
    }

    @Override
    public Response intercept(Chain chain) throws IOException {
        Request request = chain.request();
        request = request.newBuilder()
        			.addHeader("Authorization", getAuthToken().getCanonicalizedAccessToken())
                    .build();
        return handleResponse(chain.proceed(request));
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
        if (code == 401)
            tokenResolver.invalidateToken();

        throw new HttpFailedException(code,
                HiveResponseBody.getHttpErrorMessages().getOrDefault(code, "Unknown error."));
    }

    public AuthToken getAuthToken() throws HttpFailedException {
        return tokenResolver.getToken();
    }
}
