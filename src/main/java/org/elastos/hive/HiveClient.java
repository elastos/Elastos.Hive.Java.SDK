package org.elastos.hive;

import org.elastos.hive.vendors.ipfs.IPFSConnect;
import org.elastos.hive.vendors.ipfs.IPFSConnectOptions;
import org.elastos.hive.vendors.onedrive.OneDriveConnect;
import org.elastos.hive.vendors.onedrive.OneDriveConnectOptions;

public class HiveClient {
    private static HiveClient mInstance ;

    private HiveClient(HiveClientOptions hiveOptions){
    }

    public static HiveClient createInstance(HiveClientOptions hiveOptions) {
        if (mInstance == null){
            mInstance = new HiveClient(hiveOptions);
        }

        return mInstance;
    }

    public static HiveClient getInstance() {
        return mInstance;
    }

    public void close() {
        mInstance = null ;
    }

    public IHiveConnect connect(HiveConnectOptions hiveConnectOptions) throws HiveException {
        HiveConnectOptions.HiveBackendType backendType = hiveConnectOptions.getBackendType();
        IHiveConnect hiveConnect = null ;
        switch (backendType){
            case HiveBackendType_IPFS:
                hiveConnect = IPFSConnect.createInstance((IPFSConnectOptions)hiveConnectOptions);
                break;
            case HiveBackendType_OneDrive:
                hiveConnect = OneDriveConnect.createInstance((OneDriveConnectOptions)hiveConnectOptions);
                break;
            case HiveBackendType_ownCloud:
                break;
            case HiveDriveType_Butt:
                break;
            default:
                break;
        }
        hiveConnect.connect(hiveConnectOptions.getAuthenticator());
        return hiveConnect;
    }

    public int disConnect(IHiveConnect hiveConnect) {
        if (hiveConnect!=null) hiveConnect.disConnect();
        return 0;
    }
}
