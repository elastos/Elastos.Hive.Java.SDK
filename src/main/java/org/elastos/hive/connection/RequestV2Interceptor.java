/*
 * Copyright (c) 2021 Elastos Foundation
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

import com.google.gson.Gson;
import okhttp3.Response;
import okhttp3.ResponseBody;
import org.elastos.hive.exception.HiveSdkException;
import org.elastos.hive.exception.NotFoundException;
import org.elastos.hive.exception.UnauthorizedException;
import org.elastos.hive.network.responseV2.ErrorResponseBody;

import java.io.IOException;

public class RequestV2Interceptor extends RequestInterceptor {
    RequestV2Interceptor(ConnectionManager connectionManager, boolean needToken) {
        super(connectionManager, needToken);
    }

    RequestV2Interceptor(ConnectionManager connectionManager) {
        super(connectionManager, true);
    }

    @Override
    protected void handleResponseErrorCode(Response response) throws IOException {
        int code = response.code();
        if (needToken && code == 401)
            tokenResolver.invalidateToken();

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
