package org.elastos.hive.service;

import org.elastos.hive.vault.backup.BackupResult;

public interface BackupServiceProgress {
    void onProgress(BackupResult.State action, BackupResult.Result result, String message);
}
