package org.elastos.hive.vault.backup;

import com.google.gson.annotations.SerializedName;
import org.elastos.hive.connection.HiveResponseBody;
import org.elastos.hive.service.BackupService;

import java.io.IOException;

public class BackupStateResponseBody extends HiveResponseBody {
    @SerializedName("hive_backup_state")
    private String hiveBackupState;
    @SerializedName("result")
    private String result;

    public BackupService.BackupResult getStatusResult() throws IOException {
        switch (hiveBackupState) {
            case "stop":
                return BackupService.BackupResult.STATE_STOP;
            case "backup":
                return BackupService.BackupResult.STATE_BACKUP;
            case "restore":
                return BackupService.BackupResult.STATE_RESTORE;
            default:
                throw new IOException("Unknown state :" + result);
        }
    }
}
