package org.elastos.hive.vault.database;

import com.google.gson.annotations.SerializedName;
import org.elastos.hive.connection.KeyValueDict;

import java.util.Collections;
import java.util.List;

public class QueryOptions {
	private Integer skip;
	private Integer limit;
	private KeyValueDict projection;
	// ex: [('_id', -1)]
	private List<SortItem> sort;
	@SerializedName("allow_partial_results")
	private Boolean allowPartialResults;
	@SerializedName("return_key")
	private Boolean returnKey;
	@SerializedName("show_record_id")
	private Boolean showRecordId;
	@SerializedName("batch_size")
	private Integer batchSize;

	public QueryOptions setSkip(Integer skip) {
		this.skip = skip;
		return this;
	}

	public QueryOptions setLimit(Integer limit) {
		this.limit = limit;
		return this;
	}

	public QueryOptions setProjection(KeyValueDict projection) {
		this.projection = projection;
		return this;
	}

	public QueryOptions setSort(List<SortItem> sort) {
		this.sort = sort;
		return this;
	}

	public QueryOptions setSort(SortItem sort) {
		this.sort = Collections.singletonList(sort);
		return this;
	}

	public QueryOptions setAllowPartialResults(Boolean allowPartialResults) {
		this.allowPartialResults = allowPartialResults;
		return this;
	}

	public QueryOptions setReturnKey(Boolean returnKey) {
		this.returnKey = returnKey;
		return this;
	}

	public QueryOptions setShowRecordId(Boolean showRecordId) {
		this.showRecordId = showRecordId;
		return this;
	}

	public QueryOptions setBatchSize(Integer batchSize) {
		this.batchSize = batchSize;
		return this;
	}
}
