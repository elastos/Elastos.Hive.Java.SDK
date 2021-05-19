package org.elastos.hive.vault.files;

import com.google.gson.annotations.SerializedName;
import org.elastos.hive.connection.HiveResponseBody;

public class FilesPropertiesResponseBody extends HiveResponseBody {
    @SerializedName("type")
    private String type;
    @SerializedName("name")
    private String name;
    @SerializedName("size")
    private int size;
    @SerializedName("last_modify")
    private double lastModify;

    public FileInfo getFileInfo() {
        FileInfo info = new FileInfo();
        info.setType(this.type);
        info.setName(this.name);
        info.setSize(this.size);
        info.setLastModify(this.lastModify);
        return info;
    }
}
