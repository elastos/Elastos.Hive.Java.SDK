package org.elastos.hive.vendor.hivevault;

import org.elastos.hive.Client;
import org.elastos.hive.exception.HiveException;

public class HiveVaultOptions extends Client.Options {

    private long expiration;
    private String did;
    private String pwd;

    public String did() {
        return did;
    }

    public void setDid(String did) {
        this.did = did;
    }

    public String password() {
        return pwd;
    }

    public void setPassword(String pwd) {
        this.pwd = pwd;
    }

    public long expiration() {
        return expiration;
    }

    public void setExpiration(long expiration) {
        this.expiration = expiration;
    }

    @Override
    protected boolean checkValid(boolean all) {
        return (storePath()!=null) && (expiration>=0);
    }

    boolean checkValid() {
        return checkValid(true);
    }

    @Override
    protected Client buildClient() {
        return new HiveVaultClient(this);
    }

    public static class Builder {
        private HiveVaultOptions options;

        public Builder() {
            options = new HiveVaultOptions();
        }

        public Builder setDid(String did) {
            options.setDid(did);
            return this;
        }

        public Builder setPassword(String pwd) {
            options.setPassword(pwd);
            return this;
        }

        public Builder setStorePath(String storePath) {
            options.setStorePath(storePath);
            return this;
        }

        public Builder setExpiration(long expiration) {
            options.setExpiration(expiration);
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
