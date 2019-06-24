package org.elastos.hive;

import org.json.simple.JSONObject;

public interface JsonPersistent {
	JSONObject parseFrom() throws HiveException;
	void upateContent(JSONObject conetnt) throws HiveException;
}
