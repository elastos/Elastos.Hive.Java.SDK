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

import org.elastos.hive.AppContext;
import org.elastos.hive.connection.model.AuthToken;
import org.elastos.hive.auth.LocalResolver;
import org.elastos.hive.auth.RemoteResolver;
import org.elastos.hive.auth.TokenResolver;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;
import org.elastos.hive.exception.HiveException;
import org.elastos.hive.network.BaseApi;

/**
 * Set token to http request.
 */
public class RequestInterceptor implements Interceptor {
    private AuthToken token;
    private TokenResolver tokenResolver;

    RequestInterceptor(AppContext context, ConnectionManager connectionManager) {
        this.tokenResolver = new LocalResolver(context.getProviderAddress());
        this.tokenResolver.setNextResolver(new RemoteResolver(context, connectionManager));
    }

    @Override
    public Response intercept(Chain chain) throws IOException {
        checkToken();
        Request request = chain.request().newBuilder()
                .addHeader(BaseApi.HTTP_AUTHORIZATION, this.token.getHeaderTokenValue())
                .build();
        return handleResponse(chain.proceed(request));
    }

    /**
     * Handle response for common error checking.
     */
    private Response handleResponse(Response response) throws IOException {
        if (!response.isSuccessful()) {
            if (response.code() == 401) {
                //Remove token for next time refresh.
                this.token = null;
                throw new IOException("Failed to request for code " + response.code() + "(auth failed)");
            }
            throw new IOException("Failed to request for code " + response.code());
        }
        return response;
    }

    private void checkToken() throws IOException {
        if (token == null || token.isExpired()) {
            try {
                token = tokenResolver.getToken();
            } catch (HiveException e) {
                throw new IOException(e.getMessage());
            }
        }
    }

    public AuthToken getAuthToken() {
        return token;
    }
}
