package org.elastos.hive.network.model;

import java.util.HashMap;

/**
 * Used for scripting service request body to keep unspecified key-values item.
 */
public class KeyValueDict extends HashMap<String, Object> {
    public KeyValueDict putKv(String key, Object value) {
        super.put(key, value);
        return this;
    }
}
