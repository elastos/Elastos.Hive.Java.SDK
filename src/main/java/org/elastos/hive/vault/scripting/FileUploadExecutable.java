package org.elastos.hive.vault.scripting;

/**
 * The executable to upload the file content.
 */
public class FileUploadExecutable extends Executable {
    public FileUploadExecutable(String name) {
        super(name, Type.FILE_UPLOAD, null);
        super.setBody(new FileBody());
    }
}
