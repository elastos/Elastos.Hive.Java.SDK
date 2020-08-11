package org.elastos.hive.vendor.vault;

import org.elastos.hive.Authenticator;
import org.elastos.hive.Client;
import org.elastos.hive.exception.HiveException;
import org.elastos.hive.vendor.onedrive.OneDriveOptions;

public class VaultOptions extends Client.Options {

    private String authToken;
    private String clientId;
    private String clientSecret;
    private String redirectURL;
    private String nodeUrl;


    public void setAuthToken(String authToken) {
        this.authToken = authToken;
    }

    public String authToken() {return this.authToken;}

    public void setClientId(String clientId) {
        this.clientId = clientId;
    }

    public String clientId() {return this.clientId;}

    public void setClientSecret(String clientSecret) {
        this.clientSecret = clientSecret;
    }

    public String clientSecret() {
        return this.clientSecret;
    }

    public void setRedirectURL(String redirectURL) {
        this.redirectURL = redirectURL;
    }

    public String redirectURL() {return this.redirectURL;}

    private void setNodeUrl(String url) {
        this.nodeUrl = url;
    }

    public String nodeUrl() {
        return nodeUrl;
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

        public Builder setNodeUrl(String nodeUrl) {
            options.setNodeUrl(nodeUrl);
            return this;
        }

        public Builder setStorePath(String storePath) {
            options.setStorePath(storePath);
            return this;
        }

        public Builder setAuthToken(String authToken) {
            options.setAuthToken(authToken);
            return this;
        }

        public Builder setClientId(String clientId) {
            options.setClientId(clientId);
            return this;
        }

        public Builder setClientSecret(String clientSecret) {
            options.setClientSecret(clientSecret);
            return this;
        }

        public Builder setRedirectURL(String redirectURL) {
            options.setRedirectURL(redirectURL);
            return this;
        }

        public Builder setAuthenticator(Authenticator authenticator) {
            options.setAuthenticator(authenticator);
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
