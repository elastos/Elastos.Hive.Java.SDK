package org.elastos.hive.scripting.executables;

import org.json.JSONObject;

/**
 * Client side representation of back-end executables.
 * Executables are predefined, and are executed by the hive back-end when running vault scripts.
 * For example, Database.FindQuery will execute a mongo query and return a list of results.
 */
public abstract class Executable {
    public abstract JSONObject toJSON();
}
