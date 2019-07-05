package org.elastos.hive.vendors.connection;


import org.elastos.hive.utils.CheckTextUtil;
import org.elastos.hive.utils.LogUtil;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.UnsupportedCharsetException;

import okhttp3.Interceptor;
import okhttp3.MediaType;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;
import okio.Buffer;
import okio.BufferedSource;

public class NetworkLogInterceptor implements Interceptor{
    @Override
    public Response intercept(Chain chain) throws IOException {

        Request request = chain.request();

        LogUtil.d("request url->"+request.url().toString());
        LogUtil.d("request headers->"+request.headers().toString());

        RequestBody requestBody = request.body();

        String rbString = null;

        if(requestBody != null) {
            Buffer buffer = new Buffer();
            requestBody.writeTo(buffer);

            Charset charset = Charset.defaultCharset();
            MediaType contentType = requestBody.contentType();
            if (contentType != null) {
                charset = contentType.charset(charset);
            }
            rbString = buffer.readString(charset);
        }

        if (!CheckTextUtil.isEmpty(rbString)){
            LogUtil.d("request body->"+rbString);
        }

        Response response = chain.proceed(request);

        LogUtil.d("response headers ->"+response.headers().toString());

        ResponseBody responseBody = response.body();
        String rbBody = null;

        if (responseBody!=null) {
            BufferedSource source = responseBody.source();
            source.request(Long.MAX_VALUE); // Buffer the entire body.
            Buffer buffer = source.buffer();

            Charset charset = Charset.defaultCharset();

            MediaType contentType = responseBody.contentType();
            if (contentType != null) {
                try {
                    charset = contentType.charset(charset);
                } catch (UnsupportedCharsetException e) {
                    e.printStackTrace();
                }
            }
            rbBody = buffer.clone().readString(charset);
        }

        if (!CheckTextUtil.isEmpty(rbBody)){
            LogUtil.d("response body ->"+rbBody);
        }

        if (response.header("Content-Type").equals("text/html; charset=utf-8")){
            try {
                response.peekBody(0);
                response.newBuilder()
                        .headers(response.headers())
                        .message(response.message())
                        .body(ResponseBody.create(null,"")).build();
            }catch (Exception e){
            }
        }
        return response;
    }
}

