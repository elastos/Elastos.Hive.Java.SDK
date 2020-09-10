package org.elastos.hive.database;

import java.util.Arrays;
import java.util.List;

public class InsertResult extends Result {

	public boolean acknowledged() {
		return (boolean) (get("acknowledged")==null?false:get("acknowledged"));
	}
	public List<String> insertedIds() {

		List<String> ids = (List<String>) (get("inserted_ids")==null?null:get("inserted_ids"));
		if(ids != null) {
			return ids;
		} else {
			String id = (String) (get("inserted_id")==null?null:get("inserted_id"));
			return Arrays.asList(id);
		}
	}
}
