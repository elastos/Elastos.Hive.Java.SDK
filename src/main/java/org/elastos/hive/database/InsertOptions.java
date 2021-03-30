package org.elastos.hive.database;

import com.google.gson.annotations.SerializedName;

public class InsertOptions {
	@SerializedName("bypass_document_validation")
	private final Boolean bypassDocumentValidation;
	private final Boolean ordered;

	public InsertOptions(boolean bypassDocumentValidation, boolean ordered) {
		this.bypassDocumentValidation = bypassDocumentValidation;
		this.ordered = ordered;
	}
}
