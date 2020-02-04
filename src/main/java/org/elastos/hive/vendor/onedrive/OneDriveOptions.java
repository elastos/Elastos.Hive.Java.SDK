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

package org.elastos.hive.vendor.onedrive;

import org.elastos.hive.Authenticator;
import org.elastos.hive.Client;
import org.elastos.hive.exception.HiveException;

public class OneDriveOptions extends Client.Options {
    private String clientId;
    private String redirectURL;

    private OneDriveOptions() {
    }

    private void setClientId(String clientId) {
        this.clientId = clientId;
    }

    String clientId() {
        return clientId;
    }

    private void setRedirectUrl(String redirectURL) {
        this.redirectURL = redirectURL;
    }

    String redirectUrl() {
        return redirectURL;
    }

    @Override
    protected Client buildClient() {
        return new OneDriveClient(this);
    }

    public static class Builder {
        private OneDriveOptions options;

        public Builder() {
            options = new OneDriveOptions();
        }

        public Builder setStorePath(String storePath) {
            options.setStorePath(storePath);
            return this;
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

        public Client.Options build() throws HiveException {
            if (options == null) {
                throw new HiveException("Builder should be deprecated");
            }
            if (options.clientId() == null ||
                    options.redirectUrl() == null ||
                    options.authenticator() == null) {
                throw new HiveException("Some options fields are invalid");
            }

            Client.Options opts = options;
            this.options = null;
            return opts;
        }
    }
}
