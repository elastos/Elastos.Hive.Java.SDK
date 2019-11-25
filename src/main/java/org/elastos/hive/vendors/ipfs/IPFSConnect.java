package org.elastos.hive.vendors.ipfs;

import org.elastos.hive.Authenticator;
import org.elastos.hive.HiveConnectOptions;
import org.elastos.hive.HiveException;
import org.elastos.hive.HiveFile;
import org.elastos.hive.IHiveConnect;
import org.elastos.hive.vendors.onedrive.OneDriveFile;

public class IPFSConnect implements IHiveConnect {
    private static IPFSConnect mIPFSConnectInstance ;
    private static IPFSRpc ipfsRpc ;

    private IPFSConnect(){
    }

    public static IHiveConnect createInstance(IPFSConnectOptions hiveConnectOptions){
        if (null == mIPFSConnectInstance){
            mIPFSConnectInstance = new IPFSConnect();
        }
        ipfsRpc = new IPFSRpc(hiveConnectOptions.getHiveRpcNodes());
        return mIPFSConnectInstance;
    }

    public static IHiveConnect getInstance(){
        return  mIPFSConnectInstance;
    }

    @Override
    public void connect(Authenticator authenticator) throws HiveException {
        ipfsRpc.checkReachable();
    }

    @Override
    public void disConnect() {
    }

    @Override
    public void setEncryptKey(String encryptKey) {
    }

    @Override
    public <T extends HiveFile> T createHiveFile(String filename) {
        return (T) new IPFSFile(ipfsRpc);
    }

    @Override
    public <T extends HiveFile> T createHiveFile() {
        return (T) new IPFSFile(ipfsRpc);
    }
}
