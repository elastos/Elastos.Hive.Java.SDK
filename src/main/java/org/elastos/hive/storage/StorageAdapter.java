package org.elastos.hive.storage;

import org.json.JSONObject;

public interface StorageAdapter {

	JSONObject restore();

	void store(JSONObject content);
	
}
