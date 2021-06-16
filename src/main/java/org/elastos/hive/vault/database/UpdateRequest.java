package org.elastos.hive.vault.database;

import org.elastos.hive.connection.KeyValueDict;

class UpdateRequest {
	private KeyValueDict filter;
	private KeyValueDict update;
	private UpdateOptions options;

	public UpdateRequest setFilter(KeyValueDict filter) {
		this.filter = filter;
		return this;
	}

	public UpdateRequest setUpdate(KeyValueDict update) {
		this.update = update;
		return this;
	}

	public UpdateRequest setOptions(UpdateOptions options) {
		this.options = options;
		return this;
	}
}
