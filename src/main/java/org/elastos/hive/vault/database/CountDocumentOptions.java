package org.elastos.hive.vault.database;

public class CountDocumentOptions {
	private Long skip;
	private Long limit;
	private Long maxTimeMS;

	public CountDocumentOptions setSkip(Long skip) {
		this.skip = skip;
		return this;
	}

	public CountDocumentOptions setLimit(Long limit) {
		this.limit = limit;
		return this;
	}

	public CountDocumentOptions setMaxTimeMS(Long maxTimeMS) {
		this.maxTimeMS = maxTimeMS;
		return this;
	}
}
