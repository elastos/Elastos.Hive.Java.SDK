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

package org.elastos.hive;

import org.elastos.hive.exception.HiveException;
import org.elastos.hive.exception.NotImplementedException;
import org.elastos.hive.exception.UnsupportedException;
import org.elastos.hive.interfaces.Files;
import org.elastos.hive.interfaces.IPFS;
import org.elastos.hive.interfaces.KeyValues;

public abstract class Client {
    public abstract void connect() throws HiveException;

    public abstract void disconnect();

    public abstract boolean isConnected();

    public abstract Files getFiles() throws UnsupportedException;

    public abstract IPFS getIPFS() throws UnsupportedException;

    public abstract KeyValues getKeyValues() throws UnsupportedException;

    public static abstract class Options {
        private String storePath;
        private Authenticator authenticator;

        protected Options() {
        }

        protected void setStorePath(String storePath) {
            this.storePath = storePath;
        }

        public String storePath() {
            return storePath;
        }

        protected void setAuthenticator(Authenticator authenticator) {
            this.authenticator = authenticator;
        }

        public Authenticator authenticator() {
            return authenticator;
        }

        protected boolean checkValid(boolean all) {
            return (storePath != null) && (!all || authenticator != null);
        }

        protected abstract Client buildClient();
    }

    public static Client createInstance(Options options) throws HiveException {
        if (options == null) {
            throw new IllegalArgumentException();
        }

        Client client = options.buildClient();
        if (client == null) {
            throw new NotImplementedException();
        }

        return client;
    }
}
