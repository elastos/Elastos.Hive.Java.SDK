package org.elastos.hive.database;

import com.fasterxml.jackson.annotation.JsonProperty;

public class InsertOptions extends Options<InsertOptions> {
	@JsonProperty("bypass_document_validation")
	private Boolean bypassDocumentValidation;
	@JsonProperty("ordered")
	private Boolean ordered;

	public InsertOptions(boolean bypassDocumentValidation, boolean ordered) {
		bypassDocumentValidation(bypassDocumentValidation);
		ordered(ordered);
	}

	public InsertOptions(boolean bypassDocumentValidation) {
		bypassDocumentValidation(bypassDocumentValidation);
	}

	public InsertOptions() {
	}

	public InsertOptions bypassDocumentValidation(boolean value) {
		bypassDocumentValidation = value;
		return this;
	}

	public Boolean bypassDocumentValidation() {
		return bypassDocumentValidation;
	}

	public InsertOptions ordered(boolean value) {
		ordered = value;
		return this;
	}

	public Boolean ordered() {
		return ordered;
	}

	public static InsertOptions deserialize(String content) {
		return deserialize(content, InsertOptions.class);
	}
}
