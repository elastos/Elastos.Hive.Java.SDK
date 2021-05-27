package org.elastos.hive.connection;

import com.google.gson.Gson;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;
import org.elastos.hive.ServiceEndpoint;
import org.elastos.hive.auth.AccessToken;
import org.elastos.hive.auth.AuthToken;
import org.elastos.hive.auth.AccessTokenLocalResolver;
import org.elastos.hive.auth.AccessTokenRemoteResolver;
import org.elastos.hive.auth.CodeResolver;
import org.elastos.hive.exception.HiveSdkException;
import org.elastos.hive.exception.HttpFailedException;
import org.elastos.hive.exception.NotFoundException;
import org.elastos.hive.exception.UnauthorizedException;

import java.io.IOException;

class NormalRequestInterceptor implements Interceptor {
	private AccessToken accessToken;

	NormalRequestInterceptor(AccessToken accessToken) {
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
            handleResponseErrorCode(response);
        return response;
    }

    /**
     * All error code comes from node service.
     */
    private void handleResponseErrorCode(Response response) throws IOException {
        int code = response.code();
        if (code == 401)
            accessToken.invalidateToken();

        ResponseBody body = response.body();
        if (body == null)
            throw new HiveSdkException("Failed to get body on validateBody");

        try {
            ErrorResponseBody error = new Gson().fromJson(body.string(), ErrorResponseBody.class);
            if (code == 401)
                throw new UnauthorizedException(error.getError().getMessage());
            else if (code == 404)
                throw new NotFoundException(error.getError().getMessage());
        } catch (IOException e) {
            throw new HiveSdkException(e.getMessage());
        }
    }
}
