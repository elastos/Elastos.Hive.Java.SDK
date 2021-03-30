package org.elastos.hive.database;

import com.google.gson.annotations.SerializedName;

import java.util.List;
import java.util.Map;

public class FindOptions {
	private Map<String, Object> projection;
	private Long skip;
	private Long limit;
	@SerializedName("no_cursor_timeout")
	private Boolean noCursorTimeout;
	private List<Index> sort;
	@SerializedName("allow_partial_results")
	private Boolean allowPartialResults;
	@SerializedName("batch_size")
	private Integer batchSize;
	private Collation collation;
	@SerializedName("return_key")
	private Boolean returnKey;
	private List<Index> hint;
	@SerializedName("max_time_ms")
	private Integer maxTimeMS;
	private Integer min;
	private Integer max;
	private String comment;
	@SerializedName("allow_disk_use")
	private Boolean allowDiskUse;

	public Map<String, Object> getProjection() {
		return projection;
	}

	public FindOptions setProjection(Map<String, Object> projection) {
		this.projection = projection;
		return this;
	}

	public Long getSkip() {
		return skip;
	}

	public FindOptions setSkip(Long skip) {
		this.skip = skip;
		return this;
	}

	public Long getLimit() {
		return limit;
	}

	public FindOptions setLimit(Long limit) {
		this.limit = limit;
		return this;
	}

	public Boolean getNoCursorTimeout() {
		return noCursorTimeout;
	}

	public FindOptions setNoCursorTimeout(Boolean noCursorTimeout) {
		this.noCursorTimeout = noCursorTimeout;
		return this;
	}

	public List<Index> getSort() {
		return sort;
	}

	public FindOptions setSort(List<Index> sort) {
		this.sort = sort;
		return this;
	}

	public Boolean getAllowPartialResults() {
		return allowPartialResults;
	}

	public FindOptions setAllowPartialResults(Boolean allowPartialResults) {
		this.allowPartialResults = allowPartialResults;
		return this;
	}

	public Integer getBatchSize() {
		return batchSize;
	}

	public FindOptions setBatchSize(Integer batchSize) {
		this.batchSize = batchSize;
		return this;
	}

	public Collation getCollation() {
		return collation;
	}

	public FindOptions setCollation(Collation collation) {
		this.collation = collation;
		return this;
	}

	public Boolean getReturnKey() {
		return returnKey;
	}

	public FindOptions setReturnKey(Boolean returnKey) {
		this.returnKey = returnKey;
		return this;
	}

	public List<Index> getHint() {
		return hint;
	}

	public FindOptions setHint(List<Index> hint) {
		this.hint = hint;
		return this;
	}

	public Integer getMaxTimeMS() {
		return maxTimeMS;
	}

	public FindOptions setMaxTimeMS(Integer maxTimeMS) {
		this.maxTimeMS = maxTimeMS;
		return this;
	}

	public Integer getMin() {
		return min;
	}

	public FindOptions setMin(Integer min) {
		this.min = min;
		return this;
	}

	public Integer getMax() {
		return max;
	}

	public FindOptions setMax(Integer max) {
		this.max = max;
		return this;
	}

	public String getComment() {
		return comment;
	}

	public FindOptions setComment(String comment) {
		this.comment = comment;
		return this;
	}

	public Boolean getAllowDiskUse() {
		return allowDiskUse;
	}

	public FindOptions setAllowDiskUse(Boolean allowDiskUse) {
		this.allowDiskUse = allowDiskUse;
		return this;
	}
}
