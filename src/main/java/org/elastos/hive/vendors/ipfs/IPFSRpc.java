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

import org.elastos.hive.HiveError;
import org.elastos.hive.HiveException;
import org.elastos.hive.vendors.HiveRpcNode;
import org.elastos.hive.vendors.connection.ConnectionManager;

import retrofit2.Response;

public class IPFSRpc {
    HiveRpcNode[] mHiveRpcNodes ;
    HiveRpcNode mHiveRpcNode ;

    public IPFSRpc(HiveRpcNode[] hiveRpcNodes) {
        this.mHiveRpcNodes = hiveRpcNodes;
    }

    void checkReachable() throws HiveException {
        if (mHiveRpcNodes == null || mHiveRpcNodes.length == 0){
            throw new HiveException(HiveError.RPC_NODE_NULL);
        }

        for (HiveRpcNode hiveRpcNode : mHiveRpcNodes){
            if (checkConnect(hiveRpcNode)){
                return ;
            }
        }

        if (mHiveRpcNode == null){
            throw new HiveException(HiveError.NO_RPC_NODE_AVAILABLE);
        }
    }

    void selectBootstrap(HiveRpcNode hiveRpcNode)  {
        mHiveRpcNode = hiveRpcNode ;
        String baseUrl = String.format(IPFSConstance.URLFORMAT, mHiveRpcNode.getIpv4(),mHiveRpcNode.getPort());
        try {
            ConnectionManager.resetIPFSApi(baseUrl);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    HiveRpcNode getCurrentNode(){
        return mHiveRpcNode;
    }

    boolean isAvailable(){
        if (mHiveRpcNode == null){
            return false ;
        }
        return true ;
    }

    boolean checkConnect(HiveRpcNode hiveRpcNode) throws HiveException {
        if (hiveRpcNode == null){
            throw new HiveException(HiveError.RPC_NODE_NULL);
        }

        boolean isSelect = false ;
        String ipv4 = hiveRpcNode.getIpv4();
        String ipv6 = hiveRpcNode.getIpv6();
        int port = hiveRpcNode.getPort();
        if (port == 0){
            throw new HiveException(HiveError.RPC_NODE_PORT_NULL);
        }

        if (checkConnect(ipv4 , port)!=null){
            selectBootstrap(hiveRpcNode);
            isSelect = true ;
        }

        if (checkConnect(ipv6 , port)!=null){
            selectBootstrap(hiveRpcNode);
            isSelect = true ;
        }

        return isSelect ;
    }

    private Response checkConnect(String ip , int port){

        if (ip!=null&&!ip.equals("")){
            Response response = null;
            try {
                response = ConnectionManager.getIPFSApi().version(ip,port).execute();
            } catch (Exception e) {
                e.printStackTrace();
            }
            if (response == null) return null;
            if (response.code() == 200) return response;
        }
        return null;
    }
}
