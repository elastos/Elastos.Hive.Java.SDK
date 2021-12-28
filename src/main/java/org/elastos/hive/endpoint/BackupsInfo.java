package org.elastos.hive.endpoint;

import com.google.gson.annotations.SerializedName;

import java.util.List;

class BackupsInfo {
    @SerializedName("backups")
    private List<BackupDetail> backups;

    List<BackupDetail> getBackups() {
        return backups;
    }
}
