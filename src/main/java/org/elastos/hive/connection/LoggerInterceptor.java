/*
 * Copyright (c) 2019 Elastos Foundation
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package org.elastos.hive.connection;

import org.elastos.hive.utils.LogUtil;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.UnsupportedCharsetException;
import java.util.Objects;

import okhttp3.Interceptor;
import okhttp3.MediaType;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;
import okio.Buffer;
import okio.BufferedSource;

public class LoggerInterceptor implements Interceptor {
    @Override
    public Response intercept(Chain chain) throws IOException {

        Request request = chain.request();

        LogUtil.d("request url->" + request.url().toString());
        LogUtil.d("request headers->" + request.headers().toString());

        RequestBody requestBody = request.body();

        String rbString = null;

        if (requestBody != null) {
            Buffer buffer = new Buffer();
            requestBody.writeTo(buffer);

            Charset charset = Charset.defaultCharset();
            MediaType contentType = requestBody.contentType();
            if (contentType != null) {
                charset = contentType.charset(charset);
            }
            if (charset != null) {
                rbString = buffer.readString(charset);
            }
        }

        if (rbString!=null && !rbString.equals("")) {
            LogUtil.d("request body->" + rbString);
        }

        Response response = chain.proceed(request);

        LogUtil.d("response headers ->" + response.headers().toString());

        ResponseBody responseBody = response.body();
        String rbBody = null;

        if (responseBody != null) {
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
            if (charset != null) {
                rbBody = buffer.clone().readString(charset);
            }
        }

        LogUtil.d("response Code ->" + response.code());
        if (rbBody != null && !rbBody.equals(""))
            LogUtil.d("response body ->" + rbBody);

        if (Objects.requireNonNull(response.header("Content-Type")).equals("text/html; charset=utf-8")) {
            try {
                response.peekBody(0);
                response.newBuilder()
                        .headers(response.headers())
                        .message(response.message())
                        .body(ResponseBody.create(null, "")).build();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return response;
    }
}

