package org.elastos.hive.vault.scripting;

/**
 * Convenient class for downloading file by the script.
 */
public class FileDownloadExecutable extends Executable {
    public FileDownloadExecutable(String name) {
        super(name, Type.FILE_DOWNLOAD, null);
        super.setBody(new FileBody());
    }
}
