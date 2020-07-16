package org.elastos.hive.vendor.vault;

import org.elastos.did.DIDDocument;
import org.elastos.hive.Client;
import org.elastos.hive.exception.HiveException;

public class VaultOptions extends Client.Options {

    private DIDDocument doc;
    private String did;
    private String keyName;
    private String storePass;
    private String nodeUrl;

    public DIDDocument doc() {
        return doc;
    }

    public void setDoc(DIDDocument doc) {
        this.doc = doc;
    }

    public String keyName() {
        return keyName;
    }

    public void setKeyName(String keyName) {
        this.keyName = keyName;
    }

    public String did() {
        return did;
    }

    private void setDid(String did) {
        this.did = did;
    }

    public String storePass() {
        return storePass;
    }

    private void setStorePass(String pwd) {
        this.storePass = pwd;
    }

    private void setNodeUrl(String url) {
        this.nodeUrl = url;
    }

    public String nodeUrl() {
        return nodeUrl;
    }

    @Override
    protected boolean checkValid(boolean all) {
        return (did != null && storePass != null);
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

        public Builder setDoc(DIDDocument doc) {
            options.setDoc(doc);
            return this;
        }

        public Builder setKeyName(String keyName) {
            options.setKeyName(keyName);
            return this;
        }

        public Builder setDid(String did) {
            options.setDid(did);
            return this;
        }

        public Builder setPassword(String pwd) {
            options.setStorePass(pwd);
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
