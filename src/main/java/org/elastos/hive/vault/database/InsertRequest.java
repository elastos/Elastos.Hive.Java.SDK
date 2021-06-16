package org.elastos.hive.vault.database;

import com.google.gson.annotations.SerializedName;
import org.elastos.hive.connection.KeyValueDict;

import java.util.List;

class InsertRequest {
	@SerializedName("document")
	private List<KeyValueDict> documents;
	@SerializedName("options")
	private InsertOptions options;

	public InsertRequest setDocuments(List<KeyValueDict> documents) {
		this.documents = documents;
		return this;
	}

	public InsertRequest setOptions(InsertOptions options) {
		this.options = options;
		return this;
	}
}
