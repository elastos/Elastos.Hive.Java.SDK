package org.elastos.hive.vault.database;

import com.google.gson.annotations.SerializedName;

public class UpdateOptions {
	private Boolean upsert;
	@SerializedName("bypass_document_validation")
	private Boolean bypassDocumentValidation;

	public Boolean getUpsert() {
		return upsert;
	}

	public UpdateOptions setUpsert(Boolean upsert) {
		this.upsert = upsert;
		return this;
	}

	public Boolean getBypassDocumentValidation() {
		return bypassDocumentValidation;
	}

	public UpdateOptions setBypassDocumentValidation(Boolean bypassDocumentValidation) {
		this.bypassDocumentValidation = bypassDocumentValidation;
		return this;
	}
}
