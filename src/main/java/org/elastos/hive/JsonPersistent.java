package org.elastos.hive;

import org.json.JSONObject;

public interface JsonPersistent {
	JSONObject parseFrom() throws HiveException;
	void upateContent(JSONObject conetnt) throws HiveException;
}
