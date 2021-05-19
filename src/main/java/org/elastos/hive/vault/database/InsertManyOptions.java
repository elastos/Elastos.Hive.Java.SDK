package org.elastos.hive.vault.database;

public class InsertManyOptions extends InsertOneOptions {
	private final Boolean ordered;

	public InsertManyOptions(boolean bypassDocumentValidation, boolean ordered) {
		super(bypassDocumentValidation);
		this.ordered = ordered;
	}
}
