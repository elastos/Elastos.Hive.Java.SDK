package org.elastos.hive.database;

import java.util.List;

public class InsertManyResult {
	private boolean acknowledged;
	private List<String> insertedIds;

	public boolean isAcknowledged() {
		return acknowledged;
	}

	public InsertManyResult setAcknowledged(boolean acknowledged) {
		this.acknowledged = acknowledged;
		return this;
	}

	public List<String> getInsertedIds() {
		return insertedIds;
	}

	public InsertManyResult setInsertedIds(List<String> insertedIds) {
		this.insertedIds = insertedIds;
		return this;
	}
}
