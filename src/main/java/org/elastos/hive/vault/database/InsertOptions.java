package org.elastos.hive.vault.database;

import com.google.gson.annotations.SerializedName;

public class InsertOptions {
	@SerializedName("bypass_document_validation")
	boolean bypassDocumentValidation;
	@SerializedName("ordered")
	boolean ordered;

	public InsertOptions setBypassDocumentValidation(boolean bypassDocumentValidation) {
		this.bypassDocumentValidation = bypassDocumentValidation;
		return this;
	}

	public InsertOptions setOrdered(boolean ordered) {
		this.ordered = ordered;
		return this;
	}
}
