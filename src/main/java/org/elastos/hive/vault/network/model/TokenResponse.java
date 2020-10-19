package org.elastos.hive.vault.network.model;

public class TokenResponse {
    private String access_token;
    private long expires_in;
    private String scope;
    private String token_type;
    private String refresh_token;

    public String getAccess_token() {
        return access_token;
    }

    public long getExpires_in() {
        return expires_in;
    }


    public String getToken_type() {
        return token_type;
    }

    public String getRefresh_token() {
        return refresh_token;
    }

}
