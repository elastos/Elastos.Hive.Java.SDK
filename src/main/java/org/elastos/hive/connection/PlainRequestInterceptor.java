package org.elastos.hive.connection;

import java.io.IOException;

import org.elastos.hive.auth.AccessToken;
import org.elastos.hive.exception.NodeRPCException;

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

    private Response handleResponse(Response response) throws NodeRPCException {
        if (!response.isSuccessful())
            handleResponseErrorCode(response);
        return response;
    }

    private void handleResponseErrorCode(Response response) throws NodeRPCException {
    	if (response.code() == 401)
        	accessToken.invalidateToken();

    	// TODO: need to change to error format.
        throw new NodeRPCException(response.code(), -1, response.message());
    }
}
