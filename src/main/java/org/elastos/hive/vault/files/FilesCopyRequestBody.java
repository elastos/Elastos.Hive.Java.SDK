package org.elastos.hive.vault.files;

public class FilesCopyRequestBody extends FilesMoveRequestBody {
    public FilesCopyRequestBody(String srcPath, String dstPath) {
        super(srcPath, dstPath);
    }
}
