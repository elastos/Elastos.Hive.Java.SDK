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

package org.elastos.hive.vendors.onedrive;

import org.elastos.hive.Authenticator;
import org.elastos.hive.ConnectOptions;

public class OneDriveConnectOptions extends ConnectOptions {
    private final String clientId;
    private final String scope;
    private final String redirectUrl;
    private final Authenticator authenticator;

    OneDriveConnectOptions() {
        this(new Builder());
    }

    OneDriveConnectOptions(Builder builder) {
        this.clientId = builder.clientId;
        this.scope = builder.scope;
        this.redirectUrl = builder.redirectUrl;
        this.authenticator = builder.authenticator;
        setBackendType(HiveBackendType.HiveBackendType_OneDrive);
        setAuthenticator(authenticator);
    }

    String getClientId() {
        return clientId;
    }

    String getScope() {
        return scope;
    }

    String getRedirectUrl() {
        return redirectUrl;
    }

    public static class Builder{
        private String clientId;
        private String scope;
        private String redirectUrl;
        private Authenticator authenticator;

        public Builder(){
            clientId = "afd3d647-a8b7-4723-bf9d-1b832f43b881";
            scope = "User.Read Files.ReadWrite.All offline_access";
            redirectUrl = "http://localhost:12345" ;
            authenticator = requestUrl -> {};
        }

        Builder(OneDriveConnectOptions oneDriveConnectOptions){
            this.clientId = oneDriveConnectOptions.clientId;
            this.scope = oneDriveConnectOptions.scope;
            this.redirectUrl = oneDriveConnectOptions.redirectUrl;
            this.authenticator = oneDriveConnectOptions.authenticator;
        }

        public Builder clientId(String clientId){
            this.clientId = clientId;
            return this ;
        }

        public Builder scope(String scope){
            this.scope = scope ;
            return this ;
        }

        public Builder redirectUrl(String redirectUrl){
            this.redirectUrl = redirectUrl ;
            return this ;
        }

        public Builder authenticator(Authenticator authenticator){
            this.authenticator = authenticator ;
            return this ;
        }

        public OneDriveConnectOptions build(){
            return new OneDriveConnectOptions(this);
        }
    }
}
