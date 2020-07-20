package org.elastos.hive.vendor.vault;

import org.elastos.did.DIDDocument;
import org.elastos.hive.Client;
import org.elastos.hive.exception.HiveException;

public class VaultOptions extends Client.Options {

    //DID (Optional)
    private String network; //"TestNet"
    private String resolver; //"http://api.elastos.io:21606"
    private boolean verbose; //true
    private String passphrase; // "secret"
    private String tempDir; //"TEMP"
    private String storeRoot; //"DIDStore"
    private String storePass; //"passwd"
    private String walletDir; //"walletDir"
    private String walletId; //"test"
    private String walletPassword; //"passwd"

    private String did;
    private String keyName;
    private String nodeUrl;


    public String network() {
        return network==null?VaultConstance.networkConfig:network;
    }

    public void setNetwork(String network) {
        this.network = network;
    }

    public String resolver() {
        return resolver==null?VaultConstance.resolver:resolver;
    }

    public void setResolver(String resolver) {
        this.resolver = resolver;
    }

    public boolean verbose() {
        return verbose;
    }

    public void setVerbose(boolean verbose) {
        this.verbose = verbose;
    }

    public String passphrase() {
        return passphrase;
    }

    public void setPassphrase(String passphrase) {
        this.passphrase = passphrase;
    }

    public String tempDir() {
        return tempDir==null?VaultConstance.tempDir:tempDir;
    }

    public void setTempDir(String tempDir) {
        this.tempDir = tempDir;
    }

    public String storeRoot() {
        return storeRoot;
    }

    public void setStoreRoot(String storeRoot) {
        this.storeRoot = storeRoot;
    }

    public String walletDir() {
        return walletDir;
    }

    public void setWalletDir(String walletDir) {
        this.walletDir = walletDir;
    }

    public String walletId() {
        return walletId;
    }

    public void setWalletId(String walletId) {
        this.walletId = walletId;
    }

    public String walletPassword() {
        return walletPassword;
    }

    public void setWalletPassword(String walletPassword) {
        this.walletPassword = walletPassword;
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

        public Builder setNetwork(String network) {
            options.setNetwork(network);
            return this;
        }

        public Builder setResolver(String resolver) {
            options.setResolver(resolver);
            return this;
        }

        public Builder setVerbose(boolean verbose) {
            options.setVerbose(verbose);
            return this;
        }

        public Builder setPassphrase(String passphrase) {
            options.setPassphrase(passphrase);
            return this;
        }

        public Builder setTempDir(String tempDir) {
            options.setTempDir(tempDir);
            return this;
        }

        public Builder setStoreRoot(String storeRoot) {
            options.setStoreRoot(storeRoot);
            return this;
        }

        public Builder setWalletDir(String walletDir) {
            options.setWalletDir(walletDir);
            return this;
        }

        public Builder setKeyName(String keyName) {
            options.setKeyName(keyName);
            return this;
        }

        public Builder setWalletId(String walletId) {
            options.setWalletId(walletId);
            return this;
        }

        public Builder setWalletPassword(String walletPassword) {
            options.setWalletPassword(walletPassword);
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
