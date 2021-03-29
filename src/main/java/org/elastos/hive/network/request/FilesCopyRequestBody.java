package org.elastos.hive.network.request;

public class FilesCopyRequestBody extends FilesMoveRequestBody {
    public FilesCopyRequestBody(String srcPath, String dstPath) {
        super(srcPath, dstPath);
    }
}
