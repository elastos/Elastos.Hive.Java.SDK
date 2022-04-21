package org.elastos.hive.subscription;

import com.google.gson.annotations.SerializedName;

public class AppInfo {
    private String name;
    @SerializedName("developer_did")
    private String developerDid;
    @SerializedName("icon_url")
    private String iconUrl;
    // skip redirect_url
    @SerializedName("user_did")
    private String userDid;
    @SerializedName("app_did")
    private String appDid;
    @SerializedName("used_storage_size")
    private int usedStorageSize;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDeveloperDid() {
        return developerDid;
    }

    public void setDeveloperDid(String developerDid) {
        this.developerDid = developerDid;
    }

    public String getIconUrl() {
        return iconUrl;
    }

    public void setIconUrl(String iconUrl) {
        this.iconUrl = iconUrl;
    }

    public String getUserDid() {
        return userDid;
    }

    public void setUserDid(String userDid) {
        this.userDid = userDid;
    }

    public String getAppDid() {
        return appDid;
    }

    public void setAppDid(String appDid) {
        this.appDid = appDid;
    }

    public int getUsedStorageSize() {
        return usedStorageSize;
    }

    public void setUsedStorageSize(int usedStorageSize) {
        this.usedStorageSize = usedStorageSize;
    }
}
