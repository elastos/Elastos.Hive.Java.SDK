package org.elastos.hive.vault.scripting;

/**
 * The executable to get the hash code of the file content.
 */
public class FileHashExecutable extends Executable {
    public FileHashExecutable(String name) {
        super(name, Type.FILE_HASH, null);
        super.setBody(new FileBody());
    }
}
