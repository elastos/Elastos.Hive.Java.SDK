package org.elastos.hive.vault.database;

public class CountOptions {
	private Long skip;
	private Long limit;
	private Long maxTimeMS;

	public CountOptions setSkip(Long skip) {
		this.skip = skip;
		return this;
	}

	public CountOptions setLimit(Long limit) {
		this.limit = limit;
		return this;
	}

	public CountOptions setMaxTimeMS(Long maxTimeMS) {
		this.maxTimeMS = maxTimeMS;
		return this;
	}
}
