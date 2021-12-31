package org.elastos.hive.subscription;

import com.google.gson.annotations.SerializedName;

import java.util.List;

class AppStats {
    @SerializedName("apps")
    private List<AppInfo> apps;

    List<AppInfo> getApps() {
        return apps;
    }
}
