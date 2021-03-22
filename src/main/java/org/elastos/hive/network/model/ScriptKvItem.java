package org.elastos.hive.network.model;

import java.util.HashMap;

/**
 * Used for scripting service request body to keep unspecified key-values item.
 */
public class ScriptKvItem extends HashMap<String, Object> {
    public ScriptKvItem putKv(String key, String value) {
        super.put(key, value);
        return this;
    }
}
