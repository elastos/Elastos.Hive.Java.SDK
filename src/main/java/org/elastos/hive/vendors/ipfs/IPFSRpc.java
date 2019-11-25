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
