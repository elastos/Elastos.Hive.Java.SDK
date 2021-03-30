package org.elastos.hive.database;

public class InsertOneResult {
	private boolean acknowledged;
	private String insertedId;

	public boolean isAcknowledged() {
		return acknowledged;
	}

	public InsertOneResult setAcknowledged(boolean acknowledged) {
		this.acknowledged = acknowledged;
		return this;
	}

	public String getInsertedId() {
		return insertedId;
	}

	public InsertOneResult setInsertedId(String insertedId) {
		this.insertedId = insertedId;
		return this;
	}
}
