package org.elastos.hive.file;

import org.elastos.hive.utils.DateUtil;
import org.elastos.hive.vendor.vault.network.model.BaseResponse;

import java.math.BigDecimal;

/**
 * File information about a remote file or folder.
 */
public class FileInfo extends BaseResponse {

    /**

     * Type of a remote file or folder.
     */
    public enum Type {
        /** File */
        FILE,
        /** Folder */
        FOLDER
    };

    private String type;

    private String name;

    private long size;

    private String last_modify;


    public Type getType() {
        switch (type) {
            case "file":
                return Type.FILE;
            case "folder":
                return Type.FOLDER;
        }
        return null;
    }

    public String getName() {
        return name;
    }

    public long getSize() {
        return size;
    }

    public String getLastModify() {
        long timeStamp = new BigDecimal(last_modify).multiply(new BigDecimal(1000)).longValue();
        return DateUtil.getCurrentEpochTimeStamp(timeStamp);
    }
}
