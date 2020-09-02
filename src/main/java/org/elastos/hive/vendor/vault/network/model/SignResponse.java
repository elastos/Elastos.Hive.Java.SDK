package org.elastos.hive.vendor.vault.network.model;

public class SignResponse extends BaseResponse {

    private String jwt;


    public String getJwt() {
        return jwt;
    }

    public void setJwt(String jwt) {
        this.jwt = jwt;
    }
}
