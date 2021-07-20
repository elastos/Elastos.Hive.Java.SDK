package org.elastos.hive.vault.scripting;

/**
 * The executable to get the properties of the file.
 */
public class FilePropertiesExecutable extends Executable {
    public FilePropertiesExecutable(String name) {
        super(name, Type.FILE_PROPERTIES, null);
        super.setBody(new FileBody());
    }
}
