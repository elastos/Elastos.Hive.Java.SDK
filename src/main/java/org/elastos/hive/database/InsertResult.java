package org.elastos.hive.database;

import java.util.Arrays;
import java.util.List;

public class InsertResult extends Result {
	public List<String> insertedIds() {
		String[] ids = (String[])get("insertedIds");

		return Arrays.asList(ids);
	}
}
