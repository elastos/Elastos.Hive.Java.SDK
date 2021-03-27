package org.elastos.hive.network.request;

import com.google.gson.annotations.SerializedName;

public class BackupSaveRequestBody {
    @SerializedName("backup_credential")
    private final String backupCredential;

    public BackupSaveRequestBody(String backupCredential) {
        this.backupCredential = backupCredential;
    }
}
