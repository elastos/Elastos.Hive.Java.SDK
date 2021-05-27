package org.elastos.hive.vault.files;

import com.google.gson.annotations.SerializedName;
import org.elastos.hive.connection.HiveResponseBody;

class FilesPropertiesResponseBody extends HiveResponseBody {
    @SerializedName("type")
    private String type;
    @SerializedName("name")
    private String name;
    @SerializedName("size")
    private int size;
    @SerializedName("last_modify")
    private double lastModify;

    public FileInfoV1 getFileInfo() {
        FileInfoV1 info = new FileInfoV1();
        info.setType(this.type);
        info.setName(this.name);
        info.setSize(this.size);
        info.setLastModify(this.lastModify);
        return info;
    }
}
