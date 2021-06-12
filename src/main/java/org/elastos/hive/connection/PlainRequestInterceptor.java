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
    	Request request = chain.request()
    				.newBuilder()
        			.addHeader("Authorization", accessToken.getCanonicalizedAccessToken())
                    .build();

        Response response = chain.proceed(request);
        if (!response.isSuccessful()) {
        	int httpCode = response.code();
        	if (httpCode == 401)
        		accessToken.invalidateToken();

        	// TODO:
        	throw new NodeRPCException(httpCode, -1, response.message());
        }

        return response;
    }
}
