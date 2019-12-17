package org.elastos.hive.vendors.onedrive;

import org.elastos.hive.AuthHelper;
import org.elastos.hive.Authenticator;
import org.elastos.hive.HiveConnectOptions;
import org.elastos.hive.HiveFile;
import org.elastos.hive.IHiveConnect;
import org.elastos.hive.vendors.connection.ConnectionManager;
import org.elastos.hive.vendors.connection.model.BaseServiceConfig;

import java.util.concurrent.ExecutionException;

public class OneDriveConnect implements IHiveConnect {
    private static OneDriveConnect mOneDriveConnectInstance ;
    private static OneDriveConnectOptions oneDriveConnectOptions ;
    private static AuthHelper authHelper;

    private OneDriveConnect(){
    }

    public static IHiveConnect createInstance(OneDriveConnectOptions hiveConnectOptions){
        if (null == mOneDriveConnectInstance){
            mOneDriveConnectInstance = new OneDriveConnect();
        }
        oneDriveConnectOptions = hiveConnectOptions;
        return mOneDriveConnectInstance;
    }

    public static IHiveConnect getInstance(){
        return mOneDriveConnectInstance ;
    }

    @Override
    public void connect(Authenticator authenticator) {
        try {
            BaseServiceConfig config = new BaseServiceConfig.Builder().build();
            ConnectionManager.resetOneDriveApi(OneDriveConstance.ONE_DRIVE_API_BASE_URL,config);

            if (oneDriveConnectOptions.getPersistent() == null){
                String storePath = oneDriveConnectOptions.getStorePath();
                if (storePath == null || storePath.trim().equals("")) storePath = HiveConnectOptions.DEFAULT_STORE_PATH;
                oneDriveConnectOptions.setPersistent(new OneDriveAuthInfoStoreImpl(storePath));
            }

            authHelper = new OneDriveAuthHelper(oneDriveConnectOptions.getClientId(),
                    oneDriveConnectOptions.getScope(),
                    oneDriveConnectOptions.getRedirectUrl(),
                    oneDriveConnectOptions.getPersistent());

            authHelper.loginAsync(authenticator).get();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void disConnect() {
    }

    @Override
    public void setEncryptKey(String encryptKey) {
    }

    @Override
    public <T extends HiveFile> T createHiveFile(String filename) {
        return (T) new OneDriveFile(filename , authHelper);
    }

    @Override
    public <T extends HiveFile> T createHiveFile() {
        return (T) new OneDriveFile("" , authHelper);
    }
}
