package org.elastos.hive.vault.database;

import com.google.gson.annotations.SerializedName;

public class UpdateOptions {
	@SerializedName("upsert")
	private boolean upsert;
	@SerializedName("bypass_document_validation")
	private boolean bypassDocumentValidation;

	public UpdateOptions setUpsert(boolean upsert) {
		this.upsert = upsert;
		return this;
	}

	public UpdateOptions setBypassDocumentValidation(boolean bypassDocumentValidation) {
		this.bypassDocumentValidation = bypassDocumentValidation;
		return this;
	}
}
