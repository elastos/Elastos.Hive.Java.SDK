package org.elastos.hive.vendors.onedrive.Model;

import org.elastos.hive.AuthToken;

/**
 * Package: org.elastos.hive.vendors.onedrive.Model
 * ClassName: BaseServiceConfig
 * Created by ranwang on 2019/6/24.
 */
public class BaseServiceConfig {
    private boolean useGsonConverter ;
    private boolean useAuthHeader ;
    private AuthToken authToken ;
    private boolean isNobody ;

    public BaseServiceConfig(){
    }

    public BaseServiceConfig(boolean useGsonConverter, boolean useAuthHeader, AuthToken authToken, boolean isNobody) {
        this.useGsonConverter = useGsonConverter;
        this.useAuthHeader = useAuthHeader;
        this.authToken = authToken;
        this.isNobody = isNobody;
    }

    public boolean isUseGsonConverter() {
        return useGsonConverter;
    }

    public void setUseGsonConverter(boolean useGsonConverter) {
        this.useGsonConverter = useGsonConverter;
    }

    public boolean isUseAuthHeader() {
        return useAuthHeader;
    }

    public void setUseAuthHeader(boolean useAuthHeader) {
        this.useAuthHeader = useAuthHeader;
    }

    public AuthToken getAuthToken() {
        return authToken;
    }

    public void setAuthToken(AuthToken authToken) {
        this.authToken = authToken;
    }

    public boolean isNobody() {
        return isNobody;
    }

    public void setNobody(boolean nobody) {
        isNobody = nobody;
    }

}
