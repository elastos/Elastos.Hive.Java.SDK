package org.elastos.hive.vault.scripting;

import com.google.gson.annotations.SerializedName;
import org.elastos.hive.connection.KeyValueDict;

/**
 * Client side representation of a back-end execution that runs a mongo "find" query and returns some items
 * as a result.
 */
public class FindExecutable extends Executable {
	public FindExecutable(String name, String collectionName, KeyValueDict filter, KeyValueDict options) {
		super(name, Type.FIND, null);
		super.setBody(new Body(collectionName, filter, options));
	}

	public FindExecutable(String name, String collectionName, KeyValueDict filter) {
		this(name, collectionName, filter, null);
	}

	private class Body extends DatabaseBody {
		@SerializedName("filter")
		private KeyValueDict filter;
		@SerializedName("options")
		private KeyValueDict options;

		public Body(String collection, KeyValueDict filter, KeyValueDict options) {
			super(collection);
			this.filter = filter;
			this.options = options;
		}
	}
}
