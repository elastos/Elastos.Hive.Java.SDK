package org.elastos.hive.subscription;

import com.google.gson.annotations.SerializedName;

public class VaultInfoResponse {
    @SerializedName("serviceDid")
    private String serviceDid;
    @SerializedName("storageQuota")
    private int storageQuota;
    @SerializedName("storageUsed")
    private int storageUsed;
    @SerializedName("created")
    //TODO:
    private double created;
    @SerializedName("updated")
    private long updated;
    @SerializedName("pricePlan")
    private String pricePlan;

    public String getServiceDid() {
        return serviceDid;
    }

    public int getStorageQuota() {
        return storageQuota;
    }

    public int getStorageUsed() {
        return storageUsed;
    }

    public double getCreated() {
        return created;
    }

    public long getUpdated() {
        return updated;
    }

    public String getPricePlan() {
        return pricePlan;
    }
}
