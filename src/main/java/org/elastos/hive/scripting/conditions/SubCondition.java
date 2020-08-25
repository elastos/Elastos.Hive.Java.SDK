package org.elastos.hive.scripting.conditions;

import org.json.JSONObject;

/**
 * Represents a sub-condition execution, previously registered in the ACL manager.
 * This way, several scripts can rely on simply the sub-condition name, without rewriting the condition content itself.
 */
public class SubCondition extends Condition {
    private String name;

    public SubCondition(String name) {
        this.name = name;
    }

    /**
     * {
     *      "type": "SubCondition",
     *      "name": "theConditionName"
     * }
     */
    @Override
    public JSONObject toJSON() {
        JSONObject jsonObject = new JSONObject();

        jsonObject.put("type", "SubCondition");
        jsonObject.put("name", name);

        return jsonObject;
    }
}
