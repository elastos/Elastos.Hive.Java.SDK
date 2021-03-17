package org.elastos.hive.database;

import com.fasterxml.jackson.annotation.JsonProperty;

public class WriteConcern {
	@JsonProperty("w")
	private Integer w;
	@JsonProperty("wtimeout")
	private Integer wtimeout;
	@JsonProperty("j")
	private Boolean j;
	@JsonProperty("fsync")
	private Boolean fsync;

	public WriteConcern(int w, int wtimeout, boolean j, boolean fsync) {
		w(w);
		wtimeout(wtimeout);
		j(j);
		fsync(fsync);
	}

	public WriteConcern() {
	}

	public WriteConcern w(int value) {
		w = value;
		return this;
	}

	public Integer w() {
		return w;
	}

	public WriteConcern wtimeout(int value) {
		wtimeout = value;
		return this;
	}

	public Integer wtimeout() {
		return wtimeout;
	}

	public WriteConcern j(boolean value) {
		j = value;
		return this;
	}

	public Boolean j() {
		return j;
	}

	public WriteConcern fsync(boolean value) {
		fsync = value;
		return this;
	}

	public Boolean fsync() {
		return fsync;
	}
}
