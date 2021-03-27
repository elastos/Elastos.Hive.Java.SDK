package org.elastos.hive.network.response;

import com.google.gson.annotations.SerializedName;
import org.elastos.hive.exception.HiveException;
import org.elastos.hive.service.BackupService;

public class BackupStateResponseBody extends HiveResponseBody {
    @SerializedName("hive_backup_state")
    private String hiveBackupState;
    @SerializedName("result")
    private String result;

    public BackupService.BackupResult getStatusResult() throws HiveException {
        if (!"success".equals(result))
            throw new HiveException("Failed to get back-up state.");

        switch (hiveBackupState) {
            case "stop":
                return BackupService.BackupResult.STATE_STOP;
            case "backup":
                return BackupService.BackupResult.STATE_BACKUP;
            case "restore":
                return BackupService.BackupResult.STATE_RESTORE;
            default:
                throw new HiveException("Unknown state :" + result);
        }
    }
}
