package org.elastos.hive.scripting.executables.database;

import org.elastos.hive.scripting.executables.Executable;
import org.json.JSONObject;

/**
 * Client side representation of a back-end execution that runs a mongo "insert" query.
 */
public class InsertQuery extends Executable {
    private String collectionName;
    private JSONObject insertQuery;

    public InsertQuery(String collectionName, JSONObject insertQuery) {
        this.collectionName = collectionName;
        this.insertQuery = insertQuery;
    }

    /**
     * {
     *      "type": "Database.InsertQuery",
     *      "collectionName": "theCollectionToInsertInto",
     *      "queryParameters": {
     *          // Raw mongo object to insert
     *      }
     * }
     */
    @Override
    public JSONObject toJSON() {
        JSONObject jsonExecutable = new JSONObject();

        jsonExecutable.put("type", "Database.InsertQuery");
        jsonExecutable.put("collectionName", collectionName);
        jsonExecutable.put("queryParameters", insertQuery);

        return jsonExecutable;
    }
}
