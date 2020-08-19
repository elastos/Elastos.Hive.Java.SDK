package org.elastos.hive.interfaces.scripting.executables.database;

import org.elastos.hive.interfaces.scripting.executables.Executable;
import org.json.JSONObject;

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
