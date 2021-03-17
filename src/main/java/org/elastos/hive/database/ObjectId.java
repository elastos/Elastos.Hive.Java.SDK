package org.elastos.hive.database;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class ObjectId {
	@JsonProperty("$oid")
	private String id;

	@JsonCreator
	public ObjectId(@JsonProperty(value = "$oid", required = true) String id) {
		this.id = id;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}
}
