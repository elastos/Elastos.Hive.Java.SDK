package org.elastos.hive.database;

public class UpdateResult {
	private int matchedCount;
	private int modifiedCount;
	private Boolean acknowledged;
	private String upsertedId;

	public UpdateResult setMatchedCount(int matchedCount) {
		this.matchedCount = matchedCount;
		return this;
	}

	public UpdateResult setModifiedCount(int modifiedCount) {
		this.modifiedCount = modifiedCount;
		return this;
	}

	public UpdateResult setAcknowledged(Boolean acknowledged) {
		this.acknowledged = acknowledged;
		return this;
	}

	public UpdateResult setUpsertedId(String upsertedId) {
		this.upsertedId = upsertedId;
		return this;
	}
}
