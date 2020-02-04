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

package org.elastos.hive.vendor.connection;

import org.elastos.hive.vendor.connection.model.HeaderConfig;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

public class HeaderInterceptor implements Interceptor {
    private static final String AUTHORIZATION = "Authorization";
    private static final String CONTENT_TYPE = "Content-Type";
    private static final String ACCEPT_ENCODING = "Accept-Encoding";

    private final HeaderConfig headerConfig;

    HeaderInterceptor(HeaderConfig headerConfig) {
        this.headerConfig = headerConfig;
    }

    @Override
    public Response intercept(Chain chain) throws IOException {

        Request request = chain.request();

        //check & add Authorization header
        Request newRequest = addAuthHeader(request);

        //check & add CONTENT_TYPE header
        if (newRequest != null) {
            newRequest = addContentTypeHeader(newRequest);
        } else {
            newRequest = addContentTypeHeader(request);
        }

        //check & add ACCEPT_ENCODING header
        if (newRequest != null) {
            newRequest = addAcceptEncoding(newRequest);
        } else {
            newRequest = addAcceptEncoding(request);
        }

        if (newRequest != null) {
            return chain.proceed(newRequest);
        } else {
            return chain.proceed(request);
        }
    }

    private Request addAuthHeader(Request realRequest) {
        if (headerConfig == null || headerConfig.getAuthToken() == null) {
            return realRequest;
        }
        return realRequest.newBuilder()
                .addHeader(AUTHORIZATION, "bearer " + headerConfig.getAuthToken().getAccessToken())
                .build();
    }

    private Request addContentTypeHeader(Request realRequest) {
        if (headerConfig == null || headerConfig.getContentType() == null) {
            return realRequest;
        }
        return realRequest.newBuilder()
                .addHeader(CONTENT_TYPE, headerConfig.getContentType())
                .build();
    }

    private Request addAcceptEncoding(Request realRequest) {
        if (headerConfig == null || headerConfig.getAcceptEncoding() == null) {
            return realRequest;
        }
        return realRequest.newBuilder()
                .addHeader(ACCEPT_ENCODING, headerConfig.getAcceptEncoding())
                .build();
    }
}
