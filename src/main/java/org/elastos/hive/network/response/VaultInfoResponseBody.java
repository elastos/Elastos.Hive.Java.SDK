package org.elastos.hive.network.response;

import com.google.gson.annotations.SerializedName;

public class VaultInfoResponseBody extends HiveResponseBody {
    private String did;
    @SerializedName("max_storage")
    private String maxStorage;
    @SerializedName("file_use_storage")
    private String fileUseStorage;
    @SerializedName("db_use_storage")
    private String dbUseStorage;
    @SerializedName("modify_time")
    private String modifyTime;
    @SerializedName("start_time")
    private String startTime;
    @SerializedName("end_time")
    private String endTime;
    @SerializedName("pricing_using")
    private String pricingUsing;
    private String state;

    public String getDid() {
        return did;
    }

    public String getMaxStorage() {
        return maxStorage;
    }

    public String getFileUseStorage() {
        return fileUseStorage;
    }

    public String getDbUseStorage() {
        return dbUseStorage;
    }

    public String getModifyTime() {
        return modifyTime;
    }

    public String getStartTime() {
        return startTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public String getPricingUsing() {
        return pricingUsing;
    }

    public String getState() {
        return state;
    }
}
