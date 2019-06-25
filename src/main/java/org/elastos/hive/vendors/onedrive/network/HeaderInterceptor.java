package org.elastos.hive.vendors.onedrive.network;

import org.elastos.hive.AuthToken;
import org.elastos.hive.vendors.onedrive.Constance;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

public class HeaderInterceptor implements Interceptor{
    private final AuthToken authToken;
    public HeaderInterceptor(AuthToken authToken) {
        this.authToken = authToken;
    }

    @Override
    public Response intercept(Chain chain) throws IOException {

        Request request = chain.request();
        Request newRequest = request.newBuilder()
                .addHeader(Constance.AUTHORIZATION, "bearer "+authToken.getAccessToken())
//                .addHeader(Constance.CONTENT_TYPE,"application/x-www-form-urlencoded")
                .build();

        Response response = chain.proceed(newRequest);

        return response;
    }
}
