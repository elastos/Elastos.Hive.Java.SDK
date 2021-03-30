package org.elastos.hive.network.response;

import com.google.gson.annotations.SerializedName;
import org.elastos.hive.network.model.KeyValueDict;

public class FindDocResponseBody extends HiveResponseBody {
    @SerializedName("items")
    private KeyValueDict item;

    public KeyValueDict getItem() {
        return this.item;
    }
}
