package org.elastos.hive.database;

import com.fasterxml.jackson.annotation.JsonProperty;

public class CreateCollectionOptions extends Options<CreateCollectionOptions> {
	@JsonProperty("write_concern")
	private WriteConcern writeConcern;
	@JsonProperty("read_concern")
	private ReadConcern readConcern;
	@JsonProperty("read_preference")
	private ReadPreference readPreference;
	@JsonProperty("capped")
	private Boolean capped;
	@JsonProperty("size")
	private Long size;
	@JsonProperty("max")
	private Integer max;
	@JsonProperty("collation")
	private Collation collation;

	public CreateCollectionOptions() {
	}

	public CreateCollectionOptions writeConcern(WriteConcern value) {
		writeConcern = value;
		return this;
	}

	public WriteConcern writeConcern() {
		return writeConcern;
	}

	public CreateCollectionOptions readConcern(ReadConcern value) {
		readConcern = value;
		return this;
	}

	public ReadConcern readConcern() {
		return readConcern;
	}

	public CreateCollectionOptions readPreference(ReadPreference value) {
		readPreference = value;
		return this;
	}

	public ReadPreference readPreference() {
		return readPreference;
	}

	public CreateCollectionOptions capped(boolean value) {
		capped = value;
		return this;
	}

	public Boolean capped() {
		return capped;
	}

	public CreateCollectionOptions size(long value) {
		size = value;
		return this;
	}

	public Long size() {
		return size;
	}

	public CreateCollectionOptions max(int value) {
		max = value;
		return this;
	}

	public Integer max() {
		return max;
	}

	public CreateCollectionOptions collation(Collation value) {
		collation = value;
		return this;
	}

	public Collation collation() {
		return collation;
	}

	public static CreateCollectionOptions deserialize(String content) {
		return deserialize(content, CreateCollectionOptions.class);
	}
}
