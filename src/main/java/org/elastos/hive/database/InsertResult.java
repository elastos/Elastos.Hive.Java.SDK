package org.elastos.hive.database;

import java.util.Arrays;
import java.util.List;

public class InsertResult extends Result {
	public List<String> insertedIds() {
		String[] ids = (String[])get("inserted_ids");
		if(ids != null) {
			return Arrays.asList(ids);
		} else {
			String id = (String) get("inserted_id");
			return Arrays.asList(id);
		}
	}
}
