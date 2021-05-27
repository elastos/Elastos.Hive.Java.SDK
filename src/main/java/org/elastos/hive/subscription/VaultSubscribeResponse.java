package org.elastos.hive.subscription;

import com.google.gson.annotations.SerializedName;

public class VaultSubscribeResponse {
    @SerializedName("pricePlan")
    private String pricePlan;
    @SerializedName("serviceDid")
    private String serviceDid;
    @SerializedName("quota")
    private int quota;
    @SerializedName("created")
    //TODO:
    private double created;
    @SerializedName("updated")
    private long updated;

    public String getPricePlan() {
        return pricePlan;
    }

    public String getServiceDid() {
        return serviceDid;
    }

    public int getQuota() {
        return quota;
    }

    public double getCreated() {
        return created;
    }

    public long getUpdated() {
        return updated;
    }
}
