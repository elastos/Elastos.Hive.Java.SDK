package org.elastos.hive.connection;

import java.io.IOException;

import org.elastos.hive.exception.HttpFailedException;
import org.elastos.hive.exception.NodeRPCException;

import okhttp3.Response;
import okhttp3.Interceptor;

class AuthRequestInterceptor implements Interceptor {
	public AuthRequestInterceptor() {}

	@Override
	public Response intercept(Chain chain) throws IOException {
        return handleResponse(chain.proceed(chain.request()));
    }

    private Response handleResponse(Response response) throws NodeRPCException {
        if (!response.isSuccessful())
            handleResponseErrorCode(response);
        return response;
    }

    private void handleResponseErrorCode(Response response) throws NodeRPCException {
    	// TODO: need to change to error format.
        throw new NodeRPCException(response.code(), -1, response.message());
    }
}
