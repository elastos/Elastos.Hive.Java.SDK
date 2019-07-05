package org.elastos.hive.vendors.connection;

import org.elastos.hive.vendors.connection.Model.HeaderConfig;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

public class HeaderInterceptor implements Interceptor{
    private static final String AUTHORIZATION = "Authorization";
    private static final String CONTENT_TYPE = "Content-Type";
    private static final String ACCEPT_ENCODING = "Accept-Encoding";

    private final HeaderConfig headerConfig ;
    public HeaderInterceptor(HeaderConfig headerConfig) {
        this.headerConfig = headerConfig ;
    }

    @Override
    public Response intercept(Chain chain) throws IOException {

        Request request = chain.request();

        //check & add Authorization header
        Request newRequest = addAuthHeader(request);

        //check & add CONTENT_TYPE header
        if (newRequest!=null){
            newRequest = addContentTypeHeader(newRequest);
        }else{
            newRequest = addContentTypeHeader(request);
        }

        //check & add ACCEPT_ENCODING header
        if (newRequest!=null){
            newRequest = addAcceptEncoding(newRequest);
        }else {
            newRequest = addAcceptEncoding(request);
        }

        if (newRequest!=null){
            Response response = chain.proceed(newRequest);
            return response ;
        }else{
            Response response = chain.proceed(request);
            return response;
        }
    }

    private Request addAuthHeader(Request realRequest){
        if (headerConfig == null || headerConfig.getAuthToken() == null){
            return realRequest ;
        }
        Request newRequest = realRequest.newBuilder()
                .addHeader(AUTHORIZATION, "bearer "+headerConfig.getAuthToken().getAccessToken())
                .build();
        return newRequest ;

    }

    private Request addContentTypeHeader(Request realRequest){
        if (headerConfig == null|| headerConfig.getContentType() == null){
            return realRequest ;
        }
        Request newRequest = realRequest.newBuilder()
                    .addHeader(CONTENT_TYPE , headerConfig.getContentType())
                    .build();
        return newRequest ;
    }

    private Request addAcceptEncoding(Request realRequest){
        if (headerConfig == null|| headerConfig.getAcceptEncoding() == null){
            return realRequest ;
        }
        Request newRequest = realRequest.newBuilder()
                .addHeader(ACCEPT_ENCODING , headerConfig.getAcceptEncoding())
                .build();
        return newRequest ;
    }


}
