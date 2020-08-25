package org.elastos.hive.database;

import com.fasterxml.jackson.databind.JsonNode;

public class FindOptions extends Options<FindOptions> {
	private static final long serialVersionUID = 953451857979555741L;

	public FindOptions() {
	}

	public FindOptions projection(JsonNode value) {
		return setObjectOption("projection", value);
	}

	public FindOptions skip(long value) {
		return setNumberOption("skip", value);
	}

	public FindOptions limit(long value) {
		return setNumberOption("limit", value);
	}

	public FindOptions noCursorTimeout(boolean value) {
		return setBooleanOption("no_cursor_timeout", value);
	}

	public FindOptions sort(Index value) {
		return setObjectOption("sort", value);
	}

	public FindOptions sort(Index[] value) {
		return setArrayOption("sort", value);
	}

	public FindOptions allowPartialResults(boolean value) {
		return setBooleanOption("allow_partial_results", value);
	}

	public FindOptions batchSize(int value) {
		return setNumberOption("batch_size", value);
	}

	public FindOptions collation(Collation value) {
		return setObjectOption("collation", value);
	}

	public FindOptions returnKey(boolean value) {
		return setBooleanOption("return_key", value);
	}

	public FindOptions hint(Index value) {
		return setObjectOption("hint", value);
	}

	public FindOptions hint(Index[] value) {
		return setArrayOption("hint", value);
	}

	public FindOptions maxTimeMS(int value) {
		return setNumberOption("max_time_ms", value);
	}

	public FindOptions min(int value) {
		return setNumberOption("min", value);
	}

	public FindOptions max(int value) {
		return setNumberOption("max", value);
	}

	public FindOptions comment(String value) {
		return setStringOption("comment", value);
	}

	public FindOptions allowDiskUse(boolean value) {
		return setBooleanOption("allow_disk_use", value);
	}
}
