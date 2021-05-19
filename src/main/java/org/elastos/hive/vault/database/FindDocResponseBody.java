package org.elastos.hive.vault.database;

import com.google.gson.annotations.SerializedName;
import org.elastos.hive.network.model.KeyValueDict;
import org.elastos.hive.connection.HiveResponseBody;

class FindDocResponseBody extends HiveResponseBody {
    @SerializedName("items")
    private KeyValueDict item;

    public KeyValueDict getItem() {
        return this.item;
    }
}
