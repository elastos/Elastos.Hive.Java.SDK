package org.elastos.hive.vault.database;

import com.fasterxml.jackson.databind.JsonNode;
import com.google.gson.annotations.SerializedName;

/**
 * The request parameter for {@link DatabaseController#insertOne(String, JsonNode, InsertOptions)}
 */
public class InsertOptions {
	@SerializedName("bypass_document_validation")
	Boolean bypassDocumentValidation;

	@SerializedName("ordered")
	Boolean ordered;

	@SerializedName("timestamp")
	Boolean timestamp;

	public InsertOptions() {
		this(false, false);
	}

	public InsertOptions(Boolean bypassDocumentValidation) {
		this(bypassDocumentValidation, false);
	}

	public InsertOptions(Boolean bypassDocumentValidation, Boolean ordered) {
		this.bypassDocumentValidation = bypassDocumentValidation;
		this.ordered = ordered;
	}

	public InsertOptions bypassDocumentValidation(Boolean bypassDocumentValidation) {
		this.bypassDocumentValidation = bypassDocumentValidation;
		return this;
	}

	public InsertOptions ordered(Boolean value) {
		ordered = value;
		return this;
	}

	public InsertOptions timestamp(Boolean value) {
		timestamp = value;
		return this;
	}
}
