package org.elastos.hive.database;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;

public class CountOptions extends Options<CountOptions> {
	@JsonProperty("skip")
	private Long skip;
	@JsonProperty("limit")
	private Long limit;
	@JsonProperty("maxTimeMS")
	private Integer maxTimeMS;
	@JsonProperty("collation")
	private Collation collation;
	@JsonProperty("hint")
	@JsonFormat(with = {JsonFormat.Feature.ACCEPT_SINGLE_VALUE_AS_ARRAY,
            JsonFormat.Feature.WRITE_SINGLE_ELEM_ARRAYS_UNWRAPPED})
	private List<Index> hint;

	public CountOptions(long skip, long limit) {
		skip(skip);
		limit(limit);
	}

	public CountOptions() {
	}

	public CountOptions skip(long value) {
		skip = value;
		return this;
	}

	public Long skip() {
		return skip;
	}

	public CountOptions limit(long value) {
		limit = value;
		return this;
	}

	public Long limit() {
		return limit;
	}

	public CountOptions maxTimeMS(int value) {
		maxTimeMS = value;
		return this;
	}

	public Integer maxTimeMS() {
		return maxTimeMS;
	}

	public CountOptions collation(Collation value) {
		collation = value;
		return this;
	}

	public Collation collation() {
		return collation();
	}

	public CountOptions hint(Index value) {
		if (value == null) {
			hint = null;
		} else {
			if (hint == null)
				hint = new ArrayList<Index>();

			hint.add(value);
		}

		return this;
	}

	public CountOptions hint(List<Index> value) {
		if (value == null || value.isEmpty()) {
			hint = null;
		} else {
			if (hint == null)
				hint = new ArrayList<Index>();

			hint.addAll(value);
		}

		return this;
	}

	public CountOptions hint(Index[] value) {
		if (value == null || value.length == 0) {
			hint = null;
		} else {
			if (hint == null)
				hint = new ArrayList<Index>();

			hint.addAll(Arrays.asList(value));
		}

		return this;
	}

	public List<Index> hint() {
		return hint;
	}

	public static CountOptions deserialize(String content) {
		return deserialize(content, CountOptions.class);
	}
}
