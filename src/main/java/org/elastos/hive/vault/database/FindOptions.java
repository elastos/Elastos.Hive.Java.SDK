package org.elastos.hive.vault.database;

public class FindOptions {
	private long skip;
	private long limit;

	public FindOptions setSkip(long skip) {
		this.skip = skip;
		return this;
	}

	public FindOptions setLimit(long limit) {
		this.limit = limit;
		return this;
	}

	public long getSkip() {
		return skip;
	}

	public long getLimit() {
		return limit;
	}
}
