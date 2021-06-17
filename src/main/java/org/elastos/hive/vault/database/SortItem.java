package org.elastos.hive.vault.database;

import java.util.ArrayList;

public class SortItem extends ArrayList<Object> {
    public enum Order {
        ASCENDING(1), DESCENDING(-1);

        private Integer value;

        Order(Integer value) {
            this.value = value;
        }

        public Integer getValue() {
            return value;
        }
    }

    public SortItem(String key, Order order) {
        super.add(key);
        super.add(order.getValue());
    }
}
