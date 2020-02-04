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

package org.elastos.hive.vendor.ipfs;

import org.elastos.hive.exception.HiveException;
import org.elastos.hive.vendor.connection.ConnectionManager;

import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicBoolean;

import retrofit2.Response;

class IPFSRpc {
    private static final String URLFORMAT = "http://%s:%d/api/v0/";

    private AtomicBoolean connectState = new AtomicBoolean(false);
    private ArrayList<IPFSOptions.RpcNode> mHiveRpcNodes;
    private IPFSOptions.RpcNode mHiveRpcNode;

    IPFSRpc(ArrayList<IPFSOptions.RpcNode> hiveRpcNodes) {
        this.mHiveRpcNodes = hiveRpcNodes;
    }

    boolean getConnectState() {
        return connectState.get();
    }

    void dissConnect() {
        connectState.set(false);
    }

    void checkReachable() throws HiveException {
        if (mHiveRpcNodes == null || mHiveRpcNodes.size() == 0) {
            throw new HiveException(HiveException.RPC_NODE_NULL);
        }

        for (IPFSOptions.RpcNode hiveRpcNode : mHiveRpcNodes) {
            if (checkConnect(hiveRpcNode)) {
                return;
            }
        }

        if (mHiveRpcNode == null) {
            throw new HiveException(HiveException.NO_RPC_NODE_AVAILABLE);
        }
    }

    private void selectBootstrap(IPFSOptions.RpcNode hiveRpcNode) {
        mHiveRpcNode = hiveRpcNode;
        String baseUrl = String.format(URLFORMAT, mHiveRpcNode.getIpv4(), mHiveRpcNode.getPort());
        try {
            ConnectionManager.resetIPFSApi(baseUrl);
            connectState.set(true);
        } catch (Exception e) {
            e.printStackTrace();
            connectState.set(false);
        }
    }

    private boolean checkConnect(IPFSOptions.RpcNode hiveRpcNode) throws HiveException {
        if (hiveRpcNode == null)
            throw new HiveException(HiveException.RPC_NODE_NULL);

        boolean isSelect = false;
        String ipv4 = hiveRpcNode.getIpv4();
        String ipv6 = hiveRpcNode.getIpv6();
        int port = hiveRpcNode.getPort();
        if (port == 0) {
            throw new HiveException(HiveException.RPC_NODE_PORT_NULL);
        }

        if (checkConnect(ipv4, port) != null) {
            selectBootstrap(hiveRpcNode);
            isSelect = true;
        }

        if (checkConnect(ipv6, port) != null) {
            selectBootstrap(hiveRpcNode);
            isSelect = true;
        }

        return isSelect;
    }

    private Response checkConnect(String ip, int port) {

        if (ip != null && !ip.equals("")) {
            Response response = null;
            try {
                response = ConnectionManager.getIPFSApi().version(ip, port).execute();
            } catch (Exception e) {
                e.printStackTrace();
            }
            if (response == null) return null;
            if (response.code() == 200) return response;
        }
        return null;
    }
}
