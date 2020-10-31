package org.elastos.hive.database;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;

public class UpdateOptions extends Options<UpdateOptions> {
	@JsonProperty("upsert")
	private Boolean upsert;
	@JsonProperty("bypass_document_validation")
	private Boolean bypassDocumentValidation;
	@JsonProperty("collation")
	private Collation collation;
	@JsonProperty("hint")
	@JsonFormat(with = {JsonFormat.Feature.ACCEPT_SINGLE_VALUE_AS_ARRAY,
			JsonFormat.Feature.WRITE_SINGLE_ELEM_ARRAYS_UNWRAPPED})
	private List<Index> hint;


	public UpdateOptions() {}

	public UpdateOptions upsert(boolean value) {
		upsert = value;
		return this;
	}

	public Boolean upsert() {
		return upsert;
	}

	public UpdateOptions bypassDocumentValidation(boolean value) {
		bypassDocumentValidation = value;
		return this;
	}

	public Boolean bypassDocumentValidation() {
		return bypassDocumentValidation;
	}

	public UpdateOptions collation(Collation value) {
		collation = value;
		return this;
	}

	public Collation collation() {
		return collation();
	}

	public UpdateOptions hint(Index value) {
		if (value == null) {
			hint = null;
			return this;
		}

		if (hint == null)
			hint = new ArrayList<Index>();

		hint.add(value);
		return this;
	}

	public UpdateOptions hint(List<Index> value) {
		if (value == null || value.isEmpty()) {
			hint = null;
			return this;
		}

		if (hint == null)
			hint = new ArrayList<Index>();

		hint.addAll(value);
		return this;
	}

	public UpdateOptions hint(Index[] value) {
		if (value == null || value.length == 0) {
			hint = null;
			return this;
		}

		if (hint == null)
			hint = new ArrayList<Index>();

		hint.addAll(Arrays.asList(value));
		return this;
	}

	public List<Index> hint() {
		return hint;
	}

	public static UpdateOptions deserialize(String content) {
		return deserialize(content, UpdateOptions.class);
	}
}
