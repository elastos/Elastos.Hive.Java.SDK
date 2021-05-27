package org.elastos.hive.vault.files;

import com.google.gson.annotations.SerializedName;
import org.elastos.hive.utils.DateUtil;

import java.math.BigDecimal;

public class FileInfoV1 {
    @SerializedName("type")
    private String type;
    @SerializedName("name")
    private String name;
    @SerializedName("size")
    private int size;
    @SerializedName("last_modify")
    private double lastModify;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public void setLastModify(double lastModify) {
        this.lastModify = lastModify;
    }

    public String getLastModified() {
        long timeStamp = BigDecimal.valueOf(lastModify).multiply(new BigDecimal(1000)).longValue();
        return DateUtil.getCurrentEpochTimeStamp(timeStamp);
    }
}
