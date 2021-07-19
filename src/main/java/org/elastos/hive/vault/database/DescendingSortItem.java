package org.elastos.hive.vault.database;

/**
 * DescendingSortItem presents the condition of the query operation.
 */
public class DescendingSortItem extends SortItem {
    public DescendingSortItem(String key) {
        super(key, Order.DESCENDING);
    }
}
