package org.elastos.hive.database;

import java.util.List;

public class CountOptions {
	private Long skip;
	private Long limit;
	private Integer maxTimeMS;
	private Collation collation;
	private List<Index> hint;

	public CountOptions setSkip(Long skip) {
		this.skip = skip;
		return this;
	}

	public CountOptions setLimit(Long limit) {
		this.limit = limit;
		return this;
	}

	public CountOptions setMaxTimeMS(Integer maxTimeMS) {
		this.maxTimeMS = maxTimeMS;
		return this;
	}

	public CountOptions setCollation(Collation collation) {
		this.collation = collation;
		return this;
	}

	public CountOptions setHint(List<Index> hint) {
		this.hint = hint;
		return this;
	}

}
