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

import org.elastos.hive.ConnectHelper;
import org.elastos.hive.Authenticator;
import org.elastos.hive.Callback;
import org.elastos.hive.NullCallback;
import org.elastos.hive.exception.HiveException;
import org.elastos.hive.vendor.connection.ConnectionManager;

import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.concurrent.atomic.AtomicBoolean;

import retrofit2.Response;

class IPFSRpc implements ConnectHelper {
    private static final String URLFORMAT = "http://%s:%d/api/v0/";

    private AtomicBoolean connectState = new AtomicBoolean(false);
    private ArrayList<IPFSOptions.RpcNode> mHiveRpcNodes;

    IPFSRpc(ArrayList<IPFSOptions.RpcNode> hiveRpcNodes) {
        this.mHiveRpcNodes = hiveRpcNodes;
    }

    boolean getConnectState() {
        return connectState.get();
    }

    void dissConnect() {
        connectState.set(false);
    }

    private boolean doCheckValid() throws HiveException {
        if (!connectState.get())
            return checkReachable();
        return true;
    }

    private boolean checkReachable() throws HiveException {
        if (mHiveRpcNodes == null || mHiveRpcNodes.size() == 0)
            throw new IllegalArgumentException();

        for (IPFSOptions.RpcNode hiveRpcNode : mHiveRpcNodes) {
            if (checkConnect(hiveRpcNode)) {
                connectState.set(true);
                return true;
            }
        }
        connectState.set(false);
        return false;
    }

    private void selectBootstrap(IPFSOptions.RpcNode hiveRpcNode) {
        String baseUrl = String.format(URLFORMAT, hiveRpcNode.getIpv4(), hiveRpcNode.getPort());
        try {
            ConnectionManager.resetIPFSApi(baseUrl);
            connectState.set(true);
        } catch (Exception e) {
            connectState.set(false);
        }
    }

    private boolean checkConnect(IPFSOptions.RpcNode hiveRpcNode) throws HiveException {
        if (hiveRpcNode == null)
            throw new IllegalArgumentException();

        int port = hiveRpcNode.getPort();
        if (port <= 0)
            throw new IllegalArgumentException();

        String ipv4 = hiveRpcNode.getIpv4();
        String ipv6 = hiveRpcNode.getIpv6();
        boolean isSelect = false;
        selectBootstrap(hiveRpcNode);
        if (checkConnect(ipv4, port) != null) {
            isSelect = true;
        }
        if (checkConnect(ipv6, port) != null && !isSelect) {
            isSelect = true;
        }

        return isSelect;
    }

    private Response checkConnect(String ip, int port) {
        if (null == ip || ip.isEmpty())
            return null;

        Response response = null;

        try {
            response = ConnectionManager.getIPFSApi().version(ip, port).execute();
        } catch (IOException e) {
            connectState.set(false);
        }

        if (null == response || response.code() != 200)
            return null;

        return response;
    }

    @Override
    public CompletableFuture<Void> connectAsync(Authenticator authenticator) {
        return connectAsync(authenticator, new NullCallback<>());
    }

    @Override
    public CompletableFuture<Void> connectAsync(Authenticator authenticator,
                                                Callback<Void> callback) {
        return CompletableFuture.runAsync(() -> {
            try {
                checkReachable();
                callback.onSuccess(null);
            } catch (Exception e) {
                callback.onError(new HiveException(e.getLocalizedMessage()));
            }
        });
    }

    @Override
    public CompletableFuture<Void> checkValid() {
        return checkValid(new NullCallback<>());
    }

    @Override
    public CompletableFuture<Void> checkValid(Callback<Void> callback) {
        return CompletableFuture.runAsync(() -> {
            try {
                if (!doCheckValid())
                    throw new CompletionException(new HiveException(HiveException.NO_RPC_NODE_AVAILABLE));
            } catch (HiveException e) {
                callback.onError(e);
                throw new CompletionException(e);
            }
        });
    }
}
