package org.elastos.hive.vendor.vault.network.model;

public class CountDocResponse extends BaseResponse {
    private long count;

    public long getCount() {
        return count;
    }

    public void setCount(long count) {
        this.count = count;
    }
}
