package org.elastos.hive.file;

import java.util.Date;

/**
 * File information about a remote file or folder.
 */
public class FileInfo {

    /**

     * Type of a remote file or folder.
     */
    public enum Type {
        /** File */
        FILE,
        /** Folder */
        FOLDER
    };

    private Type type;

    private String name;

    private long size;

    private Date last_modify;


    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public long getSize() {
        return size;
    }

    public void setSize(long size) {
        this.size = size;
    }

    public Date getLastModify() {
        return last_modify;
    }

    public void setLast_modify(Date last_modify) {
        this.last_modify = last_modify;
    }
}
