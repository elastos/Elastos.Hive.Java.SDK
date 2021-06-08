package org.elastos.hive.vault.database;

import com.google.gson.annotations.SerializedName;

public class InsertOptions {
	@SerializedName("bypass_document_validation")
	boolean bypassDocumentValidation;

	@SerializedName("ordered")
	boolean ordered;

	public InsertOptions() {
		this(false, false);
	}

	public InsertOptions(boolean bypassDocumentValidation) {
		this(bypassDocumentValidation, false);
	}

	public InsertOptions(boolean bypassDocumentValidation, boolean ordered) {
		this.bypassDocumentValidation = bypassDocumentValidation;
		this.ordered = ordered;
	}

	public InsertOptions bypassDocumentValidation(boolean bypassDocumentValidation) {
		this.bypassDocumentValidation = bypassDocumentValidation;
		return this;
	}

	public InsertOptions ordered(boolean value) {
		ordered = value;
		return this;
	}

	public Boolean bypassDocumentValidation() {
		return bypassDocumentValidation;
	}

	public Boolean ordered() {
		return ordered;
	}
}
