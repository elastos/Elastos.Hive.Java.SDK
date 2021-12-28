package org.elastos.hive.endpoint;

import com.google.gson.annotations.SerializedName;

import java.util.List;

class VaultAppsInfo {
    @SerializedName("apps")
    private List<VaultAppDetail> apps;

    List<VaultAppDetail> getApps() {
        return apps;
    }
}
