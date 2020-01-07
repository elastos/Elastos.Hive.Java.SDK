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

package org.elastos.hive.vendors.ipfs;

import org.elastos.hive.ConnectOptions;
import org.elastos.hive.ConnectType;

public class IPFSConnectOptions extends ConnectOptions {
    private IPFSRpcNode[] rpcNodes;

    private IPFSConnectOptions(){
        super(ConnectType.IPFS);
    }

    private void setRpcNodes(IPFSRpcNode[] nodes) {
        this.rpcNodes = nodes;
    }

    public IPFSRpcNode[] getRpcNodes() {
        return rpcNodes;
    }

    public static class Builder{
        IPFSConnectOptions options;

        public Builder() {
            options = new IPFSConnectOptions();
        }

        public Builder setRpcNodes(IPFSRpcNode[] nodes) {
            options.setRpcNodes(nodes);
            return this;
        }

        public IPFSConnectOptions build(){
            if (options.getRpcNodes().length == 0)
                return null;

            IPFSConnectOptions opts = options;
            this.options = null;

            return opts;
        }
    }
}
