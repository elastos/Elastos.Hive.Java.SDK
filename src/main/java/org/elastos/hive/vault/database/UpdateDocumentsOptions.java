package org.elastos.hive.vault.database;

import com.google.gson.annotations.SerializedName;

public class UpdateDocumentsOptions {
	@SerializedName("upsert")
	private boolean upsert;
	@SerializedName("bypass_document_validation")
	private boolean bypassDocumentValidation;

	public UpdateDocumentsOptions setUpsert(boolean upsert) {
		this.upsert = upsert;
		return this;
	}

	public UpdateDocumentsOptions setBypassDocumentValidation(boolean bypassDocumentValidation) {
		this.bypassDocumentValidation = bypassDocumentValidation;
		return this;
	}
}
