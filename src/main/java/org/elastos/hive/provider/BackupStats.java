package org.elastos.hive.provider;

import com.google.gson.annotations.SerializedName;

import java.util.List;

class BackupStats {
    @SerializedName("backups")
    private List<BackupDetail> backups;

    List<BackupDetail> getBackups() {
        return backups;
    }
}
