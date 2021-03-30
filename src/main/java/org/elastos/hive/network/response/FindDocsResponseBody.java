package org.elastos.hive.network.response;

import com.google.gson.annotations.SerializedName;
import org.elastos.hive.network.model.KeyValueDict;

import java.util.List;

public class FindDocsResponseBody extends HiveResponseBody {
    @SerializedName("items")
    private List<KeyValueDict> items;

    public List<KeyValueDict> getItems() {
        return this.items;
    }
}
