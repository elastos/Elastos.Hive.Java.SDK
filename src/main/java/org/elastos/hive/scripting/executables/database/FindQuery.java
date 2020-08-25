package org.elastos.hive.scripting.executables.database;

import org.elastos.hive.scripting.executables.Executable;
import org.json.JSONObject;

/**
 * Client side representation of a back-end execution that runs a mongo "find" query and returns some items
 * as a result.
 */
public class FindQuery extends Executable {
    private String collectionName;
    private JSONObject findQuery;

    public FindQuery(String collectionName) {
       this(collectionName, null);
    }

    public FindQuery(String collectionName, JSONObject findQuery) {
        this.collectionName = collectionName;
        this.findQuery = findQuery;
    }

    /**
     * {
     *      "type": "Database.FindQuery",
     *      "collectionName": "theCollectionToQuery",
     *      "queryParameters": {
     *          // Raw mongo query
     *      }
     * }
     */
    @Override
    public JSONObject toJSON() {
        JSONObject jsonExecutable = new JSONObject();

        jsonExecutable.put("type", "Database.FindQuery");
        jsonExecutable.put("collectionName", collectionName);
        jsonExecutable.put("queryParameters", findQuery);

        return jsonExecutable;
    }
}
