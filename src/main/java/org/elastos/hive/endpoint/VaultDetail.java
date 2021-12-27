package org.elastos.hive.endpoint;

import com.google.gson.annotations.SerializedName;

public class VaultDetail {
    @SerializedName("pricing_using")
    private String pricingName;
    @SerializedName("max_storage")
    private int maxStorage;
    @SerializedName("file_use_storage")
    private int fileUseStorage;
    @SerializedName("db_use_storage")
    private int databaseUseStorage;
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

    public int getFileUseStorage() {
        return fileUseStorage;
    }

    public void setFileUseStorage(int fileUseStorage) {
        this.fileUseStorage = fileUseStorage;
    }

    public int getDatabaseUseStorage() {
        return databaseUseStorage;
    }

    public void setDatabaseUseStorage(int databaseUseStorage) {
        this.databaseUseStorage = databaseUseStorage;
    }

    public String getUserDid() {
        return userDid;
    }

    public void setUserDid(String userDid) {
        this.userDid = userDid;
    }
}
