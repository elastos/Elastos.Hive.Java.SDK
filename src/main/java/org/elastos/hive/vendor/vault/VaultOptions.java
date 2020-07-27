package org.elastos.hive.vendor.vault;

import org.elastos.hive.Client;
import org.elastos.hive.exception.HiveException;

public class VaultOptions extends Client.Options {

    private String packageId; // application id, bundle id
    private String appDid; // published on the ID sidechain
    private String scope; // the service defined scope, could be a list
    private String claims; //optional, for credential request
    private String did;
    private String nodeUrl;

    public String packageId() {
        return packageId;
    }

    public void setPackageId(String packageId) {
        this.packageId = packageId;
    }

    public String appDid() {
        return appDid;
    }

    public void setAppDid(String appDid) {
        this.appDid = appDid;
    }

    public String scope() {
        return scope;
    }

    public void setScope(String scope) {
        this.scope = scope;
    }

    public String claims() {
        return claims;
    }

    public void setClaims(String claims) {
        this.claims = claims;
    }

    public String did() {
        return did;
    }

    private void setDid(String did) {
        this.did = did;
    }

    private void setNodeUrl(String url) {
        this.nodeUrl = url;
    }

    public String nodeUrl() {
        return nodeUrl;
    }

    @Override
    protected boolean checkValid(boolean all) {
        return did != null;
    }

    boolean checkValid() {
        return checkValid(true);
    }

    @Override
    protected Client buildClient() {
        return new VaultClient(this);
    }

    public static class Builder {
        private VaultOptions options;

        public Builder() {
            options = new VaultOptions();
        }



        public Builder setDid(String did) {
            options.setDid(did);
            return this;
        }

        public Builder setNodeUrl(String nodeUrl) {
            options.setNodeUrl(nodeUrl);
            return this;
        }

        public Builder setPackageId(String packageId) {
            options.setPackageId(packageId);
            return this;
        }

        public Builder setAppDid(String appDid) {
            options.setAppDid(appDid);
            return this;
        }

        public Builder setScope(String scope) {
            options.setScope(scope);
            return this;
        }

        public Builder setClaims(String claims) {
            options.setClaims(claims);
            return this;
        }

        public Client.Options build() throws HiveException {
            if (options == null) {
                throw new HiveException("Builder deprecated");
            }
            if (!options.checkValid()) {
                throw new HiveException("Missing options");
            }

            Client.Options opts = options;
            this.options = null;
            return opts;
        }
    }

}
