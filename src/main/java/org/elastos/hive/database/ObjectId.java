package org.elastos.hive.database;

import com.fasterxml.jackson.annotation.JsonGetter;

public class ObjectId {
	private String id;

	public ObjectId(String id) {
		this.id = id;
	}

	@JsonGetter("$oid")
	public String getId() {
		return id;
	}
}
