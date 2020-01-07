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
import org.elastos.hive.ConnectType;

public class OneDriveConnectOptions extends ConnectOptions {
    private static String scope = "Files.ReadWrite.AppFolder offline_access";
    private String clientId;
    private String redirectUrl;

    private OneDriveConnectOptions() {
        super(ConnectType.OneDrive);
    }

    private void setClientId(String clientId) {
        this.clientId = clientId;
    }

    private void setRedirectUrl(String redirectUrl) {
        this.redirectUrl = redirectUrl;
    }

    public String getClientId() {
        return clientId;
    }

    public String getScope() {
        return scope;
    }

    public String getRedirectUrl() {
        return  redirectUrl;
    }

    public static class Builder {
        private OneDriveConnectOptions options;

        public Builder() {
            options = new OneDriveConnectOptions();
        }

        public Builder setClientId(String clientId) {
            options.setClientId(clientId);
            return this;
        }

        public Builder setRedirectUrl(String redirectUrl) {
            options.setRedirectUrl(redirectUrl);
            return this;
        }

        public Builder setAuthenticator(Authenticator authenticator) {
            options.setAuthenticator(authenticator);
            return this;
        }

        public OneDriveConnectOptions build() {
            if (options.getClientId() == null ||
                    options.getRedirectUrl() == null ||
                    options.getAuthenticator() == null)
                return null;

            OneDriveConnectOptions opts = options;
            this.options = null;

            return opts;
        }
    }
}
