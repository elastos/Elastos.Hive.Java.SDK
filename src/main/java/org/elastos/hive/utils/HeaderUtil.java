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

package org.elastos.hive.utils;

import retrofit2.Response;

public class HeaderUtil {
    private static final String CONTENT_LENGTH = "Content-Length";
    private static final String TRANSFER_ENCODING = "Transfer-Encoding";

    private static final String CHUNKED = "chunked";
    private static final int DEFAULT_CONTENT_LENGTH = -1 ;

    public static int getContentLength(Response response){
        if (isResponseHeaderEmpty(response , CONTENT_LENGTH)){
            return DEFAULT_CONTENT_LENGTH ;
        }

        String contentLengthStr = response.headers().get(CONTENT_LENGTH) ;
        int contentLength = Integer.valueOf(contentLengthStr);
        if (contentLength < 0){
            return DEFAULT_CONTENT_LENGTH ;
        }else{
            return contentLength;
        }
    }

    public static boolean isTrunced(Response response){
        if (isResponseHeaderEmpty(response , TRANSFER_ENCODING)){
            return false ;
        }

        String transferEncoding = response.headers().get(TRANSFER_ENCODING) ;
        if (transferEncoding.equals(CHUNKED)){
            return true ;
        }

        return false ;
    }

    private static boolean isResponseHeaderEmpty(Response response , String headerKey){
        if (response == null){
            return true;
        }

        String headerValue = response.headers().get(headerKey) ;
        if (headerValue == null || headerValue.equals("null")){
            return true;
        }
        return false ;
    }
}
