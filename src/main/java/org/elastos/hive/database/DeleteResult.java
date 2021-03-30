package org.elastos.hive.database;

public class DeleteResult {
	private String acknowledged;
	private Integer deletedCount;

	public String getAcknowledged() {
		return acknowledged;
	}

	public DeleteResult setAcknowledged(String acknowledged) {
		this.acknowledged = acknowledged;
		return this;
	}

	public Integer getDeletedCount() {
		return deletedCount;
	}

	public DeleteResult setDeletedCount(Integer deletedCount) {
		this.deletedCount = deletedCount;
		return this;
	}
}
