package org.elastos.hive.vendor.vault;

import org.elastos.hive.Client;
import org.elastos.hive.exception.HiveException;

public class VaultOptions extends Client.Options {

    private String did;
    private String pwd;
    private String nodeUrl;

    public String did() {
        return did;
    }

    private void setDid(String did) {
        this.did = did;
    }

    public String password() {
        return pwd;
    }

    private void setPassword(String pwd) {
        this.pwd = pwd;
    }

    private void setNodeUrl(String url) {
        this.nodeUrl = url;
    }

    public String nodeUrl() {
        return nodeUrl;
    }

    @Override
    protected boolean checkValid(boolean all) {
        return (did != null && pwd != null);
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

        public Builder setPassword(String pwd) {
            options.setPassword(pwd);
            return this;
        }

        public Builder setNodeUrl(String nodeUrl) {
            options.setNodeUrl(nodeUrl);
            return this;
        }

        public Builder setStorePath(String storePath) {
            options.setStorePath(storePath);
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
