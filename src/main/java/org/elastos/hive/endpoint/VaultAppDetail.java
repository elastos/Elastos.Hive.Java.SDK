package org.elastos.hive.endpoint;

import com.google.gson.annotations.SerializedName;

public class VaultAppDetail {
    @SerializedName("user_did")
    private String userDid;
    @SerializedName("app_did")
    private String appDid;
    @SerializedName("database_name")
    private String databaseName;
    @SerializedName("file_use_storage")
    private int fileUseStorage;
    @SerializedName("db_use_storage")
    private int databaseUseStorage;

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

    public String getDatabaseName() {
        return databaseName;
    }

    public void setDatabaseName(String databaseName) {
        this.databaseName = databaseName;
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
}
