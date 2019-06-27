package org.elastos.hive.vendors.onedrive.Model;

import org.elastos.hive.AuthToken;

public class BaseServiceConfig {

    private final boolean useGsonConverter ;
    private final boolean useAuthHeader ;
    private final AuthToken authToken ;
    private final boolean ignoreReturnbody;

    private BaseServiceConfig(){
        this.useGsonConverter = true;
        this.useAuthHeader = true;
        this.ignoreReturnbody = false;
        this.authToken = null;
    }

    private BaseServiceConfig(Builder builder){
        this.useGsonConverter = builder.useGsonConverter;
        this.useAuthHeader = builder.useAuthHeader;
        this.ignoreReturnbody = builder.ignoreReturnbody;
        this.authToken = builder.authToken;
    }

    public boolean isUseGsonConverter() {
        return useGsonConverter;
    }

    public boolean isUseAuthHeader() {
        return useAuthHeader;
    }

    public AuthToken getAuthToken() {
        return authToken;
    }

    public boolean isIgnoreReturnbody() {
        return ignoreReturnbody;
    }

    public static final class Builder {
        boolean useGsonConverter ;
        boolean useAuthHeader ;
        AuthToken authToken ;
        boolean ignoreReturnbody;

        public Builder(AuthToken authToken) {
            this.useGsonConverter = true ;
            this.useAuthHeader = true ;
            this.authToken = authToken;
            this.ignoreReturnbody = false ;
        }

        public Builder useGsonConverter(boolean flag){
            this.useGsonConverter = flag ;
            return this;
        }

        public Builder useAuthHeader(boolean flag){
            this.useAuthHeader = flag ;
            return this;
        }

        public Builder authToken(AuthToken authToken){
            this.authToken = authToken ;
            return this;
        }

        public Builder ignoreReturnBody(boolean flag){
            this.ignoreReturnbody = flag ;
            return this;
        }

        public BaseServiceConfig build() {
            return new BaseServiceConfig(this);
        }
    }

}
