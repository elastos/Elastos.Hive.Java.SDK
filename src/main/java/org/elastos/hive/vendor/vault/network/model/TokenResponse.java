package org.elastos.hive.vendor.vault.network.model;

public class TokenResponse extends BaseResponse {
    private String token_type;
    private String scope;
    private int expires_in;
    private int ext_expires_in;
    private String access_token;
    private String refresh_token;

    public TokenResponse(String token_type, String scope, int expires_in,
                         int ext_expires_in, String access_token, String refresh_token) {
        this.token_type = token_type;
        this.scope = scope;
        this.expires_in = expires_in;
        this.ext_expires_in = ext_expires_in;
        this.access_token = access_token;
        this.refresh_token = refresh_token;
    }

    public String getToken_type() {
        return token_type;
    }

    public void setToken_type(String token_type) {
        this.token_type = token_type;
    }

    public String getScope() {
        return scope;
    }

    public void setScope(String scope) {
        this.scope = scope;
    }

    public int getExpires_in() {
        return expires_in;
    }

    public void setExpires_in(int expires_in) {
        this.expires_in = expires_in;
    }

    public int getExt_expires_in() {
        return ext_expires_in;
    }

    public void setExt_expires_in(int ext_expires_in) {
        this.ext_expires_in = ext_expires_in;
    }

    public String getAccess_token() {
        return access_token;
    }

    public void setAccess_token(String access_token) {
        this.access_token = access_token;
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
                "token_type='" + token_type + '\'' +
                ", scope='" + scope + '\'' +
                ", expires_in=" + expires_in +
                ", ext_expires_in=" + ext_expires_in +
                ", access_token='" + access_token + '\'' +
                ", refresh_token='" + refresh_token + '\'' +
                '}';
    }

}
