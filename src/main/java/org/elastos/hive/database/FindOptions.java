package org.elastos.hive.database;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

public class FindOptions extends Options<FindOptions> {
	@JsonProperty("projection")
	private Map<String, Object> projection;
	@JsonProperty("skip")
	private Long skip;
	@JsonProperty("limit")
	private Long limit;
	@JsonProperty("no_cursor_timeout")
	private Boolean noCursorTimeout;
	@JsonProperty("sort")
	@JsonFormat(with = {JsonFormat.Feature.ACCEPT_SINGLE_VALUE_AS_ARRAY,
			JsonFormat.Feature.WRITE_SINGLE_ELEM_ARRAYS_UNWRAPPED})
	private List<Index> sort;
	@JsonProperty("allow_partial_results")
	private Boolean allowPartialResults;
	@JsonProperty("batch_size")
	private Integer batchSize;
	@JsonProperty("collation")
	private Collation collation;
	@JsonProperty("return_key")
	private Boolean returnKey;
	@JsonProperty("hint")
	@JsonFormat(with = {JsonFormat.Feature.ACCEPT_SINGLE_VALUE_AS_ARRAY,
			JsonFormat.Feature.WRITE_SINGLE_ELEM_ARRAYS_UNWRAPPED})
	private List<Index> hint;
	@JsonProperty("max_time_ms")
	private Integer maxTimeMS;
	@JsonProperty("min")
	private Integer min;
	@JsonProperty("max")
	private Integer max;
	@JsonProperty("comment")
	private String comment;
	@JsonProperty("allow_disk_use")
	private Boolean allowDiskUse;

	public FindOptions() {
	}

	public FindOptions projection(Map<String, Object> value) {
		if (value == null || value.isEmpty()) {
			projection = null;
			return this;
		}

		projection = new HashMap<String, Object>(value);
		return this;
	}

	public FindOptions projection(JsonNode value) {
		ObjectMapper mapper = new ObjectMapper();
		Map<String, Object> p = mapper.convertValue(value,
				new TypeReference<Map<String, Object>>() {});

		return projection(p);
	}

	public Map<String, Object> projection() {
		return projection;
	}

	public FindOptions skip(long value) {
		skip = value;
		return this;
	}

	public Long skip() {
		return skip;
	}

	public FindOptions limit(long value) {
		limit = value;
		return this;
	}

	public Long limit() {
		return limit;
	}

	public FindOptions noCursorTimeout(boolean value) {
		noCursorTimeout = value;
		return this;
	}

	public Boolean noCursorTimeout() {
		return noCursorTimeout;
	}

	public FindOptions sort(Index value) {
		if (value == null) {
			sort = null;
			return this;
		}

		if (sort == null)
			sort = new ArrayList<Index>();

		sort.add(value);
		return this;
	}

	public FindOptions sort(List<Index> value) {
		if (value == null || value.isEmpty()) {
			sort = null;
			return this;

		}

		if (sort == null)
			sort = new ArrayList<Index>();

		sort.addAll(value);
		return this;
	}

	public FindOptions sort(Index[] value) {
		if (value == null || value.length == 0) {
			sort = null;
			return this;
		}

		if (sort == null)
			sort = new ArrayList<Index>();

		sort.addAll(Arrays.asList(value));
		return this;
	}

	public List<Index> sort() {
		return sort;
	}

	public FindOptions allowPartialResults(boolean value) {
		allowPartialResults = value;
		return this;
	}

	public Boolean allowPartialResults() {
		return allowPartialResults;
	}

	public FindOptions batchSize(int value) {
		batchSize = value;
		return this;
	}

	public Integer batchSize() {
		return batchSize;
	}

	public FindOptions collation(Collation value) {
		collation = value;
		return this;
	}

	public Collation collation() {
		return collation;
	}

	public FindOptions returnKey(boolean value) {
		returnKey = value;
		return this;
	}

	public Boolean returnKey() {
		return returnKey;
	}

	public FindOptions hint(Index value) {
		if (value == null) {
			hint = null;
			return this;
		}
		if (hint == null)
			hint = new ArrayList<Index>();

		hint.add(value);
		return this;
	}

	public FindOptions hint(List<Index> value) {
		if (value == null || value.isEmpty()) {
			hint = null;
			return this;
		}

		if (hint == null)
			hint = new ArrayList<Index>();

		hint.addAll(value);
		return this;
	}

	public FindOptions hint(Index[] value) {
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


	public FindOptions maxTimeMS(int value) {
		maxTimeMS =  value;
		return this;
	}

	public Integer maxTimeMS() {
		return maxTimeMS;
	}

	public FindOptions min(int value) {
		min = value;
		return this;
	}

	public Integer min() {
		return min;
	}

	public FindOptions max(int value) {
		max = value;
		return this;
	}

	public Integer max() {
		return max;
	}

	public FindOptions comment(String value) {
		comment = value;
		return this;
	}

	public String comment() {
		return comment;
	}

	public FindOptions allowDiskUse(boolean value) {
		allowDiskUse = value;
		return this;
	}

	public Boolean allowDiskUse() {
		return allowDiskUse;
	}

	public static FindOptions deserialize(String content) {
		return deserialize(content, FindOptions.class);
	}
}
