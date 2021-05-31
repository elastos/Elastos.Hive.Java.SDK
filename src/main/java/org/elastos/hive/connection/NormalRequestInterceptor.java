package org.elastos.hive.connection;

import com.google.gson.Gson;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;
import org.elastos.hive.auth.AccessToken;
import org.elastos.hive.exception.*;

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
            throw new IOException("Failed to get body on validateBody");

        try {
            ErrorResponseBody error = new Gson().fromJson(body.string(), ErrorResponseBody.class);
            throw new HiveHttpException(code, error.getError().getCode(), error.getError().getMessage());
        } catch (Exception e) {
            throw new IOException(e.getMessage());
        }
    }
}
