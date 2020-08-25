package org.elastos.hive.database;

public class WriteConcern extends Options<WriteConcern> {
	private static final long serialVersionUID = -6482794443010772959L;

	public WriteConcern(int w, int wtimeout, boolean j, boolean fsync) {
		w(w);
		wtimeout(wtimeout);
		j(j);
		fsync(fsync);
	}

	public WriteConcern() {
	}

	public WriteConcern w(int value) {
		return setNumberOption("w", value);
	}

	public WriteConcern wtimeout(int value) {
		return setNumberOption("wtimeout", value);
	}

	public WriteConcern j(boolean value) {
		return setBooleanOption("j", value);
	}

	public WriteConcern fsync(boolean value) {
		return setBooleanOption("fsync", value);
	}
}
