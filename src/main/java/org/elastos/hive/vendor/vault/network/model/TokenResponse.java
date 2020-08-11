package org.elastos.hive.vendor.vault.network.model;

public class TokenResponse extends BaseResponse {
    private String token;
    private String refresh_token;
    private String token_uri;
    private String client_id;
    private String client_secret;
    private String scopes;
    private String expiry;

    public TokenResponse(String scopes, String expiry, String refresh_token) {
        this.scopes = scopes;
        this.expiry = expiry;
        this.refresh_token = refresh_token;
    }

    public String getClient_secret() {
        return client_secret;
    }

    public void setClient_secret(String client_secret) {
        this.client_secret = client_secret;
    }

    public String getClient_id() {
        return client_id;
    }

    public void setClient_id(String client_id) {
        this.client_id = client_id;
    }

    public String getToken_uri() {
        return token_uri;
    }

    public void setToken_uri(String token_uri) {
        this.token_uri = token_uri;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getScopes() {
        return scopes;
    }

    public void setScopes(String scopes) {
        this.scopes = scopes;
    }

    public String getExpiry() {
        return expiry;
    }

    public void setExpiry(String expiry) {
        this.expiry = expiry;
    }

    public String getRefresh_token() {
        return refresh_token;
    }

    public void setRefresh_token(String refresh_token) {
        this.refresh_token = refresh_token;
    }

    @Override
    public String toString() {
        return "TokenResponse{" +
                ", scope='" + scopes + '\'' +
                ", expires_in=" + expiry +
                ", refresh_token='" + refresh_token + '\'' +
                '}';
    }

}
