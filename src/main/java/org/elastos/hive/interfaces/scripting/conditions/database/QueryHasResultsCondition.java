package org.elastos.hive.interfaces.scripting.conditions.database;

import org.elastos.hive.interfaces.scripting.conditions.Condition;
import org.elastos.hive.interfaces.scripting.executables.Executable;
import org.json.JSONArray;
import org.json.JSONObject;

/**
 * Vault script condition to check if a database query returns results or not.
 * This is a way for example to check is a user is in a group, if a message contains comments, if a user
 * is in a list, etc.
 */
public class QueryHasResultsCondition extends Condition {
    private String collectionName;
    private JSONObject queryParameters;

    public QueryHasResultsCondition(String collectionName, JSONObject queryParameters) {
        this.collectionName = collectionName;
        this.queryParameters = queryParameters;
    }

    /**
     * {
     *      "type": "Database.QueryHasResultsCondition",
     *      "collectionName": "theCollectionToQueryName",
     *      "queryParameters": {
     *          // Raw mongo query arguments here
     *      }
     * }
     */
    @Override
    public JSONObject toJSON() {
        JSONObject jsonCondition = new JSONObject();

        jsonCondition.put("type", "Database.QueryHasResultsCondition");
        jsonCondition.put("collectionName", collectionName);
        jsonCondition.put("queryParameters", queryParameters);

        return jsonCondition;
    }
}
