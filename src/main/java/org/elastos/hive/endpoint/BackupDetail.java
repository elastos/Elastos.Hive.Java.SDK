package org.elastos.hive.endpoint;

import com.google.gson.annotations.SerializedName;

public class BackupDetail {
    @SerializedName("pricing_using")
    private String pricingName;
    @SerializedName("max_storage")
    private int maxStorage;
    @SerializedName("use_storage")
    private int useStorage;
    @SerializedName("user_did")
    private String userDid;

    public String getPricingName() {
        return pricingName;
    }

    public void setPricingName(String pricingName) {
        this.pricingName = pricingName;
    }

    public int getMaxStorage() {
        return maxStorage;
    }

    public void setMaxStorage(int maxStorage) {
        this.maxStorage = maxStorage;
    }

    public int getUseStorage() {
        return useStorage;
    }

    public void setUseStorage(int useStorage) {
        this.useStorage = useStorage;
    }

    public String getUserDid() {
        return userDid;
    }

    public void setUserDid(String userDid) {
        this.userDid = userDid;
    }
}
