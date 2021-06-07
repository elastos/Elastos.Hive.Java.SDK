package org.elastos.hive.vault.database;

import com.google.gson.annotations.SerializedName;
import org.elastos.hive.connection.KeyValueDict;

import java.util.List;

public class QueryOptions {
    private int skip;
    private int limit;
    private KeyValueDict projection;
    // ex: [('_id', -1)]
    private List<List<Object>> sort;
    @SerializedName("allow_partial_results")
    private boolean allowPartialResults;
    @SerializedName("return_key")
    private boolean returnKey;
    @SerializedName("show_record_id")
    private boolean showRecordId;
    @SerializedName("batch_size")
    private int batchSize;

    public void setSkip(int skip) {
        this.skip = skip;
    }

    public void setLimit(int limit) {
        this.limit = limit;
    }

    public void setProjection(KeyValueDict projection) {
        this.projection = projection;
    }

    public void setSort(List<List<Object>> sort) {
        this.sort = sort;
    }

    public void setAllowPartialResults(boolean allowPartialResults) {
        this.allowPartialResults = allowPartialResults;
    }

    public void setReturnKey(boolean returnKey) {
        this.returnKey = returnKey;
    }

    public void setShowRecordId(boolean showRecordId) {
        this.showRecordId = showRecordId;
    }

    public void setBatchSize(int batchSize) {
        this.batchSize = batchSize;
    }
}
