package org.elastos.hive.subscription;

import com.google.gson.annotations.SerializedName;

public class BackupInfoResponse {
    private String did;
    @SerializedName("max_storage")
    private long maxStorage;
    @SerializedName("file_use_storage")
    private long fileUseStorage;
    @SerializedName("db_use_storage")
    private long dbUseStorage;
    @SerializedName("modify_time")
    private long modifyTime;
    @SerializedName("start_time")
    private long startTime;
    @SerializedName("end_time")
    private long endTime;
    @SerializedName("pricing_using")
    private String pricingUsing;
    private String state;

    public String getDid() {
        return did;
    }

    public long getMaxStorage() {
        return maxStorage;
    }

    public long getFileUseStorage() {
        return fileUseStorage;
    }

    public long getDbUseStorage() {
        return dbUseStorage;
    }

    public long getModifyTime() {
        return modifyTime;
    }

    public long getStartTime() {
        return startTime;
    }

    public long getEndTime() {
        return endTime;
    }

    public String getPricingUsing() {
        return pricingUsing;
    }

    public String getState() {
        return state;
    }
}
