package org.elastos.hive.vault.database;

/**
 * AscendingSortItem presents the condition of the query operation.
 */
public class AscendingSortItem extends SortItem {
    public AscendingSortItem(String key) {
        super(key, Order.ASCENDING);
    }
}
