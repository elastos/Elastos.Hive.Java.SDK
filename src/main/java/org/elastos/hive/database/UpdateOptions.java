package org.elastos.hive.database;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class UpdateOptions {
	private Boolean upsert;
	@SerializedName("bypass_document_validation")
	private Boolean bypassDocumentValidation;
	private Collation collation;
	private List<Index> hint;

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

	public Collation getCollation() {
		return collation;
	}

	public UpdateOptions setCollation(Collation collation) {
		this.collation = collation;
		return this;
	}

	public List<Index> getHint() {
		return hint;
	}

	public UpdateOptions setHint(List<Index> hint) {
		this.hint = hint;
		return this;
	}
}
