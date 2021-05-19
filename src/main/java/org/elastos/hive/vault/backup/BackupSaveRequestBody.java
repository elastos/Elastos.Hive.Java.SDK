package org.elastos.hive.vault.backup;

import com.google.gson.annotations.SerializedName;

class BackupSaveRequestBody {
    @SerializedName("backup_credential")
    private final String backupCredential;

    public BackupSaveRequestBody(String backupCredential) {
        this.backupCredential = backupCredential;
    }
}
