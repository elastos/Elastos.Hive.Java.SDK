package org.elastos.hive.vault.database;

public class DescSortItem extends SortItem {
    public DescSortItem(String key) {
        super(key, Order.DESCENDING);
    }
}
