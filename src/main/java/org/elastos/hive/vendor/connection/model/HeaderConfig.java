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

package org.elastos.hive.vendor.connection.model;

import org.elastos.hive.AuthToken;

public class HeaderConfig {
    private final AuthToken authToken;
    private final String contentType;
    private final String acceptEncoding;

    private HeaderConfig() {
        this.authToken = null;
        this.contentType = null;
        this.acceptEncoding = null;
    }

    private HeaderConfig(Builder builder) {
        this.authToken = builder.authToken;
        this.contentType = builder.contentType;
        this.acceptEncoding = builder.acceptEncoding;
    }

    public AuthToken getAuthToken() {
        return authToken;
    }

    public String getContentType() {
        return contentType;
    }

    public String getAcceptEncoding() {
        return acceptEncoding;
    }

    public static final class Builder {
        AuthToken authToken;
        String contentType;
        String acceptEncoding;

        public Builder() {
            this.authToken = null;
            this.contentType = null;
            this.acceptEncoding = null;
        }

        public Builder authToken(AuthToken authToken) {
            this.authToken = authToken;
            return this;
        }

        public Builder contentType(String contentType) {
            this.contentType = contentType;
            return this;
        }

        public Builder acceptEncoding(String acceptEncoding) {
            this.acceptEncoding = acceptEncoding;
            return this;
        }

        public HeaderConfig build() {
            return new HeaderConfig(this);
        }
    }
}
