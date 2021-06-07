package org.elastos.hive.vault.database;

import com.google.gson.annotations.SerializedName;

public class InsertDocumentsOptions {
	@SerializedName("bypass_document_validation")
	boolean bypassDocumentValidation;
	@SerializedName("ordered")
	boolean ordered;

	public InsertDocumentsOptions setBypassDocumentValidation(boolean bypassDocumentValidation) {
		this.bypassDocumentValidation = bypassDocumentValidation;
		return this;
	}

	public InsertDocumentsOptions setOrdered(boolean ordered) {
		this.ordered = ordered;
		return this;
	}
}
