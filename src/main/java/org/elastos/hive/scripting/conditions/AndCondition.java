package org.elastos.hive.scripting.conditions;

import org.json.JSONArray;
import org.json.JSONObject;

/**
 * Vault script condition that succeeds only if all the contained conditions are successful.
 */
public class AndCondition extends Condition {
    private Condition[] conditions;

    public AndCondition(Condition[] conditions) {
        this.conditions = conditions;
    }

    /**
     * {
     *      "type": "AndCondition",
     *      "conditions": [
     *          {
     *              "type": "OrCondition",
     *              ...
     *          },
     *          {
     *              "type": "SubCondition",
     *              ...
     *          },
     *          ...
     *      ]
     * }
     */
    @Override
    public JSONObject toJSON() {
        JSONObject jsonObject = new JSONObject();

        JSONArray jsonConditions = new JSONArray();
        for (Condition c : conditions) {
            jsonConditions.put(c.toJSON());
        }

        jsonObject.put("type", "AndCondition");
        jsonObject.put("conditions", jsonConditions);

        return jsonObject;
    }
}
