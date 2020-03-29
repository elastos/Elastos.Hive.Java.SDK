package org.elastos.hive.vendors.onedrive;

import org.elastos.hive.Authenticator;
import org.elastos.hive.HiveConnectOptions;
import org.elastos.hive.Persistent;

public class OneDriveConnectOptions extends HiveConnectOptions {

    private final String clientId;
    private final String scope;
    private final String redirectUrl;


    public OneDriveConnectOptions(String clientId, String scope, String redirectUrl , Authenticator authenticator) {
        this.clientId = clientId;
        this.scope = scope;
        this.redirectUrl = redirectUrl;
        setBackendType(HiveBackendType.HiveBackendType_OneDrive);
        setAuthenticator(authenticator);
    }

    public String getClientId() {
        return clientId;
    }

    public String getScope() {
        return scope;
    }

    public String getRedirectUrl() {
        return redirectUrl;
    }
}
