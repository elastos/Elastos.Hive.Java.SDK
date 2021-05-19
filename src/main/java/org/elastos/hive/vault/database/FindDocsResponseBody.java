package org.elastos.hive.vault.database;

import com.google.gson.annotations.SerializedName;
import org.elastos.hive.network.model.KeyValueDict;
import org.elastos.hive.connection.HiveResponseBody;

import java.util.List;

class FindDocsResponseBody extends HiveResponseBody {
    @SerializedName("items")
    private List<KeyValueDict> items;

    public List<KeyValueDict> getItems() {
        return this.items;
    }
}
