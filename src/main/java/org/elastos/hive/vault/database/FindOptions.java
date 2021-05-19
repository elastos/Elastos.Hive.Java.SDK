package org.elastos.hive.vault.database;

import com.google.gson.annotations.SerializedName;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FindOptions {
	private Map<String, Object> projection;
	private Long skip;
	private List<FieldEntry> sort;
	@SerializedName("allow_partial_results")
	private Boolean allowPartialResults;
	@SerializedName("batch_size")
	private Integer batchSize;
	@SerializedName("return_key")
	private Boolean returnKey;
	@SerializedName("show_record_id")
	private Boolean showRecordId;

	public FindOptions setProjection(Map<String, Object> projection) {
		this.projection = projection;
		return this;
	}

	public FindOptions setSkip(Long skip) {
		this.skip = skip;
		return this;
	}

	public FindOptions setSort(List<FieldEntry> sort) {
		this.sort = sort;
		return this;
	}

	public FindOptions setAllowPartialResults(Boolean allowPartialResults) {
		this.allowPartialResults = allowPartialResults;
		return this;
	}

	public FindOptions setBatchSize(Integer batchSize) {
		this.batchSize = batchSize;
		return this;
	}

	public FindOptions setReturnKey(Boolean returnKey) {
		this.returnKey = returnKey;
		return this;
	}

	public FindOptions setShowRecordId(Boolean showRecordId) {
		this.showRecordId = showRecordId;
		return this;
	}

	public static class FieldEntry extends HashMap<String, Object> {
		public FieldEntry(String field, Object value) {
			super.put(field, value);
		}
	}
}
