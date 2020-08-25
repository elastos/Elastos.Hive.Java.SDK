package org.elastos.hive.scripting.conditions;

import org.json.JSONArray;
import org.json.JSONObject;

/**
 * Vault script condition that succeeds if at least one of the contained conditions are successful.
 * Contained conditions are tested in the given order, and test stops as soon as one successful condition
 * succeeds.
 */
public class OrCondition extends Condition {
    private Condition[] conditions;

    public OrCondition(Condition[] conditions) {
        this.conditions = conditions;
    }

    /**
     * {
     *      "type": "OrCondition",
     *      "conditions": [
     *          {
     *              "type": "AndCondition",
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

        jsonObject.put("type", "OrCondition");
        jsonObject.put("conditions", jsonConditions);

        return jsonObject;
    }
}
