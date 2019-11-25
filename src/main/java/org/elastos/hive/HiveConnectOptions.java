package org.elastos.hive;

public class HiveConnectOptions {
    public static final String DEFAULT_STORE_PATH = System.getProperty("user.dir");
    protected enum HiveBackendType {
        HiveBackendType_IPFS,
        HiveBackendType_OneDrive,
        HiveBackendType_ownCloud,
        HiveDriveType_Butt
    }

    private HiveBackendType backendType ;
    private Authenticator authenticator ;
    private Persistent persistent ;

    protected HiveBackendType getBackendType(){
        return this.backendType;
    }

    protected void setBackendType(HiveBackendType backendType) {
        this.backendType = backendType;
    }

    protected Authenticator getAuthenticator() {
        return authenticator;
    }

    protected void setAuthenticator(Authenticator authenticator) {
        this.authenticator = authenticator;
    }

    public Persistent getPersistent() {
        return persistent;
    }

    public void setPersistent(Persistent persistent) {
        this.persistent = persistent;
    }

}
