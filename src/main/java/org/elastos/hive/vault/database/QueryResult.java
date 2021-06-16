package org.elastos.hive.vault.database;

import com.google.gson.annotations.SerializedName;
import org.elastos.hive.connection.KeyValueDict;

import java.util.List;

class QueryResult {
	@SerializedName("items")
	private List<KeyValueDict> items;

	public List<KeyValueDict> getItems() {
		return this.items;
	}
}
