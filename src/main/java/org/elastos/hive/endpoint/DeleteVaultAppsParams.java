package org.elastos.hive.endpoint;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class DeleteVaultAppsParams {
    @SerializedName("app_dids")
    private List<String> appDids;

    public DeleteVaultAppsParams(List<String> appDids) {
        this.appDids = appDids;
    }
}
