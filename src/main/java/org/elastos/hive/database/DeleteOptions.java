package org.elastos.hive.database;

import java.util.List;

public class DeleteOptions {
	private Collation collation;
	private List<Index> hint;

	public DeleteOptions setCollation(Collation collation) {
		this.collation = collation;
		return this;
	}

	public DeleteOptions setHint(List<Index> hint) {
		this.hint = hint;
		return this;
	}

	public Collation getCollation() {
		return collation;
	}

	public List<Index> getHint() {
		return hint;
	}
}
